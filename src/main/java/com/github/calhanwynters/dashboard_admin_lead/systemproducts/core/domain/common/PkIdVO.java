package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;
import java.util.Objects;

/**
 * Value Object for Primary Keys.
 * Hardened for Java 21/25 (2026 Edition) using DomainGuard.
 */
public record PkIdVO(Long value) {

    // Boundary: Maximum logical value for a Long PK to prevent overflow exploitation
    private static final long MAX_PK_VALUE = Long.MAX_VALUE - 1000;

    /**
     * Compact Constructor enforcing positive range and safety boundaries.
     */
    public PkIdVO {
        // 1. Existence
        DomainGuard.notNull(value, "Primary Key");

        // 2. Positivity & Range Validation (Throws VAL-013)
        DomainGuard.positive(value, "Primary Key");

        // 3. Safety Boundary Validation (Throws VAL-007)
        DomainGuard.ensure(
                value <= MAX_PK_VALUE,
                "Primary Key exceeds safety boundary. Potential overflow or injection detected.",
                "VAL-007", "RANGE"
        );
    }

    /**
     * Factory method for creating an ID from a raw long.
     */
    public static PkIdVO of(long value) {
        return new PkIdVO(value);
    }

    /**
     * Overloaded factory method to satisfy test architecture.
     */
    public static PkIdVO fromString(long value) {
        return new PkIdVO(value);
    }

    /**
     * Helper for string-based inputs (API Gateways/Web layers).
     */
    public static PkIdVO fromString(String rawValue) {
        DomainGuard.notBlank(rawValue, "Input ID String");
        try {
            return new PkIdVO(Long.parseLong(rawValue.strip()));
        } catch (NumberFormatException e) {
            // Re-wrapping in a DomainRuleViolationException via ensure logic
            DomainGuard.ensure(false, "Invalid ID format: must be a valid numeric long.", "VAL-004", "SYNTAX");
            return null; // Unreachable
        }
    }
}
