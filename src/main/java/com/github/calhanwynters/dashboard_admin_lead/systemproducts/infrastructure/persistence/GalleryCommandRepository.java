package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.ImageUrl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;

public class GalleryCommandRepository {

    private static final Logger logger = LoggerFactory.getLogger(GalleryCommandRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public long save(GalleryCollection collection) {
        String collectionSql = """
        INSERT INTO gallery_collections (business_id, gallery_uuid)
        VALUES (?::uuid, ?::uuid)
        ON CONFLICT (gallery_uuid) DO UPDATE SET business_id = EXCLUDED.business_id
        """;
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long galleryId;
                try (PreparedStatement pstmt = conn.prepareStatement(collectionSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, collection.getBusinessId().value());
                    pstmt.setString(2, collection.getGalleryColId().value());
                    pstmt.executeUpdate();
                    galleryId = getOrRetrieveGalleryId(conn, collection.getGalleryColId().value());
                }

                // Clear relationships for the snapshot
                try (PreparedStatement delPstmt = conn.prepareStatement("DELETE FROM gallery_images WHERE gallery_id = ?")) {
                    delPstmt.setLong(1, galleryId);
                    delPstmt.executeUpdate();
                }

                // Unified persist call
                persistImageSnapshot(conn, galleryId, collection.getImageUrls());

                conn.commit();
                return galleryId;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Snapshot save failed for Gallery UUID: {}", collection.getGalleryColId().value(), e);
                return -1;
            }
        } catch (SQLException e) {
            logger.error("Database connection failure", e);
            return -1;
        }
    }

    /**
     * Optimized: Replaces individual SELECTs with an atomic 'Upsert and Get ID' pattern.
     */
    private void persistImageSnapshot(Connection conn, long galleryId, Collection<ImageUrl> images) throws SQLException {
        if (images == null || images.isEmpty()) return;

        // Atomic Upsert + Select ID
        String getOrCreateSql = "INSERT INTO images (url) VALUES (?) ON CONFLICT (url) DO UPDATE SET url = EXCLUDED.url RETURNING id";
        String linkSql = "INSERT INTO gallery_images (gallery_id, image_id) VALUES (?, ?)";

        try (PreparedStatement imagePstmt = conn.prepareStatement(getOrCreateSql);
             PreparedStatement linkPstmt = conn.prepareStatement(linkSql)) {

            for (ImageUrl img : images) {
                // 1. Ensure image exists and get ID in one round trip
                imagePstmt.setString(1, img.url());
                try (ResultSet rs = imagePstmt.executeQuery()) {
                    if (rs.next()) {
                        long imageId = rs.getLong(1);
                        // 2. Batch the relationship links
                        linkPstmt.setLong(1, galleryId);
                        linkPstmt.setLong(2, imageId);
                        linkPstmt.addBatch();
                    }
                }
            }
            linkPstmt.executeBatch();
        }
    }

    // --- Standalone Image CRUD (Kept for master library management) ---

    public long createGlobalImage(ImageUrl imageUrl) {
        // Redundancy removed: This logic is now shared via the internal flow logic if needed,
        // but kept public for standalone library entries.
        String sql = "INSERT INTO images (url) VALUES (?) ON CONFLICT (url) DO UPDATE SET url = EXCLUDED.url RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, imageUrl.url());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Failed to create global image asset: {}", imageUrl.url(), e);
        }
        return -1;
    }

    public boolean updateGlobalImageUrl(long imageId, ImageUrl newImageUrl) {
        String sql = "UPDATE images SET url = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newImageUrl.url());
            pstmt.setLong(2, imageId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update global image asset ID: {}", imageId, e);
            return false;
        }
    }

    public boolean deleteGlobalImage(long imageId) {
        String sql = "DELETE FROM images WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, imageId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Critical failure deleting global image asset ID: {}", imageId, e);
            return false;
        }
    }

    // --- Private Helpers ---

    private long getOrRetrieveGalleryId(Connection conn, String uuid) throws SQLException {
        // Added 'AND deleted_at IS NULL' to prevent returning 'deleted' records
        String sql = "SELECT id FROM gallery_collections WHERE gallery_uuid = ?::uuid AND deleted_at IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                throw new SQLException("Active gallery not found for UUID: " + uuid);
            }
        }
    }

    public String serializeToJson(GalleryCollection collection) {
        try {
            return objectMapper.writeValueAsString(collection);
        } catch (Exception e) {
            logger.warn("JSON Serialization failed for Gallery: {}", collection.getGalleryColId().value(), e);
            return "{}";
        }
    }

    /**
     * Updated: Soft delete a gallery by setting its deleted_at timestamp.
     */
    public boolean softDeleteGallery(String galleryUuid) {
        String sql = "UPDATE gallery_collections SET deleted_at = NOW() WHERE gallery_uuid = ?::uuid AND deleted_at IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, galleryUuid);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to soft delete gallery: {}", galleryUuid, e);
            return false;
        }
    }

    /**
     * Hard deletes a gallery and its image associations.
     * Uses a transaction to ensure both the links and the gallery record are removed.
     */
    public boolean hardDeleteGallery(String galleryUuid) {
        String deleteLinksSql = """
        DELETE FROM gallery_images
        WHERE gallery_id = (SELECT id FROM gallery_collections WHERE gallery_uuid = ?::uuid)
        """;
        String deleteGallerySql = "DELETE FROM gallery_collections WHERE gallery_uuid = ?::uuid";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement linksPstmt = conn.prepareStatement(deleteLinksSql);
                 PreparedStatement galleryPstmt = conn.prepareStatement(deleteGallerySql)) {

                // 1. Delete relationship links
                linksPstmt.setString(1, galleryUuid);
                linksPstmt.executeUpdate();

                // 2. Delete the gallery record
                galleryPstmt.setString(1, galleryUuid);
                int rowsAffected = galleryPstmt.executeUpdate();

                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Transaction failed: Hard delete for gallery UUID: {}", galleryUuid, e);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Database connection failure during hard delete", e);
            return false;
        }
    }

    /**
     * Updated: Soft delete an image.
     */
    public boolean softDeleteImage(long imageId) {
        String sql = "UPDATE images SET deleted_at = NOW() WHERE id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, imageId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to soft delete image ID: {}", imageId, e);
            return false;
        }
    }

    /**
     * Restore a soft-deleted gallery.
     */
    public boolean restoreGallery(String galleryUuid) {
        String sql = "UPDATE gallery_collections SET deleted_at = NULL WHERE gallery_uuid = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, galleryUuid);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to restore gallery: {}", galleryUuid, e);
            return false;
        }
    }
}
