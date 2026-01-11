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
    private static final Pattern HYPHEN_PREFIX = Pattern.compile("^-");
    private static final Pattern ASTERISK_PREFIX = Pattern.compile("^\\*");
    private static final Pattern BULLET_DOT_PREFIX = Pattern.compile("^•");
    private static final Pattern NUMBER_PREFIX = Pattern.compile("^(?:[1-9]|1[0-5])\\.");


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
            throw new IllegalArgumentException("Instructions must start with a bullet (-, *, •) or a number (1-15).");
        }

        // SUCCESS: At the end of a compact constructor, the current value of the
        // local variable 'instructions' is automatically assigned to the record field.
    }

    private static boolean isValidFormat(String text) {
        if (text == null || text.isBlank()) return false;

        String[] lines = text.split("\\R");
        String firstLine = lines[0].strip();

        // 1. Detect the SPECIFIC style used in the first line
        Pattern activeStyle = null;
        if (HYPHEN_PREFIX.matcher(firstLine).find()) activeStyle = HYPHEN_PREFIX;
        else if (ASTERISK_PREFIX.matcher(firstLine).find()) activeStyle = ASTERISK_PREFIX;
        else if (BULLET_DOT_PREFIX.matcher(firstLine).find()) activeStyle = BULLET_DOT_PREFIX;
        else if (NUMBER_PREFIX.matcher(firstLine).find()) activeStyle = NUMBER_PREFIX;

        if (activeStyle == null) return false;

        // 2. Enforce that SAME style for all subsequent lines
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].strip();
            if (line.isEmpty()) continue;

            // If a line uses a different bullet or number format, it fails
            if (!activeStyle.matcher(line).find()) return false;
        }

        return true;
    }



}
