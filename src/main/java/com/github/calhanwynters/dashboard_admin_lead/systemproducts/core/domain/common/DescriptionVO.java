package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Domain value object for product descriptions.
 * Encapsulates validation and normalization rules for a text string.
 */
public record DescriptionVO(String text) {
    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 2000;

    // Whitelist pattern: Allows letters, numbers, spaces, common punctuation (.,:;!?-), newlines (\n), etc.
    private static final String ALLOWED_CHARS_REGEX = "[a-zA-Z0-9 .,:;!\\-?\\n*•\\d()\\[\\]]+";
    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile(ALLOWED_CHARS_REGEX);

    // Constructor with validation and normalization
    public DescriptionVO {
        Objects.requireNonNull(text, "Description value cannot be null");

        // 1. Normalize whitespace and trim
        String normalized = text.strip().replaceAll("\\s+", " ");

        // --- Cybersecurity Enhancement: Whitelisting ---
        if (!ALLOWED_CHARS_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Description contains forbidden characters. Only letters, numbers, spaces, and common punctuation are allowed.");
        }
        // ----------------------------------------------

        // 2. Validate length after normalization
        if (normalized.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Description must be at least " + MIN_LENGTH + " characters long.");
        }

        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Description cannot exceed " + MAX_LENGTH + " characters.");
        }

        // 3. Optional business rules check (forbidden words using the normalized string)
        String[] forbiddenWords = {"forbiddenWord1", "forbiddenWord2"};
        for (String forbidden : forbiddenWords) {
            if (normalized.toLowerCase().contains(forbidden.toLowerCase())) {
                throw new IllegalArgumentException("Description contains forbidden words.");
            }
        }

        // 4. Assign the normalized and validated value to the record component
        text = normalized;
    }

}