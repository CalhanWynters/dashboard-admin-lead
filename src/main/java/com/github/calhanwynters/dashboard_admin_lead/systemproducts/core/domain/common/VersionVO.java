package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

/**
 * Hardened Version Value Object for Java 25.
 * Validated for legacy schema adaptation and overflow protection.
 */
public record VersionVO(int value) {

    // Boundary: Standard starting version
    private static final int MIN_VERSION = 1;

    // Boundary: Logical upper limit (1 Million).
    // Prevents Integer Overflow attacks and ensures database index stability.
    private static final int MAX_VERSION = 1_000_000;

    // Define semantic constants for clarity
    public static final VersionVO INITIAL = new VersionVO(MIN_VERSION);

    /**
     * Compact Constructor for Domain Validation.
     */
    public VersionVO {
        // 1. Size & Boundary Validation
        // Range checking is the 2025 standard for protecting numeric domain types.
        if (value < MIN_VERSION || value > MAX_VERSION) {
            throw new IllegalArgumentException(
                    "Version must be between %d and %d. Received: %d"
                            .formatted(MIN_VERSION, MAX_VERSION, value)
            );
        }
    }

    /**
     * Creates the next sequential version with Overflow Protection.
     */
    public VersionVO next() {
        if (this.value >= MAX_VERSION) {
            throw new IllegalStateException("Maximum version depth reached. Schema rotation required.");
        }
        return new VersionVO(this.value + 1);
    }

    /**
     * Utility for adapting legacy data where version might be null or zero.
     * Enforces the "Always-Valid" domain pattern.
     */
    public static VersionVO of(Integer rawValue) {
        if (rawValue == null || rawValue < MIN_VERSION) {
            return INITIAL;
        }
        return new VersionVO(rawValue);
    }
}
