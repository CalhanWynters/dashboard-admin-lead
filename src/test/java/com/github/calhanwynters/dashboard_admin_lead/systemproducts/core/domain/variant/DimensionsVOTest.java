package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DimensionsVO 2026 Integrity Suite")
class DimensionsVOTest {

    private static final String UNIT = "CENTIMETER"; // Example Enum value

    // --- CATEGORY: EXISTENCE (NULL CHECKS) ---
    @Test
    @DisplayName("Should throw NullPointerException when any field is null")
    void testNullFields() {
        assertThatThrownBy(() -> new DimensionsVO(null, new BigDecimal("10"), new BigDecimal("10"), DimensionUnitEnums.CM))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1E+2", "1,000", "12.5a", "10.0.0"})
    @DisplayName("Should reject non-standard numeric formats")
    void testInvalidLexicalFormats(String invalidInput) {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> {
                    // We now test the entry point (of) instead of the constructor
                    // to catch formatting issues before parsing occurs.
                    DimensionsVO.of(invalidInput, "10.0", "10.0", DimensionUnitEnums.CM);
                }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be a plain numeric format");
    }



    // --- CATEGORY: SIZE BOUNDARIES (SECURITY) ---
    @Test
    @DisplayName("Should reject strings longer than 16 characters (Buffer/Storage Security)")
    void testInputLengthBoundary() {
        // We must pass this as a STRING to test the length-security boundary
        String overlyLongInput = "12345.678901234567"; // 18 chars

        assertThatThrownBy(() ->
                DimensionsVO.of(overlyLongInput, "1.0", "1.0", DimensionUnitEnums.CM)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds security length boundary");
    }


    @Test
    @DisplayName("Should reject absolute values greater than 10,000")
    void testAbsoluteValueBoundary() {
        BigDecimal excessive = new BigDecimal("10000.01");
        assertThatThrownBy(() -> new DimensionsVO(excessive, excessive, excessive, DimensionUnitEnums.CM))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("below 10000.0");
    }


    // --- CATEGORY: PHYSICAL SEMANTICS ---
    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.1", "-100"})
    @DisplayName("Should reject non-positive dimensions (0 or negative)")
    void testPhysicalSemantics(String invalidVal) {
        BigDecimal val = new BigDecimal(invalidVal);
        assertThatThrownBy(() -> new DimensionsVO(val, val, val, DimensionUnitEnums.CM))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be positive");
    }

    // --- CATEGORY: PRECISION ---
    @Test
    @DisplayName("Should support up to 10 decimal places within 16-character limit")
    void testPrecisionSupport() {
        BigDecimal highPrecision = new BigDecimal("1.1234567890"); // 10 decimals, 12 chars total
        DimensionsVO vo = new DimensionsVO(highPrecision, highPrecision, highPrecision, DimensionUnitEnums.CM);
        assertThat(vo.length().scale()).isEqualTo(10);
    }

    // --- CATEGORY: RECORD CONTRACT ---
    @Test
    @DisplayName("Verify record immutability and equality contract")
    void testRecordContract() {
        BigDecimal val = new BigDecimal("10.5");
        DimensionsVO vo1 = new DimensionsVO(val, val, val, DimensionUnitEnums.CM);
        DimensionsVO vo2 = new DimensionsVO(val, val, val, DimensionUnitEnums.CM);

        assertThat(vo1).isEqualTo(vo2);
        assertThat(vo1.hashCode()).isEqualTo(vo2.hashCode());
        // Verify immutability: Records have no setters by design
        assertThat(vo1.getClass().getMethods()).noneMatch(m -> m.getName().startsWith("set"));
    }

    // --- CATEGORY: NORMAL INPUTS ---
    @Test
    @DisplayName("Should successfully create record with valid data")
    void testNormalInput() {
        DimensionsVO vo = new DimensionsVO(new BigDecimal("10.5"), new BigDecimal("20.0"), new BigDecimal("5"), DimensionUnitEnums.CM);
        assertThat(vo.length()).isEqualByComparingTo("10.5");
    }

}
