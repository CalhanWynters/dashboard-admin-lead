package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * DimensionsVO: A system-agnostic record for storing physical dimensions.
 * Optimized for 2026 data integrity and resource exhaustion protection.
 */
public record DimensionsVO(BigDecimal length, BigDecimal width, BigDecimal height, DimensionUnitEnums sizeUnit) {

    // Prevents scientific notation bypasses and ensures a clean decimal format
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,10})?$");

    // Safety cap to prevent "Infinite Volume" or database numeric overflows
    // Set to a high value (e.g., 10,000) just to catch clear data entry errors
    private static final BigDecimal ABSOLUTE_MAX_LIMIT = new BigDecimal("10000.0");

    // Prevents Arithmetic DoS attacks by limiting string parsing length
    private static final int MAX_INPUT_STR_LENGTH = 16;

    public DimensionsVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(length, "Length is required");
        Objects.requireNonNull(width, "Width is required");
        Objects.requireNonNull(height, "Height is required");
        Objects.requireNonNull(sizeUnit, "Unit of measure is required");

        // 2. Syntactic & Physical Boundary Validation
        validateField("Length", length);
        validateField("Width", width);
        validateField("Height", height);

        // 3. Semantic Logical Check: Dimensions must exist in physical space
        if (length.signum() <= 0 || width.signum() <= 0 || height.signum() <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive values greater than zero.");
        }
    }

    private void validateField(String fieldName, BigDecimal value) {
        String plain = value.toPlainString();

        // Security: Prevent processing of excessively long strings
        if (plain.length() > MAX_INPUT_STR_LENGTH) {
            throw new IllegalArgumentException(fieldName + " input exceeds security length boundary.");
        }

        // Lexical: Ensure characters match expected numeric format
        if (!NUMERIC_PATTERN.matcher(plain).matches()) {
            throw new IllegalArgumentException(fieldName + " contains illegal characters or invalid format.");
        }

        // Absolute Safety: Prevent values that could break downstream math/storage
        if (value.compareTo(ABSOLUTE_MAX_LIMIT) > 0) {
            throw new IllegalArgumentException(fieldName + " exceeds absolute system limit of " + ABSOLUTE_MAX_LIMIT);
        }
    }
}
