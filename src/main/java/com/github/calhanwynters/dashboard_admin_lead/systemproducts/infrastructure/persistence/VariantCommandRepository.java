package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.Feature;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.VariantCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Set;

public class VariantCommandRepository {

    private static final Logger logger = LoggerFactory.getLogger(VariantCommandRepository.class);

    /**
     * Saves the VariantCollection snapshot.
     * Manages the collection record and the many-to-many links to individual Features.
     */
    public long save(VariantCollection collection) {
        String collectionSql = """
            INSERT INTO variant_collections (business_id, variant_col_uuid)
            VALUES (?::uuid, ?::uuid)
            ON CONFLICT (variant_col_uuid) DO UPDATE SET business_id = EXCLUDED.business_id
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long collectionId;
                // 1. Persist/Update the Aggregate Root
                try (PreparedStatement pstmt = conn.prepareStatement(collectionSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, collection.getBusinessId().value());
                    pstmt.setString(2, collection.getVariantColId().value());
                    pstmt.executeUpdate();
                    collectionId = getOrRetrieveCollectionId(conn, collection.getVariantColId().value());
                }

                // 2. Clear existing feature links (Snapshot Pattern)
                try (PreparedStatement delPstmt = conn.prepareStatement("DELETE FROM collection_features_link WHERE collection_id = ?")) {
                    delPstmt.setLong(1, collectionId);
                    delPstmt.executeUpdate();
                }

                // 3. Persist features and build links
                persistFeatureSnapshot(conn, collectionId, collection.getFeatures());

                conn.commit();
                return collectionId;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Failed to save VariantCollection snapshot: {}", collection.getVariantColId().value(), e);
                return -1;
            }
        } catch (SQLException e) {
            logger.error("Database connection failure in VariantCommandRepository", e);
            return -1;
        }
    }

    private void persistFeatureSnapshot(Connection conn, long collectionId, Set<Feature> features) throws SQLException {
        if (features == null || features.isEmpty()) return;

        // Upsert Feature details
        String upsertFeatureSql = """
            INSERT INTO features (feature_uuid, name, compatibility_tag, description)
            VALUES (?::uuid, ?, ?, ?)
            ON CONFLICT (feature_uuid) DO UPDATE SET
                name = EXCLUDED.name,
                description = EXCLUDED.description,
                compatibility_tag = EXCLUDED.compatibility_tag
            RETURNING id
            """;

        String linkSql = "INSERT INTO collection_features_link (collection_id, feature_id) VALUES (?, ?)";

        try (PreparedStatement featPstmt = conn.prepareStatement(upsertFeatureSql);
             PreparedStatement linkPstmt = conn.prepareStatement(linkSql)) {

            for (Feature feature : features) {
                featPstmt.setString(1, feature.featureUuId().value());
                featPstmt.setString(2, feature.featureName().value());
                featPstmt.setString(3, feature.compatibilityTag().value());
                featPstmt.setString(4, feature.featureDescription().text());

                try (ResultSet rs = featPstmt.executeQuery()) {
                    if (rs.next()) {
                        long internalFeatureId = rs.getLong(1);
                        linkPstmt.setLong(1, collectionId);
                        linkPstmt.setLong(2, internalFeatureId);
                        linkPstmt.addBatch();
                    }
                }
            }
            linkPstmt.executeBatch();
        }
    }

    public boolean softDelete(String variantColUuid) {
        String sql = "UPDATE variant_collections SET deleted_at = NOW() WHERE variant_col_uuid = ?::uuid AND deleted_at IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, variantColUuid);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Soft delete failed for VariantCollection: {}", variantColUuid, e);
            return false;
        }
    }

    public boolean hardDelete(String variantColUuid) {
        String deleteLinks = "DELETE FROM collection_features_link WHERE collection_id = (SELECT id FROM variant_collections WHERE variant_col_uuid = ?::uuid)";
        String deleteColl = "DELETE FROM variant_collections WHERE variant_col_uuid = ?::uuid";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement lp = conn.prepareStatement(deleteLinks);
                 PreparedStatement cp = conn.prepareStatement(deleteColl)) {

                lp.setString(1, variantColUuid);
                lp.executeUpdate();

                cp.setString(1, variantColUuid);
                int deleted = cp.executeUpdate();

                conn.commit();
                return deleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Hard delete failed for VariantCollection: {}", variantColUuid, e);
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private long getOrRetrieveCollectionId(Connection conn, String uuid) throws SQLException {
        String sql = "SELECT id FROM variant_collections WHERE variant_col_uuid = ?::uuid AND deleted_at IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                throw new SQLException("VariantCollection not found or deleted: " + uuid);
            }
        }
    }
}
