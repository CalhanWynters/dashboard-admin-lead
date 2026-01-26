package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.GalleryQueryRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GalleryMongoQueryRepositoryImpl implements GalleryQueryRepository {

    private final MongoCollection<Document> collection;

    public GalleryMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_query_db")
                .getCollection("galleries");
    }

    @Override
    public Optional<GallerySummary> findByUuid(String galleryUuid) {
        Document doc = collection.find(Filters.eq("_id", galleryUuid)).first();
        return Optional.ofNullable(doc).map(this::mapToSummary);
    }

    @Override
    public List<GallerySummary> findAllByBusiness(String businessId) {
        List<GallerySummary> results = new ArrayList<>();
        collection.find(Filters.eq("business_id", businessId))
                .forEach(doc -> results.add(mapToSummary(doc)));
        return results;
    }

    @SuppressWarnings("unchecked")
    private GallerySummary mapToSummary(Document doc) {
        return new GallerySummary(
                doc.getString("_id"),
                doc.getString("business_id"),
                (List<String>) doc.get("image_urls")
        );
    }
}
