package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

/**
 * Updated for Java 25.
 * Leverages JEP 519 (Compact Object Headers) for minimal memory footprint.
 */
public record VersionVO(int value) {

    // Define semantic constants for clarity
    public static final VersionVO INITIAL = new VersionVO(1);

    /**
     * Compact Constructor.
     * Enforces domain semantics: versions are non-negative and non-zero.
     */
    public VersionVO {
        if (value < 1) {
            throw new IllegalArgumentException("Version must be a positive integer starting from 1");
        }
    }

    /**
     * Creates a new instance representing the next sequential version.
     * In Java 25, this benefits from optimized record instantiation.
     */
    public VersionVO next() {
        return new VersionVO(this.value + 1);
    }
}
