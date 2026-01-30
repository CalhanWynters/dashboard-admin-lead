package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ProductQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductFactory;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * PURE READ-SIDE: MongoDB Implementation for Product Aggregates.
 * Updated 2026: Implements version tracking and reconstitution alignment.
 */
public class ProductMongoQueryRepositoryImpl implements ProductQueryRepository {

    private final MongoCollection<Document> collection;

    public ProductMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_db")
                .getCollection("products");
    }

    /**
     * Implements method for Optimistic Locking checks.
     */
    @Override
    public OffsetDateTime getLatestTimestamp(UuId productUuId) {
        Document doc = collection.find(Filters.eq("productUuId", productUuId.value()))
                .projection(Projections.include("audit.updatedAt"))
                .first();

        if (doc == null) return null;

        Document audit = doc.get("audit", Document.class);
        // Added safety check for the audit sub-document
        if (audit == null || audit.getDate("updatedAt") == null) return null;

        return audit.getDate("updatedAt").toInstant().atOffset(ZoneOffset.UTC);
    }



    @Override
    public Optional<ProductAggregateRoot> findById(UuId productUuId) {
        Document doc = collection.find(Filters.eq("productUuId", productUuId.value())).first();
        return Optional.ofNullable(doc).map(this::mapToAggregate);
    }

    @Override
    public List<ProductAggregateRoot> findAllByBusinessId(UuId businessId) {
        List<ProductAggregateRoot> results = new ArrayList<>();
        collection.find(Filters.eq("businessId", businessId.value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public boolean existsByUuId(UuId productUuId) {
        return collection.countDocuments(Filters.eq("productUuId", productUuId.value())) > 0;
    }

    // Additional required methods from ProductQueryRepository interface
    @Override
    public List<ProductAggregateRoot> findByTypeTemplate(UuId typeColId) {
        List<ProductAggregateRoot> results = new ArrayList<>();
        collection.find(Filters.eq("typeColId", typeColId.value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public List<ProductAggregateRoot> findAllBespoke(UuId businessId) {
        List<ProductAggregateRoot> results = new ArrayList<>();
        // Query for records where typeColId is missing or null (Bespoke XOR Invariant)
        collection.find(Filters.and(
                Filters.eq("businessId", businessId.value()),
                Filters.or(Filters.exists("typeColId", false), Filters.eq("typeColId", null))
        )).forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public long countByStatus(UuId businessId, String status) {
        return collection.countDocuments(Filters.and(
                Filters.eq("businessId", businessId.value()),
                Filters.eq("status", status)
        ));
    }

    // --- RECONSTITUTION MAPPING ---

    private ProductAggregateRoot mapToAggregate(Document doc) {
        return ProductFactory.reconstitute(
                PkId.of(doc.getLong("primaryKey")),
                UuId.fromString(doc.getString("productUuId")),
                UuId.fromString(doc.getString("businessId")),
                new Name(doc.getString("name")),
                new Category(doc.getString("category")),
                new Description(doc.getString("description")),
                StatusEnums.valueOf(doc.getString("status")),
                new Version(doc.getInteger("version")),
                mapAudit(doc.get("audit", Document.class)),
                UuId.fromString(doc.getString("galleryColId")),
                doc.getString("typeColId") != null ? UuId.fromString(doc.getString("typeColId")) : UuId.NONE,
                doc.getString("variantColId") != null ? UuId.fromString(doc.getString("variantColId")) : UuId.NONE,
                mapDimensions(doc.get("dimensions", Document.class)),
                mapWeight(doc.get("weight", Document.class)),
                doc.getString("careInstruction") != null ? new CareInstruction(doc.getString("careInstruction")) : CareInstruction.NONE,
                Set.of(), // Rules handled via separate policy load if needed
                Set.of()
        );
    }

    private AuditMetadata mapAudit(Document d) {
        if (d == null) return AuditMetadata.create(); // Fallback for legacy malformed data

        // Convert Instant to OffsetDateTime to satisfy CreatedAt/LastModified constructors
        OffsetDateTime created = d.getDate("createdAt").toInstant().atOffset(ZoneOffset.UTC);
        OffsetDateTime modified = d.getDate("updatedAt").toInstant().atOffset(ZoneOffset.UTC);

        return AuditMetadata.reconstitute(
                new CreatedAt(created),
                new LastModified(modified)
        );
    } // <-- Added missing closing brace

    private Dimensions mapDimensions(Document d) {
        if (d == null) return Dimensions.NONE;
        return new Dimensions(
                new BigDecimal(d.getString("length")),
                new BigDecimal(d.getString("width")),
                new BigDecimal(d.getString("height")),
                DimensionUnitEnums.fromCode(d.getString("unit"))
        );
    }

    private Weight mapWeight(Document w) {
        if (w == null) return Weight.NONE;
        return new Weight(
                new BigDecimal(w.getString("amount")),
                WeightUnitEnums.fromString(w.getString("unit"))
        );
    }
}
