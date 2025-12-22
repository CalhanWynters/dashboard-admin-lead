package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Objects;

/**
 * Value Object for Primary Keys.
 * Hardened for Java 25 to prevent boundary overflow and logic errors.
 */
public record PkIdVO(Long value) {

    // Boundary: Maximum logical value for a Long PK to prevent overflow exploitation
    // and reserve space for metadata flags if needed.
    private static final long MAX_PK_VALUE = Long.MAX_VALUE - 1000;

    /**
     * Compact Constructor.
     */
    public PkIdVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(value, "Primary Key value cannot be null");

        // 2. Size & Boundary Validation
        // Reject zero and negative values immediately (Semantics)
        if (value <= 0) {
            throw new IllegalArgumentException("Primary Key must be a positive non-zero value. Received: %d".formatted(value));
        }

        // 3. Security Boundary: Overflow Prevention
        // In 2025, validating upper bounds on IDs is a defense-in-depth measure
        // against "Off-by-One" errors in C-based database drivers or buffer overflows.
        if (value > MAX_PK_VALUE) {
            throw new IllegalArgumentException("Primary Key exceeds safety boundary. Potential overflow or injection detected.");
        }
    }

    /**
     * Factory method for creating an ID from a raw long.
     */
    public static PkIdVO of(long value) {
        return new PkIdVO(value);
    }

    /**
     * Helper for string-based inputs (API Gateways/Web layers).
     * Validates syntax using standard Java parsers.
     */
    public static PkIdVO fromString(String rawValue) {
        Objects.requireNonNull(rawValue, "Input string cannot be null");
        try {
            return new PkIdVO(Long.parseLong(rawValue.strip()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format: must be a valid numeric long.");
        }
    }
}
