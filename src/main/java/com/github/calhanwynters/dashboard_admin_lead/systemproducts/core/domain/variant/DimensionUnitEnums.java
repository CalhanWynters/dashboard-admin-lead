package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

public enum DimensionUnitEnums {
    CM("CM"),   // Centimeters
    MM("MM"),   // Millimeters
    IN("IN"),   // Inches
    FT("FT");   // Feet

    private final String code;

    DimensionUnitEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DimensionUnitEnums fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Dimension unit code cannot be null.");
        }

        for (DimensionUnitEnums unit : values()) {
            if (unit.code.equalsIgnoreCase(code)) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unsupported dimension unit code: " + code);
    }
}
