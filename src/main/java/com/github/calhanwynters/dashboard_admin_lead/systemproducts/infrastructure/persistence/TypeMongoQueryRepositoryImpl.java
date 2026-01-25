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
 * CQRS Read Repository for Product Types using MongoDB.
 * Optimized for 2026 admin dashboard performance with denormalized documents.
 */
public class TypeMongoQueryRepositoryImpl {

    private static final Logger logger = LoggerFactory.getLogger(TypeMongoQueryRepositoryImpl.class);
    private final MongoCollection<Document> collection;

    public TypeMongoQueryRepositoryImpl(MongoClient mongoClient) {
        // Targets the 'product_catalog_read' database and 'types' collection
        this.collection = mongoClient.getDatabase("product_catalog_read")
                .getCollection("types");
    }

    /**
     * Read Model Record: Represents the flat data structure optimized for UI display.
     */
    public record TypeReadModel(
            String typeUuid,
            String name,
            String compatibilityTag,
            String description,
            String careInstructions,
            PhysicalSpecs physicalSpecs,
            PricingInfo pricing
    ) {}

    public record PhysicalSpecs(double length, double width, double height, double weight) {}
    public record PricingInfo(double amount, String currency, String modelType) {}

    /**
     * Fetches a specific Type by its UUID.
     */
    public Optional<TypeReadModel> findByUuid(String typeUuid) {
        try {
            Document doc = collection.find(Filters.eq("_id", typeUuid)).first();
            return Optional.ofNullable(doc).map(this::mapToReadModel);
        } catch (Exception e) {
            logger.error("Error retrieving Type from MongoDB: {}", typeUuid, e);
            return Optional.empty();
        }
    }

    /**
     * Fetches all Types for a specific business, leveraging MongoDB indexes.
     */
    public List<TypeReadModel> findAllByBusiness(String businessId) {
        List<TypeReadModel> results = new ArrayList<>();
        try {
            collection.find(Filters.eq("business_id", businessId))
                    .forEach(doc -> results.add(mapToReadModel(doc)));
        } catch (Exception e) {
            logger.error("Error retrieving Types for business: {}", businessId, e);
        }
        return results;
    }

    /**
     * Maps a BSON Document to the immutable TypeReadModel.
     */
    private TypeReadModel mapToReadModel(Document doc) {
        Document specsDoc = doc.get("physical_specs", Document.class);
        Document priceDoc = doc.get("pricing", Document.class);

        PhysicalSpecs specs = (specsDoc == null) ? null : new PhysicalSpecs(
                specsDoc.getDouble("length"),
                specsDoc.getDouble("width"),
                specsDoc.getDouble("height"),
                specsDoc.getDouble("weight")
        );

        PricingInfo pricing = (priceDoc == null) ? null : new PricingInfo(
                priceDoc.getDouble("amount"),
                priceDoc.getString("currency"),
                priceDoc.getString("model_type")
        );

        return new TypeReadModel(
                doc.getString("_id"),
                doc.getString("name"),
                doc.getString("compatibility_tag"),
                doc.getString("description"),
                doc.getString("care_instructions"),
                specs,
                pricing
        );
    }
}
