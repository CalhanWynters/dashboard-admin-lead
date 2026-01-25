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
 * CQRS Read Repository for Variants and Features using MongoDB.
 * Optimized for 2026 Admin Dashboards to handle selection logic and compatibility.
 */
public class VariantMongoQueryRepositoryImpl {

    private static final Logger logger = LoggerFactory.getLogger(VariantMongoQueryRepositoryImpl.class);
    private final MongoCollection<Document> collection;

    public VariantMongoQueryRepositoryImpl(MongoClient mongoClient) {
        // Targets the 'product_catalog_read' database and 'variants' collection
        this.collection = mongoClient.getDatabase("product_catalog_read")
                .getCollection("variants");
    }

    /**
     * Read Model for Features.
     * Includes embedded incompatibility rules for instant UI feedback.
     */
    public record FeatureReadModel(
            String featureUuid,
            String name,
            String compatibilityTag,
            String description,
            List<String> forbiddenFeatureUuids // Pre-calculated list for UI 'gray-out' logic
    ) {}

    public record VariantCollectionReadModel(
            String variantColUuid,
            String businessId,
            List<FeatureReadModel> features
    ) {}

    /**
     * Fetches the entire variant collection by UUID.
     */
    public Optional<VariantCollectionReadModel> findByUuid(String variantColUuid) {
        try {
            Document doc = collection.find(Filters.eq("_id", variantColUuid)).first();
            return Optional.ofNullable(doc).map(this::mapToReadModel);
        } catch (Exception e) {
            logger.error("Error retrieving Variant Collection from MongoDB: {}", variantColUuid, e);
            return Optional.empty();
        }
    }

    /**
     * Retrieves all variant collections for a specific business.
     */
    public List<VariantCollectionReadModel> findAllByBusiness(String businessId) {
        List<VariantCollectionReadModel> results = new ArrayList<>();
        try {
            collection.find(Filters.eq("business_id", businessId))
                    .forEach(doc -> results.add(mapToReadModel(doc)));
        } catch (Exception e) {
            logger.error("Error retrieving Variant Collections for business: {}", businessId, e);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private VariantCollectionReadModel mapToReadModel(Document doc) {
        List<Document> featureDocs = (List<Document>) doc.get("features");
        List<FeatureReadModel> features = new ArrayList<>();

        if (featureDocs != null) {
            for (Document fDoc : featureDocs) {
                features.add(new FeatureReadModel(
                        fDoc.getString("feature_uuid"),
                        fDoc.getString("name"),
                        fDoc.getString("compatibility_tag"),
                        fDoc.getString("description"),
                        (List<String>) fDoc.get("forbidden_feature_uuids")
                ));
            }
        }

        return new VariantCollectionReadModel(
                doc.getString("_id"),
                doc.getString("business_id"),
                features
        );
    }
}
