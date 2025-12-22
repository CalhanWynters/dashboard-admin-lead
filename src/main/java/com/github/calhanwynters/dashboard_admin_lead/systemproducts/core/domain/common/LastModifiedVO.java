package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Value Object representing the last modification timestamp of a system product.
 * Ensures the date is never null and not in the future.
 */
public record LastModifiedVO(OffsetDateTime value) {

    private static final OffsetDateTime MIN_SYSTEM_DATE = OffsetDateTime.parse("2025-01-01T00:00:00Z");

    public LastModifiedVO {
        Objects.requireNonNull(value, "Last modified date cannot be null");

        // Semantic: No future dates
        if (value.isAfter(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Date cannot be in the future.");
        }

        // Semantic: Logical lower bound
        if (value.isBefore(MIN_SYSTEM_DATE)) {
            throw new IllegalArgumentException("Date is too far in the past.");
        }
    }

    /**
     * Factory method to create a "now" timestamp.
     */
    public static LastModifiedVO now() {
        return new LastModifiedVO(OffsetDateTime.now());
    }
}
