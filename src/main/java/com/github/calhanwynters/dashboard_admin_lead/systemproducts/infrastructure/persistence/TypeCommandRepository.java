package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type.Type;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type.TypeCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Set;

public class TypeCommandRepository {

    private static final Logger logger = LoggerFactory.getLogger(TypeCommandRepository.class);

    /**
     * Saves the entire TypeCollection snapshot.
     * Handles both the collection record and the many-to-many links to individual Types.
     */
    public long save(TypeCollection collection) {
        String collectionSql = """
            INSERT INTO type_collections (business_id, type_col_uuid)
            VALUES (?::uuid, ?::uuid)
            ON CONFLICT (type_col_uuid) DO UPDATE SET business_id = EXCLUDED.business_id
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long collectionId;
                // 1. Persist/Update the Collection Aggregate Root
                try (PreparedStatement pstmt = conn.prepareStatement(collectionSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, collection.getBusinessId().value());
                    pstmt.setString(2, collection.getTypeColId().value());
                    pstmt.executeUpdate();
                    collectionId = getOrRetrieveCollectionId(conn, collection.getTypeColId().value());
                }

                // 2. Clear existing links (Snapshot Pattern)
                try (PreparedStatement delPstmt = conn.prepareStatement("DELETE FROM collection_types_link WHERE collection_id = ?")) {
                    delPstmt.setLong(1, collectionId);
                    delPstmt.executeUpdate();
                }

                // 3. Persist individual Types and link them
                persistTypeSnapshot(conn, collectionId, collection.getTypes());

                conn.commit();
                return collectionId;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Failed to save TypeCollection snapshot for UUID: {}", collection.getTypeColId().value(), e);
                return -1;
            }
        } catch (SQLException e) {
            logger.error("Database connection failure in TypeCommandRepository", e);
            return -1;
        }
    }

    private void persistTypeSnapshot(Connection conn, long collectionId, Set<Type> types) throws SQLException {
        if (types == null || types.isEmpty()) return;

        // Upsert the Type details (identified by its unique typeId/UUID)
        String upsertTypeSql = """
            INSERT INTO types (type_uuid, name, compatibility_tag, description, care_instructions, length, width, height, weight)
            VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (type_uuid) DO UPDATE SET
                name = EXCLUDED.name,
                description = EXCLUDED.description,
                care_instructions = EXCLUDED.care_instructions
            RETURNING id
            """;

        String linkSql = "INSERT INTO collection_types_link (collection_id, type_id) VALUES (?, ?)";

        try (PreparedStatement typePstmt = conn.prepareStatement(upsertTypeSql);
             PreparedStatement linkPstmt = conn.prepareStatement(linkSql)) {

            for (Type type : types) {
                typePstmt.setString(1, type.typeId().value());
                typePstmt.setString(2, type.typeName().value());
                typePstmt.setString(3, type.compatibilityTag().value());
                typePstmt.setString(4, type.typeDescription().text());
                typePstmt.setString(5, type.typeCareInstruction().instructions());

                // Handling nullable Dimensions/Weight
                if (type.typeDimensions() != null) {
                    typePstmt.setBigDecimal(6, type.typeDimensions().length());
                    typePstmt.setBigDecimal(7, type.typeDimensions().width());
                    typePstmt.setBigDecimal(8, type.typeDimensions().height());
                } else {
                    typePstmt.setNull(6, Types.DECIMAL);
                    typePstmt.setNull(7, Types.DECIMAL);
                    typePstmt.setNull(8, Types.DECIMAL);
                }

                if (type.typeWeight() != null) {
                    typePstmt.setBigDecimal(9, type.typeWeight().amount());
                } else {
                    typePstmt.setNull(9, Types.DECIMAL);
                }

                try (ResultSet rs = typePstmt.executeQuery()) {
                    if (rs.next()) {
                        long internalTypeId = rs.getLong(1);
                        linkPstmt.setLong(1, collectionId);
                        linkPstmt.setLong(2, internalTypeId);
                        linkPstmt.addBatch();
                    }
                }
            }
            linkPstmt.executeBatch();
        }
    }

    public boolean softDelete(String typeColUuid) {
        String sql = "UPDATE type_collections SET deleted_at = NOW() WHERE type_col_uuid = ?::uuid AND deleted_at IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeColUuid);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Soft delete failed for TypeCollection: {}", typeColUuid, e);
            return false;
        }
    }

    public boolean hardDelete(String typeColUuid) {
        String deleteLinks = "DELETE FROM collection_types_link WHERE collection_id = (SELECT id FROM type_collections WHERE type_col_uuid = ?::uuid)";
        String deleteColl = "DELETE FROM type_collections WHERE type_col_uuid = ?::uuid";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement lp = conn.prepareStatement(deleteLinks);
                 PreparedStatement cp = conn.prepareStatement(deleteColl)) {

                lp.setString(1, typeColUuid);
                lp.executeUpdate();

                cp.setString(1, typeColUuid);
                int deleted = cp.executeUpdate();

                conn.commit();
                return deleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Hard delete failed for TypeCollection: {}", typeColUuid, e);
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private long getOrRetrieveCollectionId(Connection conn, String uuid) throws SQLException {
        String sql = "SELECT id FROM type_collections WHERE type_col_uuid = ?::uuid AND deleted_at IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                throw new SQLException("TypeCollection not found or deleted: " + uuid);
            }
        }
    }
}
