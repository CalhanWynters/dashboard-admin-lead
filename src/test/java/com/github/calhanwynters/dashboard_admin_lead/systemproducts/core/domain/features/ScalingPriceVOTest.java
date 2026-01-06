package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("ScalingPriceVO High-Integrity Financial Test Suite")
class ScalingPriceVOTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency JPY = Currency.getInstance("JPY");
    private static final Currency BHD = Currency.getInstance("BHD");

    @Nested
    @DisplayName("Constructor Existence & Lexical Integrity")
    class ValidationTests {

        @Test
        @DisplayName("Should throw NullPointerException when mandatory fields are missing")
        void givenNullInputs_WhenConstructing_ThenThrowNPE() {
            assertThatThrownBy(() -> new ScalingPriceVO(null, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 2, USD))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Unit is required");
        }

        @ParameterizedTest
        @ValueSource(strings = {"kg", "GB/sec", "10-pack", "UNIT123"})
        @DisplayName("Should accept valid lexical units based on 2026 whitelist")
        void givenValidUnits_WhenConstructing_ThenSuccess(String validUnit) {
            var vo = ScalingPriceVO.of(validUnit, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, USD);
            assertThat(vo.unit()).isEqualTo(validUnit);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "kg!", "unit;", "this-string-is-way-too-long-for-db", ""})
        @DisplayName("Should reject units with invalid characters or excessive length")
        void givenInvalidUnits_WhenConstructing_ThenThrowException(String invalidUnit) {
            assertThatThrownBy(() -> ScalingPriceVO.of(invalidUnit, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unit contains invalid characters or length");
        }
    }

    @Nested
    @DisplayName("Financial Invariants & Boundary Enforcement")
    class FinancialInvariantTests {

        @Test
        @DisplayName("Should prevent 'Accidental Free Product' by rejecting dual-zero prices")
        void givenBothPricesZero_WhenConstructing_ThenReject() {
            assertThatThrownBy(() -> ScalingPriceVO.of("kg", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Scaling price must have a positive base price or step price");
        }

        @Test
        @DisplayName("Should reject negative thresholds or non-positive increment steps")
        void givenInvalidSteps_WhenConstructing_ThenReject() {
            assertThatThrownBy(() -> ScalingPriceVO.of("kg", new BigDecimal("-1"), BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, USD))
                    .hasMessageContaining("Threshold cannot be negative");

            assertThatThrownBy(() -> ScalingPriceVO.of("kg", BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, USD))
                    .hasMessageContaining("Increment step must be strictly positive");
        }

        @Test
        @DisplayName("Should prevent Arithmetic DoS by rejecting extreme scales")
        void givenExtremeScale_WhenConstructing_ThenReject() {
            BigDecimal extreme = new BigDecimal("1.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001");
            assertThatThrownBy(() -> ScalingPriceVO.of("kg", extreme, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Numeric scale exceeds safety limits");
        }

        @Test
        @DisplayName("Should reject prices exceeding system safety limit (1 Billion)")
        void givenHugePrice_WhenConstructing_ThenReject() {
            BigDecimal huge = new BigDecimal("1000000000.01");
            assertThatThrownBy(() -> ScalingPriceVO.of("kg", BigDecimal.ZERO, huge, BigDecimal.ONE, BigDecimal.ONE, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Price values exceed system boundary limits");
        }
    }

    @Nested
    @DisplayName("Currency Precision & ISO-4217 Compliance")
    class CurrencyComplianceTests {

        @ParameterizedTest
        @CsvSource({
                "USD, 10.00, 10.005", // Fails: USD has 2 digits, 10.005 is a fractional penny
                "JPY, 10.5,  10.00",  // Fails: JPY has 0 digits, 10.5 is invalid
                "BHD, 10.00, 10.0001" // Fails: BHD has 3 digits, .0001 is invalid
        })
        @DisplayName("Should reject 'fractional penny' inputs that violate ISO-4217 scales")
        void givenInvalidPrecisionForCurrency_WhenConstructing_ThenReject(String code, String base, String step) {
            Currency currency = Currency.getInstance(code);
            assertThatThrownBy(() -> ScalingPriceVO.of("kg", BigDecimal.TEN, new BigDecimal(base), BigDecimal.ONE, new BigDecimal(step), currency))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("scale exceeds");
        }

        @Test
        @DisplayName("Should enforce output scale matches the record's precision")
        void givenValidInputs_WhenCalculating_ThenResultHasExactPrecision() {
            // Using BHD (3 decimals)
            ScalingPriceVO vo = ScalingPriceVO.of("kg", BigDecimal.ZERO, new BigDecimal("10.000"), BigDecimal.ONE, new BigDecimal("5.000"), BHD);
            BigDecimal result = vo.calculate(new BigDecimal("1"));

            assertThat(result.scale()).isEqualTo(3);
            assertThat(result.toPlainString()).isEqualTo("15.000");
        }
    }

    @Nested
    @DisplayName("Calculation Logic (Base, Steps, & Ceiling Rounding)")
    class CalculationTests {

        @Test
        @DisplayName("Should return basePrice when quantity is null (defaulting to zero)")
        void givenNullQuantity_WhenCalculating_ThenReturnBasePrice() {
            var vo = ScalingPriceVO.of("kg", BigDecimal.TEN, BigDecimal.valueOf(50), BigDecimal.ONE, BigDecimal.TEN, USD);
            assertThat(vo.calculate(null)).isEqualByComparingTo("50.00");
        }

        @Test
        @DisplayName("Should return basePrice when quantity is within threshold")
        void givenQuantityUnderThreshold_WhenCalculating_ThenReturnBasePrice() {
            var vo = ScalingPriceVO.of("kg", BigDecimal.TEN, BigDecimal.valueOf(50), BigDecimal.ONE, BigDecimal.TEN, USD);
            assertThat(vo.calculate(new BigDecimal("7.5"))).isEqualByComparingTo("50.00");
            assertThat(vo.calculate(BigDecimal.TEN)).isEqualByComparingTo("50.00");
        }

        @Test
        @DisplayName("Should apply step price for exact increments")
        void givenExactStepQuantity_WhenCalculating_ThenApplyStepPrice() {
            // Base 10, Step 5. Qty 15 = Base + 1 Step
            var vo = ScalingPriceVO.of("kg", BigDecimal.TEN, BigDecimal.valueOf(50), BigDecimal.valueOf(5), BigDecimal.valueOf(20), USD);
            assertThat(vo.calculate(new BigDecimal("15"))).isEqualByComparingTo("70.00");
        }

        @Test
        @DisplayName("Should apply Ceiling Rule for partial steps (Any excess counts as a full step)")
        void givenPartialStepQuantity_WhenCalculating_ThenRoundUpToNextStep() {
            // Base 10, Step 5. Qty 10.00001 = Base + 1 Step (due to ceiling)
            var vo = ScalingPriceVO.of("kg", BigDecimal.TEN, BigDecimal.valueOf(50), BigDecimal.valueOf(5), BigDecimal.valueOf(20), USD);
            assertThat(vo.calculate(new BigDecimal("10.00001"))).isEqualByComparingTo("70.00");
        }
    }
}
