package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.GalleryQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryColFactory;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class GalleryMongoQueryRepositoryImpl implements GalleryQueryRepository {

    private final MongoCollection<Document> collection;

    public GalleryMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_db").getCollection("galleries");
    }

    @Override
    public Optional<GalleryCollection> findById(UuId galleryId) {
        Document doc = collection.find(Filters.eq("galleryId", galleryId.value())).first();
        return Optional.ofNullable(doc).map(this::mapToAggregate);
    }

    @Override
    public List<GalleryCollection> findAllByBusinessId(UuId businessId) {
        List<GalleryCollection> results = new ArrayList<>();
        collection.find(Filters.eq("businessId", businessId.value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    private GalleryCollection mapToAggregate(Document doc) {
        List<String> rawUrls = doc.getList("imageUrls", String.class);

        // Reconstitution: Convert Strings back to ImageUrl Value Objects (Enforces SSRF Guards)
        Set<ImageUrl> imageUrls = (rawUrls == null) ? Set.of() : rawUrls.stream()
                .map(ImageUrl::new)
                .collect(Collectors.toSet());

        return GalleryColFactory.reconstitute(
                doc.getInteger("primaryKey"),
                UuId.fromString(doc.getString("galleryId")),
                UuId.fromString(doc.getString("businessId")),
                imageUrls
        );
    }
}
