package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Category Value Object for Java 25.
 * Implements Unicode-aware whitelisting and DoS prevention boundaries.
 */
public record CategoryVO(String value) {

    private static final int MAX_LENGTH = 100;

    /**
     * Lexical Whitelist (Java 25 Optimized):
     * - \p{L}: Any Unicode letter (Essential for global 2025 support)
     * - \p{N}: Any Unicode number
     * - Anchored with ^ and $ to prevent partial injection matches.
     */
    private static final Pattern VALID_CHARS = Pattern.compile("^[\\p{L}\\p{N} ]+$");

    /**
     * Compact Constructor.
     */
    public CategoryVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(value, "Category value must not be null");

        // 2. Pre-Check Size (DoS Mitigation)
        // Reject massive inputs before executing expensive regex or normalization.
        if (value.length() > MAX_LENGTH * 2) {
            throw new IllegalArgumentException("Input raw data exceeds safety buffer limits.");
        }

        // 3. Normalization
        // strip() is the 2025 standard for Unicode-aware whitespace removal.
        // Internal double-space collapse prevents bypass of unique constraints.
        String normalized = value.strip().replaceAll("\\s{2,}", " ");

        // 4. Content Integrity check
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty or blank.");
        }

        // 5. Size & Boundary
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Category length %d exceeds maximum of %d"
                    .formatted(normalized.length(), MAX_LENGTH));
        }

        // 6. Lexical Content (Unicode Whitelisting)
        if (!VALID_CHARS.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Category contains forbidden characters.");
        }

        // 7. Assignment
        value = normalized;
    }
}
