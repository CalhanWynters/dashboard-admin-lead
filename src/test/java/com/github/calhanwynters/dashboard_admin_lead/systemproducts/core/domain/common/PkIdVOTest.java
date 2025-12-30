package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PkIdVOTest {

    @Test
    @DisplayName("Should create valid instance for positive long values")
    void acceptsValidPositiveIds() {
        PkIdVO vo = new PkIdVO(12345L);
        assertEquals(12345L, vo.value());
    }

    @Test
    @DisplayName("Should reject null values with specific message")
    void rejectsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new PkIdVO(null));
        assertEquals("Primary Key value cannot be null", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -9999L})
    @DisplayName("Should reject non-positive values (zero and negative)")
    void rejectsNonPositiveValues(long invalidValue) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new PkIdVO(invalidValue));
        assertTrue(ex.getMessage().contains("must be a positive non-zero value"));
    }

    @Test
    @DisplayName("Should reject values exceeding the safety boundary (Long.MAX_VALUE - 1000)")
    void rejectsOverflowSafetyBoundary() {
        // Test exactly at the boundary limit
        long limit = Long.MAX_VALUE - 1000;
        assertDoesNotThrow(() -> new PkIdVO(limit));

        // Test one above the boundary
        long overLimit = limit + 1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new PkIdVO(overLimit));
        assertEquals("Primary Key exceeds safety boundary. Potential overflow or injection detected.", ex.getMessage());
    }

    @Test
    @DisplayName("Factory method 'of' should create valid instance")
    void factoryMethodOfWorks() {
        PkIdVO vo = PkIdVO.of(500L);
        assertEquals(500L, vo.value());
    }

    @Test
    @DisplayName("Should parse valid numeric strings via fromString")
    void fromStringParsesValidNumericInput() {
        PkIdVO vo = PkIdVO.fromString(" 789 "); // Testing strip() logic
        assertEquals(789L, vo.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "12.34", "", " "})
    @DisplayName("Should throw IllegalArgumentException for malformed string IDs")
    void fromStringRejectsMalformedInput(String malformed) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> PkIdVO.fromString(malformed));
        assertEquals("Invalid ID format: must be a valid numeric long.", ex.getMessage());
    }

}
