package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened SKU Value Object for Java 25.
 * Enforces canonical uppercase format and DoS-resilient whitelisting.
 */
public record SkuVO(String sku) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    /**
     * Lexical Whitelist (Java 25 Optimized):
     * ^ and $ anchors ensure the entire string is validated.
     * Restricts to Alphanumeric, Hyphens, and Underscores.
     */
    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("^[A-Z0-9_-]+$");

    /**
     * Compact Constructor for Domain Validation.
     */
    public SkuVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(sku, "SKU cannot be null");

        // 2. Size & Boundary (DoS Mitigation)
        // Rejects massive raw payloads before expensive regex or normalization occurs.
        if (sku.length() > MAX_LENGTH * 2) {
            throw new IllegalArgumentException("Input raw data exceeds safety buffer limits.");
        }

        // 3. Normalization & Semantics
        // strip() is Unicode-aware; toUpperCase() ensures canonical lookup format.
        String normalized = sku.strip().toUpperCase();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be empty or blank.");
        }

        // 4. Size check post-normalization
        int length = normalized.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "SKU must be between %d and %d chars. Current: %d"
                            .formatted(MIN_LENGTH, MAX_LENGTH, length)
            );
        }

        // 5. Lexical Content (Injection Prevention)
        // Ensures no special characters, spaces, or lower-case letters.
        if (!ALLOWED_CHARS_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("SKU contains forbidden characters. Use uppercase letters, numbers, hyphens, and underscores only.");
        }

        // 6. Data Assignment
        sku = normalized;
    }
}
