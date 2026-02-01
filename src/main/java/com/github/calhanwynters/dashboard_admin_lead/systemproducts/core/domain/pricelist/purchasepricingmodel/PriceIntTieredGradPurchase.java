package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * Implements Graduated (Staircase) Pricing for single purchases.
 * Every unit is billed according to the rate of the tier it falls into.
 */
public record PriceIntTieredGradPurchase(List<TierBucket> buckets) implements PurchasePricing {

    public record TierBucket(BigDecimal minQty, BigDecimal maxQty, Money pricePerUnit) {
        public TierBucket {
            Objects.requireNonNull(minQty, "minQty cannot be null");
            Objects.requireNonNull(pricePerUnit, "pricePerUnit cannot be null");
        }
    }

    /**
     * Compact Constructor for validation and defensive copying.
     */
    public PriceIntTieredGradPurchase {
        Objects.requireNonNull(buckets, "Buckets list cannot be null");
        if (buckets.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one bucket.");
        }

        // 1. Validate Currency Consistency
        // All tiers must use the same currency to allow for mathematical summation.
        Currency baseCurrency = buckets.getFirst().pricePerUnit().currency();
        for (TierBucket bucket : buckets) {
            if (!bucket.pricePerUnit().currency().equals(baseCurrency)) {
                throw new IllegalArgumentException("All buckets must use the same currency: " + baseCurrency);
            }
        }

        // 2. Defensive Copying and Sorting
        // Reassigning the 'buckets' parameter here automatically updates the record's final field.
        buckets = buckets.stream()
                .sorted(Comparator.comparing(TierBucket::minQty))
                .toList();
    }

    @Override
    public Money calculate(BigDecimal quantity) {
        BigDecimal remaining = (quantity == null) ? BigDecimal.ZERO : quantity;

        // Start with a zero-sum Money using the currency from the first bucket
        Money total = Money.zero(buckets.getFirst().pricePerUnit().currency());

        for (TierBucket bucket : buckets) {
            if (remaining.signum() <= 0) break;

            BigDecimal contribution = calculateBucketContribution(bucket, remaining);

            if (contribution.signum() > 0) {
                // Calculate cost for this specific bucket's portion
                Money bucketCost = bucket.pricePerUnit().multiply(contribution.intValueExact());
                total = total.add(bucketCost);

                // Subtract the processed portion from the total remaining quantity
                remaining = remaining.subtract(contribution);
            }
        }

        return total;
    }

    /**
     * Determines how much of the remaining quantity fits into the current bucket.
     */
    private BigDecimal calculateBucketContribution(TierBucket bucket, BigDecimal remaining) {
        if (bucket.maxQty() == null) {
            // "Infinity" bucket: takes everything that is left
            return remaining;
        }

        // Capacity = max - min
        BigDecimal capacity = bucket.maxQty().subtract(bucket.minQty());

        // Contribution is the smaller of what's left vs the bucket's total capacity
        return remaining.min(capacity);
    }
}
