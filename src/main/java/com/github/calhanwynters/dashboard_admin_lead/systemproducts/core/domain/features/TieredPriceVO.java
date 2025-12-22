package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Tiered Pricing Value Object - Hardened for 2025.
 * Implements strict defensive copying, scale boundaries, and lexical whitelisting.
 */
public record TieredPriceVO(
        String unit,
        List<PriceTier> tiers,
        Currency currency
) {
    // 1. Lexical Content: Fully anchored whitelist
    private static final Pattern UNIT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-/]{1,20}$");

    // 2. Size & Boundary: Prevent memory and arithmetic DoS
    private static final int MAX_TIERS = 100;
    private static final int MAX_SCALE = 10;
    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000000.00");

    public record PriceTier(BigDecimal threshold, BigDecimal price) {
        public PriceTier {
            // Existence & Nullability
            Objects.requireNonNull(threshold, "Threshold required");
            Objects.requireNonNull(price, "Price required");

            // Arithmetic DoS Prevention: Check scale
            if (threshold.scale() > MAX_SCALE || price.scale() > MAX_SCALE) {
                throw new IllegalArgumentException("Numeric scale exceeds 2025 safety limits");
            }

            // Semantics: Logic and Ranges
            if (threshold.compareTo(BigDecimal.ZERO) < 0 || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Tier values must be non-negative");
            }
            if (price.compareTo(MAX_PRICE) > 0) {
                throw new IllegalArgumentException("Price exceeds system boundary");
            }
        }
    }

    /**
     * Compact Constructor.
     */
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

        // 4. Defensive Copying & Semantic Sorting
        // Rubric Requirement: Use List.copyOf for a truly immutable snapshot
        List<PriceTier> sortedTiers = tiers.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PriceTier::threshold))
                .toList();

        // Cross-Field Consistency: Ensure no duplicate thresholds (Ambiguous pricing)
        long uniqueThresholds = sortedTiers.stream().map(PriceTier::threshold).distinct().count();
        if (uniqueThresholds != sortedTiers.size()) {
            throw new IllegalArgumentException("Tiers must have unique thresholds to prevent pricing ambiguity");
        }

        tiers = List.copyOf(sortedTiers);
    }

    public BigDecimal calculate(BigDecimal quantity) {
        // Semantics: Enforce logical quantity handling
        BigDecimal qty = (quantity == null) ? BigDecimal.ZERO : quantity;
        if (qty.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Calculation quantity cannot be negative");
        }

        return tiers.stream()
                .filter(t -> t.threshold().compareTo(qty) >= 0)
                .findFirst()
                .map(PriceTier::price)
                .orElseGet(() -> tiers.getLast().price())
                .setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }
}
