package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GalleryMongoQueryRepositoryImpl {

    private final MongoCollection<Document> collection;

    public GalleryMongoQueryRepositoryImpl(MongoClient mongoClient) {
        // Accessing the read-optimized 'projections' database
        this.collection = mongoClient.getDatabase("product_query_db")
                .getCollection("galleries");
    }

    public record GalleryReadModel(String galleryUuid, String businessId, List<String> imageUrls) {}

    /**
     * High-speed O(1) lookup in MongoDB.
     */
    public Optional<GalleryReadModel> findByUuid(String galleryUuid) {
        Document doc = collection.find(Filters.eq("_id", galleryUuid)).first();

        return Optional.ofNullable(doc).map(this::mapToReadModel);
    }

    /**
     * Efficiently fetches all galleries for a business using a MongoDB index.
     */
    public List<GalleryReadModel> findAllByBusiness(String businessId) {
        List<GalleryReadModel> results = new ArrayList<>();

        collection.find(Filters.eq("business_id", businessId))
                .forEach(doc -> results.add(mapToReadModel(doc)));

        return results;
    }

    @SuppressWarnings("unchecked")
    private GalleryReadModel mapToReadModel(Document doc) {
        return new GalleryReadModel(
                doc.getString("_id"),
                doc.getString("business_id"),
                (List<String>) doc.get("image_urls")
        );
    }
}
