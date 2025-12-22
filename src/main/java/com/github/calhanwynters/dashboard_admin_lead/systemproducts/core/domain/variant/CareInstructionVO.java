package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Value Object for product care instructions.
 * Validated against the 2025 Domain Validation Rubric.
 */
public record CareInstructionVO(String instructions) {

    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 500;

    /**
     * Lexical Whitelist (Java 25 Optimized):
     * ^ and $ anchors ensure the ENTIRE string conforms to the whitelist.
     * Includes support for newlines, bullet points, and basic punctuation.
     * Prevents XSS (<, >) and SQLi (;) by exclusion.
     */
    private static final Pattern ALLOWED_CHARS_PATTERN =
            Pattern.compile("^[a-zA-Z0-9 .,:;!\\-?\\n*•()\\[\\]]+$");

    /**
     * Compact Constructor.
     */
    public CareInstructionVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(instructions, "Instructions cannot be null");

        // 2. Size & Boundary (DoS Mitigation)
        // Reject massive raw payloads before expensive regex or normalization occurs.
        if (instructions.length() > MAX_LENGTH * 2) {
            throw new IllegalArgumentException("Input raw data exceeds safety buffer limits.");
        }

        // 3. Normalization
        // strip() is Unicode-aware (Standard for 2025 internationalization).
        String normalized = instructions.strip();

        // 4. Existence post-normalization
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Instructions cannot be empty or blank.");
        }

        // 5. Lexical Content (Injection Prevention)
        // CRITICAL: The pattern is now anchored to prevent partial-match bypasses.
        if (!ALLOWED_CHARS_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Instructions contain forbidden characters.");
        }

        // 6. Final Size check
        int length = normalized.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Instructions must be between %d and %d chars. Current: %d"
                            .formatted(MIN_LENGTH, MAX_LENGTH, length)
            );
        }

        // 7. Semantics (Business Rule Validation)
        // Validates that the data makes logical sense as an "Instruction".
        if (!isFormattedCorrectly(normalized)) {
            throw new IllegalArgumentException("Instructions must start with a bullet (*, -, •) or numbering (1.).");
        }

        // Assign the cleaned, validated value
        instructions = normalized;
    }

    private static boolean isFormattedCorrectly(String text) {
        return text.startsWith("-") ||
                text.startsWith("*") ||
                text.startsWith("•") ||
                text.startsWith("1.");
    }
}
