package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public record DimensionsVO(BigDecimal length, BigDecimal width, BigDecimal height, DimensionUnitEnums sizeUnit) {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,10})?$");
    private static final BigDecimal ABSOLUTE_MAX_LIMIT = new BigDecimal("10000.0");
    private static final int MAX_INPUT_STR_LENGTH = 16;

    /**
     * Factory method: Strictly enforces non-scientific notation from String inputs.
     * Use this for API/UI data entry.
     */
    public static DimensionsVO of(String lengthStr, String widthStr, String heightStr, DimensionUnitEnums unit) {
        return new DimensionsVO(
                parseStrict(lengthStr, "Length"),
                parseStrict(widthStr, "Width"),
                parseStrict(heightStr, "Height"),
                unit
        );
    }

    private static BigDecimal parseStrict(String input, String fieldName) {
        if (input == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        // FIRST: Security Boundary Check (DoS protection)
        if (input.length() > MAX_INPUT_STR_LENGTH) {
            throw new IllegalArgumentException(fieldName + " input exceeds security length boundary.");
        }

        // SECOND: Lexical Format Check (Regex)
        if (!NUMERIC_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException(fieldName + " must be a plain numeric format (no scientific notation).");
        }

        return new BigDecimal(input);
    }


    public DimensionsVO {
        Objects.requireNonNull(length, "Length is required");
        Objects.requireNonNull(width, "Width is required");
        Objects.requireNonNull(height, "Height is required");
        Objects.requireNonNull(sizeUnit, "Unit required");

        validateLogical(length, "Length");
        validateLogical(width, "Width");
        validateLogical(height, "Height");
    }

    private void validateLogical(BigDecimal value, String name) {
        if (value.signum() <= 0 || value.compareTo(ABSOLUTE_MAX_LIMIT) > 0) {
            throw new IllegalArgumentException(name + " must be positive and below " + ABSOLUTE_MAX_LIMIT);
        }
    }
}
