package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * Implements Graduated (Staircase) Pricing for fractional quantities.
 * Portions of the quantity are billed at different rates as thresholds are crossed.
 * Common in SaaS metered billing, utility usage, and time-based services.
 */
public record PriceFractTieredGradPurchase(List<TierBucket> buckets) implements PurchasePricing {

    public record TierBucket(BigDecimal minQty, BigDecimal maxQty, Money pricePerUnit) {
        public TierBucket {
            Objects.requireNonNull(minQty, "minQty cannot be null");
            Objects.requireNonNull(pricePerUnit, "pricePerUnit cannot be null");
        }
    }

    public PriceFractTieredGradPurchase {
        Objects.requireNonNull(buckets, "Buckets list cannot be null");
        if (buckets.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one pricing bucket.");
        }

        // 1. Validate Currency Consistency
        Currency baseCurrency = buckets.getFirst().pricePerUnit().currency();
        for (TierBucket bucket : buckets) {
            if (!bucket.pricePerUnit().currency().equals(baseCurrency)) {
                throw new IllegalArgumentException("All buckets must use the same currency: " + baseCurrency);
            }
        }

        // 2. Defensive Copying and Sorting
        buckets = buckets.stream()
                .sorted(Comparator.comparing(TierBucket::minQty))
                .toList();
    }

    /**
     * Calculates the total price by filling buckets sequentially.
     * Supports fractional quantities (e.g., 1.5 units).
     */
    @Override
    public Money calculate(BigDecimal quantity) {
        BigDecimal remaining = (quantity == null) ? BigDecimal.ZERO : quantity;

        if (remaining.signum() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        // Initialize total with a zero amount in the bucket's currency
        Money total = Money.zero(buckets.getFirst().pricePerUnit().currency());

        for (TierBucket bucket : buckets) {
            if (remaining.signum() <= 0) break;

            BigDecimal contribution = calculateBucketContribution(bucket, remaining);

            if (contribution.signum() > 0) {
                // IMPORTANT: Uses the BigDecimal multiply overload to allow fractional math
                Money bucketCost = bucket.pricePerUnit().multiply(contribution);
                total = total.add(bucketCost);

                remaining = remaining.subtract(contribution);
            }
        }

        return total;
    }

    /**
     * Determines the portion of the remaining quantity that belongs in this bucket.
     */
    private BigDecimal calculateBucketContribution(TierBucket bucket, BigDecimal remaining) {
        if (bucket.maxQty() == null) {
            // Final catch-all bucket
            return remaining;
        }

        // Capacity = max - min
        BigDecimal capacity = bucket.maxQty().subtract(bucket.minQty());

        // Contribution is the smaller of what's left vs the bucket's total capacity
        return remaining.min(capacity);
    }

    @Override
    public PurchasePricing adjustedBy(double factor) {
        BigDecimal bigFactor = BigDecimal.valueOf(factor);

        // Map over the buckets and adjust each bucket's pricePerUnit
        List<TierBucket> adjustedBuckets = this.buckets.stream()
                .map(bucket -> new TierBucket(
                        bucket.minQty(),
                        bucket.maxQty(),
                        bucket.pricePerUnit().multiply(bigFactor)
                ))
                .toList();

        return new PriceFractTieredGradPurchase(adjustedBuckets);
    }
}
