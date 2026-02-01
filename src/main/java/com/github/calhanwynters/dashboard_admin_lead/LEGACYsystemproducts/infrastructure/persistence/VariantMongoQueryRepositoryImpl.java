package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.VariantQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.Feature;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.VariantFactory;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.VariantAggregate;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PURE READ-SIDE: MongoDB Implementation for Variants.
 * This class is strictly for Querying and contains no 'save' or 'update' logic.
 */
public class VariantMongoQueryRepositoryImpl implements VariantQueryRepository {

    private final MongoCollection<Document> collection;

    public VariantMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_db")
                .getCollection("feature_collections");
    }

    @Override
    public Optional<VariantAggregate> findById(UuId variantId) {
        Document doc = collection.find(Filters.eq("variantId", variantId.value())).first();
        return Optional.ofNullable(doc).map(this::mapToAggregate);
    }

    @Override
    public Set<VariantAggregate> findAllByBusinessId(UuId businessId) {
        Set<VariantAggregate> results = new HashSet<>();
        collection.find(Filters.eq("businessId", businessId.value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public boolean existsByBusinessId(UuId businessId) {
        return collection.countDocuments(Filters.eq("businessId", businessId.value())) > 0;
    }

    @Override
    public int countFeaturesInCollection(UuId variantId) {
        return findById(variantId)
                .map(col -> col.act().size())
                .orElse(0);
    }

    @Override
    public Set<VariantAggregate> findByFeature(Feature feature) {
        Set<VariantAggregate> results = new HashSet<>();
        // Query nested array for matching feature ID
        collection.find(Filters.eq("features.featureUuId", feature.featureUuId().value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    // --- READ-ONLY MAPPING (Reconstitution) ---

    private VariantAggregate mapToAggregate(Document doc) {
        List<Document> featureDocs = doc.getList("features", Document.class);
        Set<Feature> features = (featureDocs == null) ? Set.of() : featureDocs.stream()
                .map(f -> new Feature(
                        UuId.fromString(f.getString("featureUuId")),
                        new Name(f.getString("name")),
                        new Label(f.getString("compatibilityTag")),
                        new Description(f.getString("featureDescription"))
                ))
                .collect(Collectors.toSet());

        return VariantFactory.reconstitute(
                doc.getInteger("primaryKey"),
                UuId.fromString(doc.getString("variantColId")),
                UuId.fromString(doc.getString("businessId")),
                features
        );
    }
}
