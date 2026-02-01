package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.PriceListQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.pricelist.PriceListFactory;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.pricelist.purchasepricingmodel.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.infrastructure.persistence.MongoAuditMapper.mapAudit;

/**
 * PURE READ-SIDE: MongoDB Implementation for PriceList.
 * Handles deep-nested polymorphic mapping for multi-currency pricing.
 */
public class PriceListMongoQueryRepositoryImpl implements PriceListQueryRepository {

    private final MongoCollection<Document> collection;

    public PriceListMongoQueryRepositoryImpl(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("product_db").getCollection("price_lists");
    }

    @Override
    public List<PriceListAggregate> findByStrategyBoundary(Class<?> strategyClass) {
        List<PriceListAggregate> results = new ArrayList<>();
        // Strategy boundary is stored as a String (class featuresName) in your mapping logic
        collection.find(Filters.eq("strategyBoundary", strategyClass.getName()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public List<PriceListAggregate> findAllByCurrency(Currency currency) {
        List<PriceListAggregate> results = new ArrayList<>();
        /*
         * Since multiCurrencyPrices is a Map<UuId, Map<Currency, PurchasePricing>>,
         * in MongoDB it looks like: multiCurrencyPrices.TARGET_UUID.CURRENCY_CODE
         * We use a regex or exists check on nested keys to find any list containing this currency.
         */
        String currencyCode = currency.getCurrencyCode();
        // This looks for any document where the currency code exists as a key in any sub-map
        collection.find(Filters.exists("multiCurrencyPrices.*." + currencyCode))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public boolean existsByUuId(UuId priceListUuId) {
        // Standard existence check returning a boolean
        return collection.countDocuments(Filters.eq("priceListUuId", priceListUuId.value())) > 0;
    }


    @Override
    public Optional<PriceListAggregate> findById(UuId priceListUuId) {
        Document doc = collection.find(Filters.eq("priceListUuId", priceListUuId.value())).first();
        return Optional.ofNullable(doc).map(this::mapToAggregate);
    }

    @Override
    public List<PriceListAggregate> findAllByBusinessId(UuId businessId) {
        List<PriceListAggregate> results = new ArrayList<>();
        collection.find(Filters.eq("businessId", businessId.value()))
                .forEach(doc -> results.add(mapToAggregate(doc)));
        return results;
    }

    @Override
    public OffsetDateTime getLatestTimestamp(UuId priceListUuId) {
        Document doc = collection.find(Filters.eq("priceListUuId", priceListUuId.value()))
                .projection(Projections.include("audit.updatedAt"))
                .first();

        if (doc == null) return null;
        Document audit = doc.get("audit", Document.class);
        return audit != null && audit.getDate("updatedAt") != null
                ? audit.getDate("updatedAt").toInstant().atOffset(ZoneOffset.UTC)
                : null;
    }

    // --- RECONSTITUTION MAPPING (Nested Multi-Currency logic) ---

    private PriceListAggregate mapToAggregate(Document doc) {
        return PriceListFactory.reconstitute(
                PkId.of(doc.getLong("primaryKey")),
                UuId.fromString(doc.getString("priceListUuId")),
                UuId.fromString(doc.getString("businessId")),
                loadBoundaryClass(doc.getString("strategyBoundary")),
                new Version(doc.getInteger("version")),
                mapAudit(doc.get("audit", Document.class)),
                mapMultiCurrencyPrices(doc.get("multiCurrencyPrices", Document.class))
        );
    }

    private Map<UuId, Map<Currency, PurchasePricing>> mapMultiCurrencyPrices(Document doc) {
        Map<UuId, Map<Currency, PurchasePricing>> outerMap = new HashMap<>();
        if (doc == null) return outerMap;

        for (String targetIdStr : doc.keySet()) {
            UuId targetId = UuId.fromString(targetIdStr);
            Document currencyMapDoc = doc.get(targetIdStr, Document.class);
            Map<Currency, PurchasePricing> currencyMap = new HashMap<>();

            for (String currencyCode : currencyMapDoc.keySet()) {
                Currency currency = Currency.getInstance(currencyCode);
                Document pricingDoc = currencyMapDoc.get(currencyCode, Document.class);
                currencyMap.put(currency, reconstitutePricing(pricingDoc));
            }
            outerMap.put(targetId, currencyMap);
        }
        return outerMap;
    }

    /**
     * Polymorphic Reconstitution via 2026 Pattern Matching for Switch.
     */
    private PurchasePricing reconstitutePricing(Document d) {
        String type = d.getString("_type");
        List<Document> bucketDocs = d.getList("buckets", Document.class);

        return switch (type) {
            case "PriceFixedPurchase" ->
                    new PriceFixedPurchase(mapMoney(d.get("fixedPrice", Document.class)));

            case "PriceFractScaledPurchase" ->
                    new PriceFractScaledPurchase(mapMoney(d.get("basePrice", Document.class)), mapMoney(d.get("ratePerUnit", Document.class)));

            case "PriceIntScaledPurchase" ->
                    new PriceIntScaledPurchase(mapMoney(d.get("basePrice", Document.class)), mapMoney(d.get("scalingFactorPerUnit", Document.class)));

            case "PriceFractTieredGradPurchase" ->
                    new PriceFractTieredGradPurchase(bucketDocs.stream().map(this::mapFractGradBucket).toList());

            case "PriceIntTieredGradPurchase" ->
                    new PriceIntTieredGradPurchase(bucketDocs.stream().map(this::mapIntGradBucket).toList());

            case "PriceFractTieredVolPurchase" ->
                    new PriceFractTieredVolPurchase(bucketDocs.stream().map(this::mapFractVolBucket).toList());

            case "PriceIntTieredVolPurchase" ->
                    new PriceIntTieredVolPurchase(bucketDocs.stream().map(this::mapIntVolBucket).toList());

            case "PriceNonePurchase" ->
                    new PriceNonePurchase(Currency.getInstance(d.getString("currency")));

            default -> throw new IllegalStateException("Unknown Pricing Type: " + type);
        };
    }

    // --- Specific Bucket Mappers to satisfy Type Safety ---

    private PriceFractTieredGradPurchase.TierBucket mapFractGradBucket(Document b) {
        return new PriceFractTieredGradPurchase.TierBucket(new BigDecimal(b.getString("minQty")),
                b.getString("maxQty") != null ? new BigDecimal(b.getString("maxQty")) : null, mapMoney(b.get("pricePerUnit", Document.class)));
    }

    private PriceIntTieredGradPurchase.TierBucket mapIntGradBucket(Document b) {
        return new PriceIntTieredGradPurchase.TierBucket(new BigDecimal(b.getString("minQty")),
                b.getString("maxQty") != null ? new BigDecimal(b.getString("maxQty")) : null, mapMoney(b.get("pricePerUnit", Document.class)));
    }

    private PriceFractTieredVolPurchase.TierBucket mapFractVolBucket(Document b) {
        return new PriceFractTieredVolPurchase.TierBucket(new BigDecimal(b.getString("minQty")),
                b.getString("maxQty") != null ? new BigDecimal(b.getString("maxQty")) : null, mapMoney(b.get("pricePerUnit", Document.class)));
    }

    private PriceIntTieredVolPurchase.TierBucket mapIntVolBucket(Document b) {
        return new PriceIntTieredVolPurchase.TierBucket(new BigDecimal(b.getString("minQty")),
                b.getString("maxQty") != null ? new BigDecimal(b.getString("maxQty")) : null, mapMoney(b.get("pricePerUnit", Document.class)));
    }

    private List<PriceFractTieredGradPurchase.TierBucket> mapBuckets(Document d) {
        List<Document> bucketDocs = d.getList("buckets", Document.class);
        return bucketDocs.stream().map(b -> new PriceFractTieredGradPurchase.TierBucket(
                new BigDecimal(b.getString("minQty")),
                b.getString("maxQty") != null ? new BigDecimal(b.getString("maxQty")) : null,
                mapMoney(b.get("pricePerUnit", Document.class))
        )).toList();
    }

    private Money mapMoney(Document m) {
        return new Money(
                new BigDecimal(m.getString("amount")),
                Currency.getInstance(m.getString("currency")),
                m.getInteger("precision"),
                java.math.RoundingMode.valueOf(m.getString("roundingMode"))
        );
    }

    @SuppressWarnings("unchecked")
    private Class<? extends PurchasePricing> loadBoundaryClass(String className) {
        try {
            return (Class<? extends PurchasePricing>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Invalid Strategy Boundary in Snapshot: " + className);
        }
    }

}
