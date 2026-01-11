package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public record WeightVO(BigDecimal amount, WeightUnitEnums weightUnit) {

    // Lexical Content: Whitelist for numeric inputs
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,5})?$"); // Adjusted to match up to 5 decimal places

    // Size & Boundary: Max string length for BigDecimal
    private static final int MAX_SERIALIZED_LENGTH = 32;

    // Semantics: Max input scale for precision
    private static final int MAX_INPUT_SCALE = 5;

    public WeightVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(amount, "Weight amount must not be null");
        Objects.requireNonNull(weightUnit, "Weight unit must not be null");

        // 2. Size & Boundary (String DoS Prevention)
        String plainAmount = amount.toPlainString();
        if (plainAmount.length() > MAX_SERIALIZED_LENGTH) {
            throw new IllegalArgumentException("Input numeric string length exceeds security boundary.");
        }

        // 3. Lexical Content & Syntax
        if (!NUMERIC_PATTERN.matcher(plainAmount).matches()) {
            throw new IllegalArgumentException("Weight amount contains illegal characters or invalid scale format.");
        }

        // 4. Semantics
        if (amount.scale() > MAX_INPUT_SCALE) {
            throw new IllegalArgumentException("Numeric precision exceeds allowed scale of " + MAX_INPUT_SCALE + " decimal places.");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Weight amount cannot be negative.");
        }
    }
}

