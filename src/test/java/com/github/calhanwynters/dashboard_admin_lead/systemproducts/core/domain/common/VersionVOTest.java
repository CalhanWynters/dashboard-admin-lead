package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VersionVOTest {

    @Test
    @DisplayName("Should create valid instance for values within boundary [1 - 1,000,000]")
    void acceptsValidVersions() {
        // Test Lower Boundary
        assertDoesNotThrow(() -> new VersionVO(1));

        // Test Nominal Case
        VersionVO vo = new VersionVO(500);
        assertEquals(500, vo.value());

        // Test Upper Boundary
        assertDoesNotThrow(() -> new VersionVO(1_000_000));
    }


    @ParameterizedTest
    @ValueSource(ints = {0, -1, 1_000_001})
    @DisplayName("Should throw IllegalArgumentException for values outside boundaries")
    void rejectsInvalidBoundaries(int invalidValue) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new VersionVO(invalidValue));

        assertTrue(ex.getMessage().contains("Version must be between 1 and 1000000"));
    }

    @Test
    @DisplayName("Should increment version correctly using next()")
    void incrementsVersion() {
        VersionVO current = new VersionVO(1);
        VersionVO next = current.next();

        assertEquals(2, next.value());
        assertNotSame(current, next, "Value Objects should return a new instance on mutation");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when incrementing past MAX_VERSION")
    void preventsOverflowOnNext() {
        VersionVO maxReached = new VersionVO(1_000_000);

        IllegalStateException ex = assertThrows(IllegalStateException.class, maxReached::next);
        assertEquals("Maximum version depth reached. Schema rotation required.", ex.getMessage());
    }

    @Test
    @DisplayName("Factory 'of' should adapt null or zero to INITIAL version")
    void factoryOfAdaptsLegacyData() {
        assertEquals(VersionVO.INITIAL, VersionVO.of(null));
        assertEquals(VersionVO.INITIAL, VersionVO.of(0));
        assertEquals(VersionVO.INITIAL, VersionVO.of(-5));

        VersionVO valid = VersionVO.of(42);
        assertEquals(42, valid.value());
    }

    @Test
    @DisplayName("INITIAL constant should represent the minimum system version")
    void initialConstantIsCorrect() {
        assertEquals(1, VersionVO.INITIAL.value());
    }
}