package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UuIdVOTest {

    @Test
    @DisplayName("Should create valid instance from standard UUID string")
    void acceptsValidUuid() {
        String raw = UUID.randomUUID().toString();
        UuIdVO vo = new UuIdVO(raw);
        assertEquals(raw, vo.value());
    }

    @Test
    @DisplayName("Should normalize by stripping surrounding whitespace")
    void normalizesWhitespace() {
        String raw = UUID.randomUUID().toString();
        UuIdVO vo = new UuIdVO("  " + raw + "  ");
        assertEquals(raw, vo.value());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null input")
    void rejectsNull() {
        assertThrows(NullPointerException.class, () -> new UuIdVO(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "550e8400-e29b-41d4-a716-44665544000",  // 35 chars (Too short)
            "550e8400-e29b-41d4-a716-4466554400000", // 37 chars (Too long)
            "not-a-uuid-length-at-all-clearly-bad"   // 36 chars but invalid
    })
    @DisplayName("Should reject strings that are not exactly 36 characters")
    void rejectsInvalidLength(String invalid) {
        // Only trigger the length exception if the length is actually != 36
        if (invalid.length() != 36) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new UuIdVO(invalid));
            assertEquals("UuId must be exactly 36 characters.", ex.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "550e8400-e29b-41d4-a716-44665544000G", // Invalid hex 'G'
            "550e8400ge29bg41d4ga716g446655440000", // No hyphens
            "z50e8400-e29b-41d4-a716-446655440000"  // Invalid hex 'z'
    })
    @DisplayName("Should reject strings with invalid hex or missing hyphens")
    void rejectsInvalidSyntax(String invalid) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new UuIdVO(invalid));
        assertEquals("Invalid UUID syntax or hex encoding detected.", ex.getMessage());
    }

    @Test
    @DisplayName("Generate factory should produce valid, unique UuIdVO instances")
    void generateFactoryWorks() {
        UuIdVO vo1 = UuIdVO.generate();
        UuIdVO vo2 = UuIdVO.generate();

        assertNotNull(vo1.value());
        assertEquals(36, vo1.value().length());
        assertNotEquals(vo1, vo2, "Generated UUIDs must be unique");
    }

    @Test
    @DisplayName("asUUID should return a functional java.util.UUID object")
    void convertsToNativeUuid() {
        UuIdVO vo = UuIdVO.generate();
        UUID nativeUuid = vo.asUUID();

        assertEquals(vo.value(), nativeUuid.toString());
    }
}
