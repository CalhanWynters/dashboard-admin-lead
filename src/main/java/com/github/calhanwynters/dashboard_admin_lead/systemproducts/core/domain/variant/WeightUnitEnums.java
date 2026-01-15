package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;
import java.util.Arrays;

/**
 * Standardized Weight Units for product variants.
 * Aligned with DomainGuard for the 2026 Edition.
 */
public enum WeightUnitEnums {
    GRAM,
    KILOGRAM,
    POUND,
    OUNCE,
    CARAT,
    TROY_OUNCE;

    /**
     * Safe parser for weight units using DomainGuard.
     * Enforces strict uppercase matching and handles normalization.
     */
    public static WeightUnitEnums fromString(String value) {
        // 1. Existence and Blank check (Throws VAL-010)
        DomainGuard.notBlank(value, "Weight Unit");

        // 2. Normalization
        String normalized = value.strip().toUpperCase();

        // 3. Lookup with standardized error (Throws VAL-004)
        return Arrays.stream(values())
                .filter(unit -> unit.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> {
                    DomainGuard.ensure(false,
                            "Unsupported weight unit: '%s'. Allowed: %s".formatted(value, Arrays.toString(values())),
                            "VAL-004", "SYNTAX");
                    return null; // Unreachable
                });
    }
}
