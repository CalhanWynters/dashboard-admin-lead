package com.github.calhanwynters.dashboard_admin_lead.systemproductcodes.core.domain.sku;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened SKU Value Object for 2026.
 * Enforces: Human-readability, AI-compatibility, and Universal Formatting.
 * Integrity Rules: No leading zeros, Dash-only separators, Look-alike Ambiguity protection.
 *
 * -----------------------------------------------------------------------------------------
 * IMPLEMENTATION NOTE FOR INVENTORY MODULE:
 * Operational lifecycle transitions (e.g., flipping a SKU to 'Z-' when stock hits zero)
 * are decoupled from this Identity VO and must be implemented in the [Inventory Module].
 *
 * TRANSFER LOGIC REFERENCE:
 * The Inventory Module should implement a service to prepend status prefixes (X, Z, 1).
 * To maintain the 2026 sorting hierarchy, the logic must ensure:
 *   if (firstChar == 'X' || firstChar == 'Z' || firstChar == '1') {
 *       // Status prefix must be followed by a dash (e.g., Z-CB-E141)
 *       // to ensure alphabetical isolation in sorted reports.
 *   }
 * -----------------------------------------------------------------------------------------
 */
public record SkuVO(String sku) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 30;

    /**
     * Lexical Whitelist:
     * - Only A-Z, 0-9, and Hyphen (-).
     * - Underscores removed for 2026 URL-safety and Barcode compatibility.
     */
    private static final Pattern ALLOWED_CHARS_PATTERN =
            Pattern.compile("^[A-Z0-9-]+$");

    public SkuVO {
        // 1. Nullability & Normalization
        Objects.requireNonNull(sku, "SKU cannot be null.");
        sku = sku.strip().toUpperCase();

        // 2. Security & Basic Presence
        if (sku.length() > MAX_LENGTH + 10) {
            throw new IllegalArgumentException("SKU raw length exceeds security buffer.");
        }
        if (sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be empty.");
        }

        // 3. Lexical Content
        if (!ALLOWED_CHARS_PATTERN.matcher(sku).matches()) {
            throw new IllegalArgumentException("SKU contains forbidden characters. Use A-Z, 0-9, and dashes only.");
        }

        // 4. Data Integrity: Leading Zero Prevention
        // Prevents Excel/CSV tools from stripping '0' and corrupting primary keys in 2026.
        if (sku.startsWith("0")) {
            throw new IllegalArgumentException("SKU cannot start with zero (Data tool safety rule).");
        }

        // 5. Human Error Mitigation: Look-alike Ambiguity
        // Rejection logic for ambiguous character pairs (0/O and 1/I).
        if (sku.contains("0") && sku.contains("O")) {
            throw new IllegalArgumentException("Ambiguity Error: SKU contains both '0' and 'O'.");
        }
        if (sku.contains("1") && sku.contains("I")) {
            throw new IllegalArgumentException("Ambiguity Error: SKU contains both '1' and 'I'.");
        }

        // 6. Structural Integrity
        if (sku.startsWith("-") || sku.endsWith("-")) {
            throw new IllegalArgumentException("SKU cannot start or end with a separator.");
        }
        if (sku.contains("--")) {
            throw new IllegalArgumentException("SKU cannot contain consecutive separators.");
        }

        // 7. Domain Rule: Structural Segmentation
        // Enforces the Vendor-Style-Variant logic required for sorting.
        if (!sku.contains("-")) {
            throw new IllegalArgumentException("SKU must contain a hyphen to separate attributes (e.g., VENDOR-ID).");
        }

        // 8. Final Range Check
        int length = sku.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("SKU length %d is outside range [%d-%d]."
                    .formatted(length, MIN_LENGTH, MAX_LENGTH));
        }
    }
}
