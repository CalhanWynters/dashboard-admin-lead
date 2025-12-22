package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Updated for Java 25.
 * Benefits from JEP 519 (Compact Object Headers) for reduced memory overhead.
 */
public record LabelVO(String value) {

    // Whitelist pattern: Letters, numbers, spaces, and hyphens.
    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("[a-zA-Z0-9 -]+");
    private static final int MAX_LENGTH = 20;

    /**
     * Compact constructor for Java 25.
     */
    public LabelVO {
        Objects.requireNonNull(value, "Label value cannot be null");

        // 1. Use strip() instead of trim()
        // strip() is the modern standard (since Java 11, prioritized in 2025)
        // as it is Unicode-aware and more performant for basic normalization.
        String normalized = value.strip();

        // 2. Syntax & Size Validation
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Label value cannot be empty or blank");
        }

        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Label value cannot exceed " + MAX_LENGTH + " characters.");
        }

        // 3. Cybersecurity (Lexical Content): Whitelisting
        // In Java 25, Pattern matching is heavily optimized via JIT intrinsics.
        if (!ALLOWED_CHARS_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Label contains forbidden characters.");
        }

        // 4. Update the implicit parameter
        // The compiler automatically performs this.value = value at the end.
        value = normalized;
    }
}
