package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class StatusEnumsTest {

    @ParameterizedTest
    @ValueSource(strings = {"ACTIVE", "  active  ", "Draft", "inactive"})
    @DisplayName("Should parse valid status strings regardless of case or surrounding whitespace")
    void parsesValidStrings(String input) {
        StatusEnums result = StatusEnums.fromString(input);
        assertNotNull(result);
        assertEquals(input.strip().toUpperCase(), result.name());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null input")
    void rejectsNull() {
        assertThrows(NullPointerException.class, () -> StatusEnums.fromString(null));
    }

    @Test
    @DisplayName("Should trigger DoS protection for abnormally long strings")
    void rejectsTooLongStrings() {
        String longInput = "A".repeat(21);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> StatusEnums.fromString(longInput));
        assertEquals("Status input exceeds logical boundary.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject invalid status names with helpful message")
    void rejectsInvalidStatus() {
        String invalid = "DELETED";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> StatusEnums.fromString(invalid));

        assertTrue(ex.getMessage().contains("Allowed values:"));
        assertTrue(ex.getMessage().contains("ACTIVE"));
    }

    @ParameterizedTest
    @EnumSource(StatusEnums.class)
    @DisplayName("Draft should be allowed to transition to any other status")
    void draftCanTransitionToAny(StatusEnums target) {
        assertTrue(StatusEnums.DRAFT.canTransitionTo(target));
    }

    @ParameterizedTest
    @CsvSource({
            "ACTIVE, INACTIVE, true",
            "ACTIVE, DISCONTINUED, true",
            "ACTIVE, DRAFT, false",
            "INACTIVE, ACTIVE, true",
            "INACTIVE, DISCONTINUED, false",
            "DISCONTINUED, ACTIVE, true",
            "DISCONTINUED, DRAFT, false"
    })
    @DisplayName("Should enforce domain-specific transition rules")
    void enforcesTransitionRules(StatusEnums current, StatusEnums next, boolean expected) {
        assertEquals(expected, current.canTransitionTo(next),
                "Transition from %s to %s should be %b".formatted(current, next, expected));
    }

    @Test
    @DisplayName("Transition check should reject null target status")
    void transitionRejectsNull() {
        assertThrows(NullPointerException.class, () -> StatusEnums.ACTIVE.canTransitionTo(null));
    }
}
