package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Tiered Pricing Value Object for 2025.
 * Incorporates Origin, Size, Lexical, Syntax, and Semantic safety.
 * In the Feature Entity, place as a list for varying currencies for easy regional config based runtime.
 */
public record TieredPriceVO(
        String unit,
        List<PriceTier> tiers,
        Currency currency
) {
    // Lexical Content: Restrict to alphanumeric, dashes, and slashes.
    private static final Pattern UNIT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-/]{1,20}$");

    // Size: Prevent memory abuse (e.g., millions of tiers).
    private static final int MAX_TIERS = 100;

    public record PriceTier(BigDecimal threshold, BigDecimal price) {
        public PriceTier {
            Objects.requireNonNull(threshold, "Threshold required");
            Objects.requireNonNull(price, "Price required");
            // Semantics: Prices and thresholds cannot be negative.
            if (threshold.compareTo(BigDecimal.ZERO) < 0 || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Tier values must be non-negative");
            }
        }
    }

    public TieredPriceVO {
        // 1. Lexical & Syntax: Basic format and encoding check.
        Objects.requireNonNull(unit, "Unit required");
        if (!UNIT_PATTERN.matcher(unit).matches()) {
            throw new IllegalArgumentException("Invalid unit format or encoding");
        }

        // 2. Size: Defensive limit on list size.
        if (tiers == null || tiers.isEmpty() || tiers.size() > MAX_TIERS) {
            throw new IllegalArgumentException("Invalid tier count (1 to " + MAX_TIERS + ")");
        }

        // 3. Semantics & Immutability: Sort and lock the list.
        // In Java 25, .toList() returns an unmodifiable list.
        tiers = tiers.stream()
                .sorted(Comparator.comparing(PriceTier::threshold))
                .toList();

        Objects.requireNonNull(currency, "Currency required");

        // Origin Note: Identity verification (Legitimate Sender) is typically
        // handled via JWT/OAuth2 at the Service/Controller layer before instantiation.
    }

    public BigDecimal calculate(BigDecimal quantity) {
        // Semantics: Ensure calculation makes sense even with null/negative input.
        BigDecimal qty = (quantity == null) ? BigDecimal.ZERO : quantity.max(BigDecimal.ZERO);

        return tiers.stream()
                .filter(t -> t.threshold().compareTo(qty) >= 0)
                .findFirst()
                .map(PriceTier::price)
                .orElseGet(() -> tiers.getLast().price()) // Java 21+ Sequenced Collections
                .setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }
}
