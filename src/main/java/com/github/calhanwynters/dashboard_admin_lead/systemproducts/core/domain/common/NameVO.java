package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Value Object for Domain Names.
 * Validated for Java 25 standards including Unicode support and DoS resilience.
 */
public record NameVO(String value) {

    // Boundary Constants
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    /**
     * Lexical Whitelist (Java 25 Optimized):
     * - \p{L}: Any Unicode letter (Essential for global 2025 apps)
     * - \p{N}: Any Unicode number
     * - \s: Limited whitespace
     * - Anchored with ^ and $ to prevent partial injection matches.
     */
    private static final Pattern ALLOWED_CHARS_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N} .,:;!\\-?'\"()]+$");

    /**
     * Compact Constructor.
     */
    public NameVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(value, "Name value cannot be null");

        // 2. Pre-Check Size (DoS mitigation: Stop processing if massive)
        if (value.length() > MAX_LENGTH * 2) {
            throw new IllegalArgumentException("Input exceeds safety buffer limits.");
        }

        // 3. Normalization
        // strip() is Unicode-aware (preferred over trim()); collapse internal double spaces.
        String normalized = value.strip().replaceAll("\\s{2,}", " ");

        // 4. Existence check post-normalization
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty or blank.");
        }

        // 5. Size & Boundary
        int length = normalized.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Name length %d is outside allowed range [%d-%d]".formatted(length, MIN_LENGTH, MAX_LENGTH)
            );
        }

        // 6. Lexical Content (Injection Prevention)
        if (!ALLOWED_CHARS_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Name contains forbidden characters or invalid Unicode sequences.");
        }

        // 7. Data Assignment
        value = normalized;
    }
}
