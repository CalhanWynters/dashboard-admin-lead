package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hardened Domain Status Enum for Java 25.
 * Implements strict input parsing and domain semantic safety.
 */
public enum StatusEnums {
    ACTIVE,
    DRAFT,
    INACTIVE,
    DISCONTINUED;

    // Defensive Copying: Immutable snapshot of valid names for fast, safe lookups
    private static final Set<String> VALID_NAMES = Arrays.stream(StatusEnums.values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    /**
     * Syntax & Lexical Validation: Safe parser for external inputs.
     * Prevents internal exception leakage and enforces strict whitelisting.
     */
    public static StatusEnums fromString(String value) {
        // 1. Existence & Nullability
        Objects.requireNonNull(value, "Status value cannot be null");

        // 2. Normalization & Size Boundary
        String normalized = value.strip().toUpperCase();
        if (normalized.length() > 20) { // DoS Mitigation: Reject abnormally long status strings
            throw new IllegalArgumentException("Status input exceeds logical boundary.");
        }

        // 3. Lexical Content / Whitelisting
        if (!VALID_NAMES.contains(normalized)) {
            throw new IllegalArgumentException("Invalid Status: '%s'. Allowed values: %s"
                    .formatted(normalized, VALID_NAMES));
        }

        return StatusEnums.valueOf(normalized);
    }

    /**
     * Semantics: Logic to check if the status transition is valid.
     * (Example of Cross-Field/Semantic Consistency within a domain context)
     */
    public boolean canTransitionTo(StatusEnums nextStatus) {
        Objects.requireNonNull(nextStatus, "Target status cannot be null");

        return switch (this) {
            case DRAFT -> true; // Draft can go anywhere
            case ACTIVE -> nextStatus != DRAFT; // Active cannot go back to Draft
            case INACTIVE, DISCONTINUED -> nextStatus == ACTIVE; // Only reactivation allowed
        };
    }
}
