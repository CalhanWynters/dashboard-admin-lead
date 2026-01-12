package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened SKU Value Object for Java 25.
 * Enforces canonical uppercase format and DoS-resilient whitelisting.
 * Validated against the 2025 Domain Validation Rubric.
 */
public record SkuVO(String sku) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    /**
     * Lexical Whitelist (Java 25 Optimized):
     * - (?U) enables Unicode property support.
     * - Whitelists A-Z, 0-9, hyphen, and underscore.
     */
    private static final Pattern ALLOWED_CHARS_PATTERN =
            Pattern.compile("^(?U)[A-Z0-9_-]+$");

    /**
     * Compact Constructor.
     */
    public SkuVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(sku, "SKU cannot be null.");

        // 2. Normalization (Immediate)
        // 2025 Standard: Normalize before any size or lexical checks.
        sku = sku.strip().toUpperCase();

        // 3. Size & Boundary (DoS Mitigation)
        // Rejecting early prevents complex regex execution on malicious payloads.
        if (sku.length() > MAX_LENGTH + 10) {
            throw new IllegalArgumentException("SKU raw length exceeds security buffer.");
        }

        if (sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be empty.");
        }

        // 4. Lexical Content (Injection Prevention)
        if (!ALLOWED_CHARS_PATTERN.matcher(sku).matches()) {
            throw new IllegalArgumentException("SKU contains forbidden characters.");
        }

        // 5. Final Range Check
        int length = sku.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("SKU length %d is outside range [%d-%d]."
                    .formatted(length, MIN_LENGTH, MAX_LENGTH));
        }

        // 6. Semantics (Business Logic)
        // Prevention of "Floating Separators"
        if (sku.startsWith("-") || sku.startsWith("_") || sku.endsWith("-") || sku.endsWith("_")) {
            throw new IllegalArgumentException("SKU cannot start or end with a separator.");
        }

        // Prevention of "Consecutive Separators" (Anti-Fragmentation)
        // Ensures SKU is clean for barcode generation and URL pathing.
        if (sku.contains("--") || sku.contains("__") || sku.contains("-_") || sku.contains("_-")) {
            throw new IllegalArgumentException("SKU cannot contain consecutive or mixed separators.");
        }



        // Final value is automatically assigned to the record field.
    }
}
