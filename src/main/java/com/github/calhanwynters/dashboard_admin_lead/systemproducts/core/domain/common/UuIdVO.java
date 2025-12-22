package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Optimized for Java 25.
 * Benefits from JEP 519 (Compact Object Headers) for reduced memory footprint.
 */
public record UuIdVO(String value) {

    // Standard UUID regex (standardized across internal systems)
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    /**
     * Compact Constructor for Java 25.
     * Automatically validates values upon record creation.
     */
    public UuIdVO {
        Objects.requireNonNull(value, "UuId value cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("UuId value cannot be empty or blank");
        }

        if (!UUID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("UuId must be a valid UUID format string.");
        }

        // Value is implicitly assigned to 'this.value' by the compiler
    }

    /**
     * Java 25 Factory Method.
     * Generates a new unique UuIdVO.
     */
    public static UuIdVO generate() {
        return new UuIdVO(UUID.randomUUID().toString());
    }

    public static UuIdVO fromString(String value) {
        return new UuIdVO(value);
    }

    public UUID toUUID() {
        return UUID.fromString(this.value);
    }
}
