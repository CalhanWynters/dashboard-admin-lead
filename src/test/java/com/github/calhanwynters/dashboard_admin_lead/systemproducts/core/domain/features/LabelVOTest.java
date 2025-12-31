package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LabelVOTest {

    @Test
    @DisplayName("Should create valid LabelVO and normalize whitespace")
    void shouldCreateValidLabelAndNormalize() {
        // Test normalization: strip leading/trailing and collapse internal spaces
        LabelVO label = new LabelVO("  Alpha   Beta-123  ");
        assertEquals("Alpha Beta-123", label.value(), "Value should be trimmed and internal spaces collapsed.");
    }

    @Test
    @DisplayName("Should throw NullPointerException when value is null")
    void shouldThrowNpeOnNull() {
        assertThrows(NullPointerException.class, () -> new LabelVO(null));
    }

    @Test
    @DisplayName("Should reject strings exceeding the DoS safety buffer")
    void shouldRejectDosPayloads() {
        String massiveInput = "a".repeat(41); // MAX_LENGTH (20) * 2 + 1
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new LabelVO(massiveInput));
        assertTrue(exception.getMessage().contains("safety buffer limits"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t\n", "  "})
    @DisplayName("Should reject blank or empty strings after normalization")
    void shouldRejectBlankInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> new LabelVO(input));
    }

    @Test
    @DisplayName("Should reject strings exceeding max length of 20")
    void shouldRejectOverlyLongLabels() {
        String input = "ThisLabelIsExactlyTwentyOneChars"; // 32 chars
        assertThrows(IllegalArgumentException.class, () -> new LabelVO(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Label!",       // FAIL: '!' not in [a-zA-Z0-9 -]
            "Label#1",      // FAIL: '#' not in [a-zA-Z0-9 -]
            "User_Name",    // FAIL: '_' not in [a-zA-Z0-9 -]
            "100%",         // FAIL: '%' not in [a-zA-Z0-9 -]
            "<script>",     // FAIL: '<' and '>' not in [a-zA-Z0-9 -]
            "Item&Prop",    // FAIL: '&' not in [a-zA-Z0-9 -]
            "-StartWith",   // FAIL: Regex requires alphanumeric at start
            "EndWith-"      // FAIL: Regex requires alphanumeric at end
    })
    @DisplayName("Should reject forbidden characters or invalid boundary characters")
    void shouldRejectInvalidLexicalContent(String input) {
        assertThrows(IllegalArgumentException.class, () -> new LabelVO(input));
    }



    @Test
    @DisplayName("Should allow valid alphanumeric strings with internal spaces and hyphens")
    void shouldAllowComplexValidLabels() {
        assertDoesNotThrow(() -> new LabelVO("Product-A 100"));
        assertDoesNotThrow(() -> new LabelVO("Version 2"));
    }
}