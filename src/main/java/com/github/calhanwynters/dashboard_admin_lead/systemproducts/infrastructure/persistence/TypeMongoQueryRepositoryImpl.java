package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.TypeColQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
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
 * MongoDB Implementation of the TypeColQueryRepository.
 * Maps denormalized MongoDB documents directly to TypeColProjectionDTO.
 */
public class TypeMongoQueryRepositoryImpl implements TypeColQueryRepository {

    private static final Logger logger = LoggerFactory.getLogger(TypeMongoQueryRepositoryImpl.class);
    private final MongoCollection<Document> collection;

    public TypeMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_catalog_read")
                .getCollection("type_collections");
    }

    @Override
    public Optional<TypeColProjectionDTO> findProjectionByBusinessId(UuId businessId) {
        try {
            // Find the collection document by business_id
            Document doc = collection.find(Filters.eq("business_id", businessId.value().toString())).first();

            if (doc == null) {
                return Optional.empty();
            }

            return Optional.of(mapToProjectionDto(doc));
        } catch (Exception e) {
            logger.error("Error retrieving Type Collection projection for business: {}", businessId, e);
            return Optional.empty();
        }
    }

    private TypeColProjectionDTO mapToProjectionDto(Document doc) {
        List<Document> typeDocs = doc.getList("types", Document.class, new ArrayList<>());

        List<TypeColProjectionDTO.TypeItemDTO> typeItems = typeDocs.stream()
                .map(this::mapToItemDto)
                .toList();

        return new TypeColProjectionDTO(
                doc.getString("_id"), // typeColId
                doc.getString("business_id"),
                typeItems.size(),
                typeItems
        );
    }

    private TypeColProjectionDTO.TypeItemDTO mapToItemDto(Document typeDoc) {
        Document specs = typeDoc.get("physical_specs", Document.class);
        Document pricing = typeDoc.get("pricing", Document.class);

        // Formatting logic moved to the read-side mapper/repository for UI optimization
        String dimensions = (specs != null)
                ? String.format("%sx%sx%s", specs.get("length"), specs.get("width"), specs.get("height"))
                : "N/A";

        String weight = (specs != null) ? String.valueOf(specs.get("weight")) : "N/A";
        String price = (pricing != null) ? String.valueOf(pricing.get("amount")) : "0.00";

        return new TypeColProjectionDTO.TypeItemDTO(
                typeDoc.getString("type_id"),
                typeDoc.getString("name"),
                typeDoc.getString("compatibility_tag"),
                typeDoc.getString("description"),
                typeDoc.getString("care_instructions"),
                weight,
                dimensions,
                price
        );
    }
}
