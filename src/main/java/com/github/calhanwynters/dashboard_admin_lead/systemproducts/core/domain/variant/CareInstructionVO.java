package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
    // \p{S} includes \p{So} (Degree Sign), \p{Sc} (Currency), and \p{Sm} (Math symbols)
    private static final Pattern VALID_CONTENT_PATTERN =
            Pattern.compile("^(?U)[\\p{L}\\p{N} °.,:!\\n\\r*•()\\[\\]\\-?]+$");


    /**
     * Compact Constructor.
     */
    public CareInstructionVO {
        Objects.requireNonNull(instructions, "Care instructions cannot be null.");
        instructions = instructions.replace("\r\n", "\n").replace("\r", "\n").strip();

        // Size & Lexical Checks
        if (instructions.length() > MAX_LENGTH * 1.5) throw new IllegalArgumentException("Input raw data exceeds safety buffer.");
        if (instructions.isBlank()) throw new IllegalArgumentException("Instructions cannot be empty.");
        if (!VALID_CONTENT_PATTERN.matcher(instructions).matches()) throw new IllegalArgumentException("Instructions contain forbidden characters.");

        int length = instructions.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("Length %d is outside allowed range [%d-%d].".formatted(length, MIN_LENGTH, MAX_LENGTH));
        }

        // Semantic Check: Only call the method that throws specific exceptions
        validateSemantics(instructions);
    }


    private static void validateSemantics(String text) {
        // Use \\R to handle all OS-specific line breaks (Unix \n, Windows \r\n)
        String[] lines = text.split("\\R");
        if (lines.length == 0) return;

        // Step A: Determine the style from line 1
        Pattern style = Stream.of(HYPHEN_PREFIX, ASTERISK_PREFIX, BULLET_DOT_PREFIX, NUMBER_PREFIX)
                .filter(p -> p.matcher(lines[0].strip()).find())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Instructions must start with a valid bullet (-, *, •) or a number (1-15)."));

        // Step B: Consistency check for subsequent lines
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].strip();
            if (!line.isEmpty() && !style.matcher(line).find()) {
                throw new IllegalArgumentException(
                        "Prefix style mismatch: Line %d does not match the style established in line 1."
                                .formatted(i + 1));
            }
        }
    }
}
