package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.legacyfeatures;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


/**
 * Tiered Pricing Value Object - Hardened for 2026.
 * Implements strict defensive copying, scale boundaries, and lexical whitelisting.
 */
public record TieredPriceVO(
        String unit,
        List<PriceTier> tiers,
        Currency currency
) {
    private static final Pattern UNIT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-/]{1,20}$");
    private static final int MAX_TIERS = 100;
    private static final int MAX_SCALE = 10;
    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000000.00");

    // The inner record is now aware of the currency context for validation
    public record PriceTier(BigDecimal threshold, BigDecimal price, Currency currency) {
        public PriceTier {
            // Existence & Nullability
            Objects.requireNonNull(threshold, "Threshold required");
            Objects.requireNonNull(price, "Price required");
            Objects.requireNonNull(currency, "Currency required");

            // Arithmetic DoS Prevention: Check scale
            if (threshold.scale() > MAX_SCALE || price.scale() > MAX_SCALE) {
                throw new IllegalArgumentException("Numeric scale exceeds 2026 safety limits");
            }

            if (price.compareTo(MAX_PRICE) > 0) {
                throw new IllegalArgumentException("Price exceeds system boundary");
            }

            // --- ADDED: STRICT INPUT VALIDATION AGAINST CURRENCY PRECISION ---
            int expectedScale = currency.getDefaultFractionDigits();
            // Use stripTrailingZeros() to handle cases like JPY (scale 0) accepting $10.00 input
            int actualInputScale = price.stripTrailingZeros().scale();

            if (actualInputScale > expectedScale) {
                throw new IllegalArgumentException(
                        "Input price scale (%d) exceeds currency %s allowed precision (%d). Fractional pennies not allowed."
                                .formatted(actualInputScale, currency.getCurrencyCode(), expectedScale)
                );
            }
            // ----------------------------------------------------------------
        }

        // Standard factory - REJECTS zero or negative
        // Note: Factory methods within inner records must accept all fields now due to validation change.
        public static PriceTier of(BigDecimal threshold, BigDecimal price, Currency currency) {
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Standard price must be strictly positive. Use .free() for $0 products.");
            }
            return new PriceTier(threshold, price, currency);
        }

        // Explicit factory - REQUIRES acknowledgment of making the tier free
        public static PriceTier free(BigDecimal threshold, Currency currency, String acknowledgment) {
            if (acknowledgment == null || acknowledgment.isEmpty()) {
                throw new IllegalArgumentException("Acknowledgment for creating a free tier is required.");
            }
            return new PriceTier(threshold, BigDecimal.ZERO, currency);
        }
    }

    public TieredPriceVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(unit, "Unit required");
        Objects.requireNonNull(currency, "Currency required");

        // 2. Lexical & Syntax
        if (!UNIT_PATTERN.matcher(unit).matches()) {
            throw new IllegalArgumentException("Invalid unit format or encoding");
        }

        // 3. Size & Boundary
        if (tiers == null || tiers.isEmpty() || tiers.size() > MAX_TIERS) {
            throw new IllegalArgumentException("Invalid tier count (1 to %d)".formatted(MAX_TIERS));
        }

        // 4. Defensive Copying, Semantic Sorting, and Cross-Field Consistency Check
        // The list comprehension is used for clarity and defensive copying in Java 25+
        List<PriceTier> validatedTiers = tiers.stream()
                .filter(Objects::nonNull)
                .map(tier -> {
                    // CRITICAL: Ensure every tier in the list matches the parent VO's currency.
                    if (!tier.currency().equals(currency)) {
                        throw new IllegalArgumentException("All tiers must use the same currency as the parent TieredPriceVO.");
                    }
                    // The inner compact constructor handles the scale/price validation
                    return new PriceTier(tier.threshold(), tier.price(), tier.currency());
                })
                .sorted(Comparator.comparing(PriceTier::threshold))
                .toList();

        long uniqueThresholds = validatedTiers.stream()
                .map(tier -> tier.threshold().stripTrailingZeros()) // Normalize scale
                .distinct()
                .count();
        if (uniqueThresholds != validatedTiers.size()) {
            throw new IllegalArgumentException("Tiers must have unique thresholds.");
        }

        // 5. Positive Price Check
        boolean hasPaidTier = validatedTiers.stream().anyMatch(t -> t.price().compareTo(BigDecimal.ZERO) > 0);
        if (!hasPaidTier) {
            throw new IllegalArgumentException("At least one tier must have a positive price.");
        }

        // 6. Assignment
        tiers = List.copyOf(validatedTiers);
    }

    public BigDecimal calculate(BigDecimal quantity) {
        BigDecimal qty = (quantity == null) ? BigDecimal.ZERO : quantity;

        // Find the highest threshold that is LESS THAN OR EQUAL to the quantity
        BigDecimal calculatedPrice = tiers.stream()
                .filter(t -> t.threshold().compareTo(qty) <= 0)
                .reduce((first, second) -> second) // Get the last (highest) matching tier
                .map(PriceTier::price)
                .orElseGet(() -> tiers.getFirst().price()); // Fallback to lowest tier

        // Final normalization to ensure the ultimate output always respects the currency's scale
        return calculatedPrice.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }
}
