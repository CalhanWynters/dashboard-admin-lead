package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Value Object for product care instructions.
 * Compliant with 2025 Domain Validation Rubric.
 */
public record CareInstructionVO(String instructions) {

    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 500;

    /**
     * Java 25 Optimized Pattern:
     * - Whitelists alphanumeric, spaces, and specific punctuation.
     * - (?U) enables Unicode-aware character classes for 2025 i18n standards.
     */
    private static final Pattern VALID_CONTENT_PATTERN =
            Pattern.compile("^(?U)[\\p{L}\\p{N} .,:!\\-?\\n*•()\\[\\]]+$");

    /**
     * Compact Constructor.
     */
    public CareInstructionVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(instructions, "Care instructions cannot be null.");

        // 2. Normalization & Pre-validation
        // In a compact constructor, 'instructions' refers to the parameter, not the field.
        instructions = instructions.strip();

        // 3. Size & Boundary (DoS Mitigation)
        if (instructions.length() > MAX_LENGTH * 1.5) {
            throw new IllegalArgumentException("Input raw data exceeds safety buffer.");
        }

        if (instructions.isBlank()) {
            throw new IllegalArgumentException("Instructions cannot be empty.");
        }

        // 4. Lexical Content (Injection Prevention)
        if (!VALID_CONTENT_PATTERN.matcher(instructions).matches()) {
            throw new IllegalArgumentException("Instructions contain forbidden characters.");
        }

        // 5. Boundary Validation (Final Range)
        int length = instructions.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("Length %d is outside allowed range [%d-%d]."
                    .formatted(length, MIN_LENGTH, MAX_LENGTH));
        }

        // 6. Semantics (Business Rules)
        if (!isValidFormat(instructions)) {
            throw new IllegalArgumentException("Instructions must start with a bullet (-, *, •) or '1.'");
        }

        // SUCCESS: At the end of a compact constructor, the current value of the
        // local variable 'instructions' is automatically assigned to the record field.
    }

    private static boolean isValidFormat(String text) {
        if (text.length() < 3) return false;

        return text.startsWith("-") ||
                text.startsWith("*") ||
                text.startsWith("•") ||
                text.startsWith("1.");
    }
}
