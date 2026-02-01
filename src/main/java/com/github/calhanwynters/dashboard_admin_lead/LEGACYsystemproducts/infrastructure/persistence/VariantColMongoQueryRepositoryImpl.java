package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.VariantColQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PURE READ-SIDE: MongoDB Implementation for Variant Collections.
 * This class is strictly for Querying and contains no 'save' or 'update' logic.
 */
public class VariantColMongoQueryRepositoryImpl implements VariantColQueryRepository {

    private final MongoCollection<Document> collection;

    public VariantColMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_db")
                .getCollection("variant_collections");
    }

    @Override
    public Optional<VariantCollectionAggregate> findById(UuId variantColId) {
        Document doc = collection.find(Filters.eq("variantColId", variantColId.value())).first();
        return Optional.ofNullable(doc).map(this::mapToAggregate);
    }

    @Override
    public Set<VariantCollectionAggregate> findAllByBusinessId(UuId businessId) {
        Set<VariantCollectionAggregate> results = new HashSet<>();
        collection.find(Filters.eq("businessId", businessId.value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public boolean existsByBusinessId(UuId businessId) {
        return collection.countDocuments(Filters.eq("businessId", businessId.value())) > 0;
    }

    @Override
    public int countVariantsInCollection(UuId variantColId) {
        return findById(variantColId)
                .map(col -> col.act().size())
                .orElse(0);
    }

    @Override
    public Set<VariantCollectionAggregate> findByVariant(VariantAggregate variant) {
        Set<VariantCollectionAggregate> results = new HashSet<>();
        // Query nested array for matching variant ID
        collection.find(Filters.eq("variants.variantId", variant.getVariantId().value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    // --- READ-ONLY MAPPING (Reconstitution) ---

    private VariantCollectionAggregate mapToAggregate(Document doc) {
        List<Document> variantDocs = doc.getList("variants", Document.class);

        // Map documents directly to IDs
        Set<UuId> variantIds = (variantDocs == null) ? Collections.emptySet() : variantDocs.stream()
                .map(f -> UuId.fromString(f.getString("variantId")))
                .collect(Collectors.toSet());

        return VariantCollectionFactory.reconstitute(
                doc.getInteger("primaryKey"),
                UuId.fromString(doc.getString("variantColId")),
                UuId.fromString(doc.getString("businessId")),
                variantIds
        );
    }









}
