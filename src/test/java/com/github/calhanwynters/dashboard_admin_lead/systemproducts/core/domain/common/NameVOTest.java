package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class NameVOTest {

    @Test
    @DisplayName("Should normalize and accept valid alphanumeric names")
    void acceptsValidNames() {
        NameVO name = new NameVO("  John Doe  ");
        assertEquals("John Doe", name.value(), "Should strip whitespace");

        NameVO withNumbers = new NameVO("Product 123");
        assertEquals("Product 123", withNumbers.value());
    }

    @Test
    @DisplayName("Should collapse multiple internal spaces into a single space")
    void collapsesInternalSpaces() {
        NameVO name = new NameVO("John    Middle    Doe");
        assertEquals("John Middle Doe", name.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Müeller",   // German
            "Σοφία",     // Greek
            "李华",       // Chinese (2 chars)
            "太郎",       // Japanese (2 chars)
            "박지성"      // Korean (3 chars)
    })
    @DisplayName("Should support global Unicode characters (2025 Standard)")
    void supportsUnicodeCharacters(String unicodeName) {
        assertDoesNotThrow(() -> new NameVO(unicodeName));
    }

    @Test
    @DisplayName("Should reject null values with specific message")
    void rejectsNull() {
        Throwable ex = assertThrows(NullPointerException.class, () -> new NameVO(null));
        assertEquals("Name value cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject blank or empty strings after stripping")
    void rejectsBlankStrings() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new NameVO("   "));
        assertEquals("Name cannot be empty or blank.", ex.getMessage());
    }

    @Test
    @DisplayName("Should enforce length boundaries [2-100]")
    void enforcesLengthBoundaries() {
        // Too short
        assertThrows(IllegalArgumentException.class, () -> new NameVO("A"));

        // Too long (101 chars)
        String longName = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> new NameVO(longName));
    }

    @Test
    @DisplayName("Should trigger DoS safety buffer for inputs slightly over the limit")
    void triggersDosSafetyBuffer() {
        // MAX_LENGTH (100) + 10 = 110. 111 should fail.
        String massiveInput = "a".repeat(111);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new NameVO(massiveInput));

        assertEquals("Input exceeds safety buffer limits.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Name<script>", "Name{json}", "Name#Hash", "Name%Percent"})
    @DisplayName("Should reject forbidden special characters")
    void rejectsForbiddenCharacters(String invalid) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new NameVO(invalid));

        // Ensures we failed due to the Lexical check, not the Size/Buffer check
        assertEquals("Name contains forbidden characters or invalid Unicode sequences.",
                ex.getMessage());
    }



}
