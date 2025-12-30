package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Value Object for last modification timestamps.
 * Hardened for Java 25 environments with Clock-injection for testing
 * and nanosecond precision protection.
 */
public record LastModifiedVO(OffsetDateTime value) {

    // Boundary: Logical lower bound (System Epoch)
    private static final OffsetDateTime MIN_SYSTEM_DATE = OffsetDateTime.parse("2025-01-01T00:00:00Z");

    // Boundary: Safety buffer for clock drift (e.g., 5 minutes)
    private static final long CLOCK_DRIFT_BUFFER_SECONDS = 30;

    public LastModifiedVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(value, "Last modified date cannot be null");

        // 2. Syntax: Standardize precision (Truncate to nanoseconds or microseconds)
        // Prevents database-specific truncation errors causing equality mismatches
        value = value.truncatedTo(ChronoUnit.NANOS);

        // 3. Semantics & Security: Range Validation
        OffsetDateTime now = OffsetDateTime.now();

        // Logical check: Far Past
        if (value.isBefore(MIN_SYSTEM_DATE)) {
            throw new IllegalArgumentException("Date is before system epoch (%s)".formatted(MIN_SYSTEM_DATE));
        }

        // Logical check: Future date with Drift Buffer
        if (value.isAfter(now.plusSeconds(CLOCK_DRIFT_BUFFER_SECONDS))) {
            throw new IllegalArgumentException("Date cannot be in the future (beyond clock skew buffer).");
        }
    }

    /**
     * Factory method using system UTC clock.
     */
    public static LastModifiedVO now() {
        return new LastModifiedVO(OffsetDateTime.now(Clock.systemUTC()));
    }

    /**
     * Testing/Mocking Factory: Allows injection of fixed clocks for predictable audits.
     */
    public static LastModifiedVO now(Clock clock) {
        return new LastModifiedVO(OffsetDateTime.now(clock));
    }
}
