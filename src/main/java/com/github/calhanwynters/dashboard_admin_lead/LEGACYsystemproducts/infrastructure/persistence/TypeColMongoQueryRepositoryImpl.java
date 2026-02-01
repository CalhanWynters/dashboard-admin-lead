package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.TypeColQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.type.Type;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.type.TypeColFactory;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.type.TypeCollectionAggregate;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.math.BigDecimal;
import java.util.*;

/**
 * PURE READ-SIDE: MongoDB Implementation for Type Collections.
 * Cleaned for 2026 CQRS compliance.
 */
public class TypeColMongoQueryRepositoryImpl implements TypeColQueryRepository {

    private final MongoCollection<Document> collection;

    public TypeColMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_db")
                .getCollection("type_collections");
    }

    @Override
    public Optional<TypeCollectionAggregate> findById(UuId typeColId) {
        Document doc = collection.find(Filters.eq("typeColId", typeColId.value())).first();
        return Optional.ofNullable(doc).map(this::mapToAggregate);
    }

    @Override
    public List<TypeCollectionAggregate> findAllByBusinessId(UuId businessId) {
        List<TypeCollectionAggregate> results = new ArrayList<>();
        collection.find(Filters.eq("businessId", businessId.value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public boolean existsByBusinessId(UuId businessId) {
        return collection.countDocuments(Filters.eq("businessId", businessId.value())) > 0;
    }

    // --- READ-ONLY MAPPING LOGIC (Reconstitution) ---

    private TypeCollectionAggregate mapToAggregate(Document doc) {
        List<Document> typeDocs = doc.getList("types", Document.class);
        Set<Type> types = new HashSet<>();

        if (typeDocs != null) {
            for (Document t : typeDocs) {
                types.add(new Type(
                        UuId.fromString(t.getString("typeId")),
                        new Label(t.getString("compatibilityTag")),
                        new Name(t.getString("typeName")),
                        mapDimensions(t.get("typeDimensions", Document.class)),
                        mapWeight(t.get("typeWeight", Document.class)),
                        new Description(t.getString("typeDescription")),
                        new CareInstruction(t.getString("typeCareInstruction"))
                ));
            }
        }

        return TypeColFactory.reconstitute(
                doc.getInteger("primaryKey"),
                UuId.fromString(doc.getString("typeColId")),
                UuId.fromString(doc.getString("businessId")),
                types
        );
    }

    private Dimensions mapDimensions(Document d) {
        if (d == null) return null;
        return new Dimensions(
                new BigDecimal(d.getString("length")),
                new BigDecimal(d.getString("width")),
                new BigDecimal(d.getString("height")),
                DimensionUnitEnums.fromCode(d.getString("sizeUnit"))
        );
    }

    private Weight mapWeight(Document w) {
        if (w == null) return null;
        return new Weight(
                new BigDecimal(w.getString("amount")),
                WeightUnitEnums.fromString(w.getString("weightUnit"))
        );
    }
}
