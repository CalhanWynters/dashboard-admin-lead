package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Scaling Price VO for Java 25 (2026).
 * Enforces strict ISO-4217 scale compliance for both base and incremental prices.
 */
public record ScalingPriceVO(
        String unit,
        BigDecimal baseThreshold,
        BigDecimal basePrice,
        BigDecimal incrementStep,
        BigDecimal pricePerStep,
        int precision,
        Currency currency
) {
    // 1. Lexical Content: Fully anchored whitelist
    private static final Pattern UNIT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-/]{1,20}$");
    // 2. Size Boundaries: Protect against Arithmetic DoS
    private static final int MAX_ARITHMETIC_SCALE = 100;
    private static final BigDecimal MAX_PRICE_LIMIT = new BigDecimal("1000000000.00");

    public ScalingPriceVO {
        // --- Existence & Nullability ---
        Objects.requireNonNull(unit, "Unit is required");
        Objects.requireNonNull(baseThreshold, "Base threshold is required");
        Objects.requireNonNull(basePrice, "Base price is required");
        Objects.requireNonNull(incrementStep, "Increment step is required");
        Objects.requireNonNull(pricePerStep, "Price per step is required");
        Objects.requireNonNull(currency, "Currency is required");

        // --- Lexical Content & Size ---
        if (!UNIT_PATTERN.matcher(unit).matches()) {
            throw new IllegalArgumentException("Unit contains invalid characters or length");
        }

        // --- Arithmetic DoS Mitigation ---
        // Verify scale to prevent CPU exhaustion on division
        if (baseThreshold.scale() > MAX_ARITHMETIC_SCALE || incrementStep.scale() > MAX_ARITHMETIC_SCALE) {
            throw new IllegalArgumentException("Numeric scale exceeds safety limits");
        }

        // --- Semantics & Boundary ---
        if (incrementStep.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Increment step must be strictly positive");
        }
        if (baseThreshold.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }

        // --- Cross-Field Consistency ---
        // Strict Currency Scale Validation (Fractional Penny Check)
        int standardScale = currency.getDefaultFractionDigits();

        // Validate Precision Component
        if (precision < standardScale || precision > 10) {
            throw new IllegalArgumentException("Precision %d is invalid for %s (min %d, max 10)"
                    .formatted(precision, currency.getCurrencyCode(), standardScale));
        }

        // Validate Base Price
        if (basePrice.stripTrailingZeros().scale() > standardScale) {
            throw new IllegalArgumentException("Base price scale exceeds %s limit of %d digits"
                    .formatted(currency.getCurrencyCode(), standardScale));
        }

        // Validate Step Price
        if (pricePerStep.stripTrailingZeros().scale() > standardScale) {
            throw new IllegalArgumentException("Price per step scale exceeds %s limit of %d digits"
                    .formatted(currency.getCurrencyCode(), standardScale));
        }

        // 5. Logical System Boundaries
        if (basePrice.abs().compareTo(MAX_PRICE_LIMIT) > 0 || pricePerStep.abs().compareTo(MAX_PRICE_LIMIT) > 0) {
            throw new IllegalArgumentException("Price values exceed system boundary limits");
        }

        if (basePrice.compareTo(BigDecimal.ZERO) <= 0 && pricePerStep.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Scaling price must have a positive base price or step price.");
        }

        // 6. Final Normalization to Canonical Form
        // We use UNNECESSARY because the validation above guarantees no data loss
        basePrice = basePrice.setScale(precision, RoundingMode.UNNECESSARY);
        pricePerStep = pricePerStep.setScale(precision, RoundingMode.UNNECESSARY);
    }

    public static ScalingPriceVO of(String unit, BigDecimal threshold, BigDecimal base,
                                    BigDecimal step, BigDecimal stepPrice, Currency currency) {
        return new ScalingPriceVO(unit, threshold, base, step, stepPrice,
                currency.getDefaultFractionDigits(), currency);
    }

    public BigDecimal calculate(BigDecimal quantityRequested) {
        // Existence: Handle null quantity as zero
        BigDecimal qty = (quantityRequested == null) ? BigDecimal.ZERO : quantityRequested;

        // Semantics: Logical range check
        if (qty.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (qty.compareTo(baseThreshold) <= 0) {
            return basePrice;
        }

        // Arithmetic Safety: Ensure the calculation doesn't create infinite decimals
        BigDecimal overage = qty.subtract(baseThreshold);
        // Calculation logic: Ceiling division for partial steps
        BigDecimal numberOfSteps = overage.divide(incrementStep, 0, RoundingMode.CEILING);
        BigDecimal incrementalCost = numberOfSteps.multiply(pricePerStep);

        // Result is strictly scaled to the currency's standard to prevent fractional penny output
        return basePrice.add(incrementalCost).setScale(precision, RoundingMode.UNNECESSARY);
    }
}
