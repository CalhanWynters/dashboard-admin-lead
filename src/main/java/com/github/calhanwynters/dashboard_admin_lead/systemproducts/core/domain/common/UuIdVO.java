package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;
import java.util.UUID;

/**
 * Hardened UUID Value Object for Java 21/25 (2026 Edition).
 * Validated against RFC 9562 standards using DomainGuard.
 */
public record UuIdVO(String value) {

    private static final int UUID_LENGTH = 36;

    /**
     * Compact Constructor enforcing RFC 9562 UUID standards.
     */
    public UuIdVO {
        // 1. Existence and Initial Content (Throws VAL-010)
        DomainGuard.notBlank(value, "UUID");

        // 2. Normalization & Size Boundary (Throws VAL-002)
        String normalized = value.strip();
        DomainGuard.ensure(
                normalized.length() == UUID_LENGTH,
                "UUID must be exactly %d characters.".formatted(UUID_LENGTH),
                "VAL-002", "SIZE"
        );

        // 3. Syntax & Lexical Content (Throws VAL-004)
        try {
            // Validates hex characters and structural hyphens via standard parser
            UUID.fromString(normalized);
        } catch (IllegalArgumentException e) {
            DomainGuard.ensure(
                    false,
                    "Invalid UUID syntax or hex encoding detected.",
                    "VAL-004", "SYNTAX"
            );
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
