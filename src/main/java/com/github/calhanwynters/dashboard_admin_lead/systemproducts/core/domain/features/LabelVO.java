package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Label Value Object for Java 25.
 * Validated against Unicode-aware lexical standards and DoS boundaries.
 */
public record LabelVO(String value) {

    private static final int MAX_LENGTH = 20;

    /**
     * Lexical Whitelist (Java 25 Optimized):
     * ^ : Start anchor
     * [a-zA-Z0-9] : ASCII Alphanumeric
     * (?: [a-zA-Z0-9-]+)* : Allow internal spaces and hyphens, but not at start/end
     * $ : End anchor
     * This prevents leading/trailing space bypasses and double-space injection.
     */
    private static final Pattern ALLOWED_CHARS_PATTERN =
            Pattern.compile("^[a-zA-Z0-9](?:[a-zA-Z0-9 -]*[a-zA-Z0-9])?$");

    /**
     * Static factory method to satisfy Domain Architecture tests.
     */
    public static LabelVO from(String value) {
        return new LabelVO(value);
    }

    /**
     * Compact constructor for Domain Validation.
     */
    public LabelVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(value, "Label value cannot be null");

        // 2. Pre-Check Size (DoS Mitigation)
        // Rejects massive strings before processing regex to prevent ReDoS.
        if (value.length() > MAX_LENGTH * 2) {
            throw new IllegalArgumentException("Input raw data exceeds safety buffer limits.");
        }

        // 3. Normalization
        // strip() handles Unicode whitespace. replaceAll collapses multiple internal spaces.
        String normalized = value.strip().replaceAll("\\s{2,}", " ");

        // 4. Content Existence check
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Label value cannot be empty or blank.");
        }

        // 5. Size & Boundary
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Label value cannot exceed %d characters.".formatted(MAX_LENGTH));
        }

        // 6. Lexical Content (Injection & Syntax Prevention)
        // Ensures no special characters like <, >, &, or SQL control chars.
        if (!ALLOWED_CHARS_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Label contains forbidden characters or invalid spacing.");
        }

        // 7. Assignment
        value = normalized;
    }
}
