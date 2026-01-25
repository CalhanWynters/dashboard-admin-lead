package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money.PriceFixedPurchase;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money.PriceNonePurchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ProductCommandRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProductCommandRepository.class);

    /**
     * Saves the Product Aggregate.
     * Implements Optimistic Locking using the 'version' field and handles the XOR logic
     * for Bespoke vs. Type-based attributes.
     */
    public boolean save(ProductAggregateRoot product) {
        String sql = """
            INSERT INTO products (
                product_uuid, business_id, name, category, description, status,
                version, gallery_col_uuid, type_col_uuid, variant_col_uuid,
                length, width, height, weight, care_instructions,
                price_amount, currency, created_at, updated_at
            ) VALUES (
                ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?::uuid, ?::uuid, ?::uuid,
                ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            ON CONFLICT (product_uuid) DO UPDATE SET
                name = EXCLUDED.name,
                category = EXCLUDED.category,
                description = EXCLUDED.description,
                status = EXCLUDED.status,
                version = products.version + 1,
                gallery_col_uuid = EXCLUDED.gallery_col_uuid,
                type_col_uuid = EXCLUDED.type_col_uuid,
                variant_col_uuid = EXCLUDED.variant_col_uuid,
                length = EXCLUDED.length,
                width = EXCLUDED.width,
                height = EXCLUDED.height,
                weight = EXCLUDED.weight,
                care_instructions = EXCLUDED.care_instructions,
                price_amount = EXCLUDED.price_amount,
                currency = EXCLUDED.currency,
                updated_at = NOW()
            WHERE products.version = ? -- Optimistic Locking Check
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Identity & Core
            pstmt.setString(1, product.getProductUuId().value());
            pstmt.setString(2, product.getBusinessId().value());
            pstmt.setString(3, product.getProductName().value());
            pstmt.setString(4, product.getProductCategory().toString());
            pstmt.setString(5, product.getProductDesc().text());
            pstmt.setString(6, product.getStatus().name());
            pstmt.setLong(7, product.getVersion().value());

            // Aggregate References
            pstmt.setString(8, product.getGalleryColId().value());
            pstmt.setString(9, product.getTypeColId().map(UuId::value).orElse(null));
            pstmt.setString(10, product.getVariantColId().map(UuId::value).orElse(null));

            // Bespoke Attributes (XOR Logic handled by presence/absence of TypeColId)
            if (product.getTypeColId().isEmpty()) {
                product.getProductDimensions().ifPresentOrElse(d -> {
                    try {
                        pstmt.setBigDecimal(11, d.length());
                        pstmt.setBigDecimal(12, d.width());
                        pstmt.setBigDecimal(13, d.height());
                    } catch (SQLException e) { throw new RuntimeException(e); }
                }, () -> setNullDimensions(pstmt));

                product.getProductWeight().ifPresentOrElse(w -> {
                    try { pstmt.setBigDecimal(14, w.amount()); } catch (SQLException e) { throw new RuntimeException(e); }
                }, () -> { try { pstmt.setNull(14, Types.DECIMAL); } catch (SQLException e) { throw new RuntimeException(e); }});

                product.getProductCareInstruction().ifPresentOrElse(c -> {
                    try { pstmt.setString(15, c.instructions()); } catch (SQLException e) { throw new RuntimeException(e); }
                }, () -> { try { pstmt.setNull(15, Types.VARCHAR); } catch (SQLException e) { throw new RuntimeException(e); }});

                product.getProductPricing().ifPresentOrElse(p -> {
                    try {
                        // Use pattern matching to extract price if it's a Fixed model
                        if (p instanceof PriceFixedPurchase(
                                com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money.Money fixedPrice
                        )) {
                            pstmt.setBigDecimal(16, fixedPrice.amount());
                            pstmt.setString(17, fixedPrice.currency().getCurrencyCode());
                        } else if (p instanceof PriceNonePurchase(java.util.Currency currency)) {
                            // Price is zero, but we still have a currency context
                            pstmt.setBigDecimal(16, java.math.BigDecimal.ZERO);
                            pstmt.setString(17, currency.getCurrencyCode());
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, () -> {
                    try {
                        pstmt.setNull(16, Types.DECIMAL);
                        pstmt.setNull(17, Types.VARCHAR);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                // Type-based product: Set all bespoke fields to NULL
                setNullBespokeFields(pstmt);
            }

            // Audit
            pstmt.setObject(18, product.getAudit().createdAt().value());
            pstmt.setObject(19, product.getAudit().lastModified().value());

            // Final param: The version check for the WHERE clause (Update only)
            pstmt.setLong(20, product.getVersion().value());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0 && isExisting(product.getProductUuId().value())) {
                throw new SQLException("Optimistic locking failure: Product version mismatch.");
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Failed to save product aggregate: {}", product.getProductUuId().value(), e);
            return false;
        }
    }

    public boolean softDelete(String productUuid) {
        String sql = "UPDATE products SET status = 'DELETED', updated_at = NOW() WHERE product_uuid = ?::uuid AND status != 'DELETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productUuid);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Soft delete failed for Product: {}", productUuid, e);
            return false;
        }
    }

    public boolean hardDelete(String productUuid) {
        String sql = "DELETE FROM products WHERE product_uuid = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productUuid);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Hard delete failed for Product: {}", productUuid, e);
            return false;
        }
    }

    private boolean isExisting(String uuid) throws SQLException {
        String sql = "SELECT 1 FROM products WHERE product_uuid = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) { return rs.next(); }
        }
    }

    private void setNullDimensions(PreparedStatement pstmt) {
        try {
            pstmt.setNull(11, Types.DECIMAL);
            pstmt.setNull(12, Types.DECIMAL);
            pstmt.setNull(13, Types.DECIMAL);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void setNullBespokeFields(PreparedStatement pstmt) throws SQLException {
        setNullDimensions(pstmt);
        pstmt.setNull(14, Types.DECIMAL); // weight
        pstmt.setNull(15, Types.VARCHAR); // care
        pstmt.setNull(16, Types.DECIMAL); // price
        pstmt.setNull(17, Types.VARCHAR); // currency
    }
}
