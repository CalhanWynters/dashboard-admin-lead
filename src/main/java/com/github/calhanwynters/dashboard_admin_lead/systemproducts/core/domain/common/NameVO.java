package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A Value Object representing the name of a product, feature, or other domain concept.
 * Enforces non-null, non-blank, length, and strict content constraints using whitelisting.
 */
public record NameVO(String value) {

    // Whitelist pattern: Allows letters (upper/lower), numbers, spaces, and common punctuation (.,:;!-'")
    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("[a-zA-Z0-9 .,:;!\\-?'\"()]+");
    private static final int MAX_LENGTH = 100;

    /**
     * Compact constructor for validation.
     */
    public NameVO {
        Objects.requireNonNull(value, "Name value cannot be null");

        // Normalize input
        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("Name value cannot be empty or blank");
        }

        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Name value cannot exceed " + MAX_LENGTH + " characters.");
        }

        // --- Cybersecurity Enhancement: Whitelisting ---
        if (!ALLOWED_CHARS_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Name contains forbidden characters. Only letters, numbers, spaces, and common punctuation (including parentheses) are allowed.");
        }
        // ----------------------------------------------

        // The record's internal 'value' component will store the normalized value
        value = trimmedValue;
    }
}