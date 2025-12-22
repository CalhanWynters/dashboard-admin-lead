package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Objects;

/**
 * Updated for Java 25.
 * Standardizes relational database Primary Key logic.
 */
public record PkIdVO(Long value) {

    /**
     * Compact Constructor for Java 25.
     * Enforces the "Always-Valid" domain pattern.
     */
    public PkIdVO {
        // Semantic & Syntax Validation
        Objects.requireNonNull(value, "Primary Key value cannot be null");

        if (value <= 0) {
            throw new IllegalArgumentException("Primary Key must be a positive non-zero value.");
        }
    }

    /**
     * Factory method for creating an ID from a raw long.
     */
    public static PkIdVO of(long value) {
        return new PkIdVO(value);
    }
}
