package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CQRS Read Repository for Products using MongoDB.
 * Flattens Aggregates (Product + Type + Gallery) into a single UI-ready document.
 */
public class ProductMongoQueryRepositoryImpl {

    private static final Logger logger = LoggerFactory.getLogger(ProductMongoQueryRepositoryImpl.class);
    private final MongoCollection<Document> collection;

    public ProductMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_catalog_read")
                .getCollection("products");
    }

    /**
     * Comprehensive Read Model for the Admin Dashboard.
     */
    public record ProductReadModel(
            String productUuid,
            String businessId,
            String name,
            String category,
            String description,
            String status,
            long version,
            DisplaySpecs specs,      // Pre-flattened: either bespoke or inherited from Type
            List<String> imageUrls,  // Pulled from the linked Gallery
            String typeColUuid,      // Null if bespoke
            String variantColUuid,   // Reference to the options
            AuditInfo audit
    ) {}

    public record DisplaySpecs(
            double length, double width, double height, double weight,
            String careInstructions, double price, String currency
    ) {}

    public record AuditInfo(String createdAt, String lastModified) {}

    /**
     * Fetches a complete product view by UUID.
     */
    public Optional<ProductReadModel> findByUuid(String productUuid) {
        try {
            Document doc = collection.find(Filters.eq("_id", productUuid)).first();
            return Optional.ofNullable(doc).map(this::mapToReadModel);
        } catch (Exception e) {
            logger.error("Failed to fetch product document: {}", productUuid, e);
            return Optional.empty();
        }
    }

    /**
     * Fetches the dashboard list for a business.
     */
    public List<ProductReadModel> findAllByBusiness(String businessId) {
        List<ProductReadModel> results = new ArrayList<>();
        try {
            collection.find(Filters.eq("business_id", businessId))
                    .forEach(doc -> results.add(mapToReadModel(doc)));
        } catch (Exception e) {
            logger.error("Failed to fetch products for business: {}", businessId, e);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private ProductReadModel mapToReadModel(Document doc) {
        Document specsDoc = doc.get("display_specs", Document.class);
        Document auditDoc = doc.get("audit", Document.class);

        DisplaySpecs specs = (specsDoc == null) ? null : new DisplaySpecs(
                specsDoc.getDouble("length"),
                specsDoc.getDouble("width"),
                specsDoc.getDouble("height"),
                specsDoc.getDouble("weight"),
                specsDoc.getString("care_instructions"),
                specsDoc.getDouble("price"),
                specsDoc.getString("currency")
        );

        AuditInfo audit = (auditDoc == null) ? null : new AuditInfo(
                auditDoc.getString("created_at"),
                auditDoc.getString("last_modified")
        );

        return new ProductReadModel(
                doc.getString("_id"),
                doc.getString("business_id"),
                doc.getString("name"),
                doc.getString("category"),
                doc.getString("description"),
                doc.getString("status"),
                doc.getLong("version"),
                specs,
                (List<String>) doc.get("image_urls"),
                doc.getString("type_col_uuid"),
                doc.getString("variant_col_uuid"),
                audit
        );
    }
}
