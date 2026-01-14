package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeightVOTest {

    private static final String VALID_AMOUNT = "10.5";
    private static final WeightUnitEnums VALID_UNIT = WeightUnitEnums.GRAM;

    @Nested
    @DisplayName("Existence & Nullability")
    class NullabilityTests {
        @Test
        void shouldThrowExceptionWhenAmountIsNull() {
            assertThatThrownBy(() -> new WeightVO(null, VALID_UNIT))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void shouldThrowExceptionWhenUnitIsNull() {
            assertThatThrownBy(() -> new WeightVO(new BigDecimal(VALID_AMOUNT), null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Size & Boundary (32 Character Limit)")
    class BoundaryTests {
        @Test
        void shouldAcceptAmountAtMaxBoundary() {
            // 32 characters: 26 digits + dot + 5 decimals
            String maxLenStr = "12345678901234567890123456.12345";
            assertThat(maxLenStr).hasSize(32);

            WeightVO vo = new WeightVO(new BigDecimal(maxLenStr), VALID_UNIT);
            assertThat(vo.amount()).isEqualByComparingTo(maxLenStr);
        }

        @Test
        void shouldRejectAmountExceedingMaxBoundary() {
            // 33 characters
            String tooLong = "123456789012345678901234567.12345";
            assertThatThrownBy(() -> new WeightVO(new BigDecimal(tooLong), VALID_UNIT))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "10.5A",    // Alphabetic
            "10..5"     // Malformed decimal
    })
    void shouldRejectMalformedStrings(String malformed) {
        // These will likely fail at the BigDecimal constructor level before the regex
        assertThatThrownBy(() -> new WeightVO(new BigDecimal(malformed), VALID_UNIT))
                .isInstanceOf(NumberFormatException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1.2e3",    // Scientific notation
            ".500"      // Leading dot
    })
    void shouldDiscussIfTheseAreTrulyInvalid(String input) {
        // If your domain allows the numerical value, these are technically valid
        // because BigDecimal normalizes them to "1200" and "0.500".
        // If you MUST reject the FORMAT, validate the String BEFORE the constructor.
    }

    @Nested
    @DisplayName("Semantics (Scale & Sign)")
    class SemanticTests {
        @Test
        void shouldRejectNegativeValues() {
            assertThatThrownBy(() -> new WeightVO(new BigDecimal("-1.00"), VALID_UNIT))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldRejectScaleHigherThanFive() {
            assertThatThrownBy(() -> new WeightVO(new BigDecimal("10.123456"), VALID_UNIT))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldAcceptValidScale() {
            WeightVO vo = new WeightVO(new BigDecimal("10.12345"), VALID_UNIT);
            assertThat(vo.amount().scale()).isLessThanOrEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Cross-Field Consistency")
    class EnumTests {
        @ParameterizedTest
        @EnumSource(WeightUnitEnums.class)
        void shouldWorkWithAllEnumUnits(WeightUnitEnums unit) {
            WeightVO vo = new WeightVO(new BigDecimal("1.0"), unit);
            assertThat(vo.weightUnit()).isEqualTo(unit);
        }
    }

    @Nested
    @DisplayName("Equality & HashCode")
    class EqualityTests {
        @Test
        void shouldBeEqualWhenValuesAreIdentical() {
            WeightVO vo1 = new WeightVO(new BigDecimal("10.5"), WeightUnitEnums.GRAM);
            WeightVO vo2 = new WeightVO(new BigDecimal("10.5"), WeightUnitEnums.GRAM);

            assertThat(vo1).isEqualTo(vo2);
            assertThat(vo1.hashCode()).isEqualTo(vo2.hashCode());
        }

        @Test
        void shouldNotBeEqualWhenScaleDiffers() {
            // BigDecimal equality (and thus Record equality) is sensitive to scale
            WeightVO vo1 = new WeightVO(new BigDecimal("1.0"), WeightUnitEnums.GRAM);
            WeightVO vo2 = new WeightVO(new BigDecimal("1.00"), WeightUnitEnums.GRAM);

            assertThat(vo1).isNotEqualTo(vo2);
        }

        @Test
        void shouldNotBeEqualWhenUnitDiffers() {
            WeightVO vo1 = new WeightVO(new BigDecimal("10.5"), WeightUnitEnums.GRAM);
            WeightVO vo2 = new WeightVO(new BigDecimal("10.5"), WeightUnitEnums.CARAT);

            assertThat(vo1).isNotEqualTo(vo2);
        }
    }
}
