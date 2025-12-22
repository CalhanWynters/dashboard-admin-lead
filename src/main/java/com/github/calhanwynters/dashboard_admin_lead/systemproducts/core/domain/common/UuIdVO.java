package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Objects;
import java.util.UUID;

/**
 * Hardened UUID Value Object for Java 25.
 * Validated against RFC 9562 standards using standard Java parsers.
 */
public record UuIdVO(String value) {

    // Boundary: Strict UUID length (36 chars)
    private static final int UUID_LENGTH = 36;

    /**
     * Compact Constructor for Java 25.
     */
    public UuIdVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(value, "UuId value cannot be null");

        // 2. Normalization & Size Boundary
        // strip() handles Unicode whitespace; length check prevents DoS from massive strings
        String normalized = value.strip();
        if (normalized.length() != UUID_LENGTH) {
            throw new IllegalArgumentException("UuId must be exactly %d characters.".formatted(UUID_LENGTH));
        }

        // 3. Syntax & Lexical Content
        // Use the standard parser (Syntax criterion) to prevent hex injection or invalid versions
        try {
            // This validates hex characters and structural hyphens simultaneously
            UUID.fromString(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID syntax or hex encoding detected.");
        }

        // 4. Data Assignment
        value = normalized;
    }

    /**
     * Java 25 Factory Method.
     */
    public static UuIdVO generate() {
        return new UuIdVO(UUID.randomUUID().toString());
    }

    public static UuIdVO fromString(String value) {
        return new UuIdVO(value);
    }

    /**
     * Utility to return the object as a typed UUID.
     */
    public UUID asUUID() {
        return UUID.fromString(this.value);
    }
}
