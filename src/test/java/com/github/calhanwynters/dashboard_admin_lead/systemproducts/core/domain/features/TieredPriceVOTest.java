package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Senior SDET Suite: Hardened Financial Validation for TieredPriceVO.
 * Standards: Java 25, AssertJ, Given_When_Then naming.
 */

@DisplayName("TieredPriceVO & PriceTier Specification")
class TieredPriceVOTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency JPY = Currency.getInstance("JPY");
    private static final Currency BHD = Currency.getInstance("BHD");
    private static final String DEFAULT_UNIT = "API-CALL";

    @Nested
    @DisplayName("Existence & Nullability Invariants")
    class NullabilityTests {
        @Test
        void Given_NullUnit_When_ConstructingVO_Then_ThrowNPE() {
            assertThatThrownBy(() -> new TieredPriceVO(null, List.of(), USD))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void Given_NullTiers_When_ConstructingVO_Then_ThrowIAE() {
            assertThatThrownBy(() -> new TieredPriceVO(DEFAULT_UNIT, null, USD))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Structural Integrity & Boundaries")
    class StructuralTests {
        @Test
        void Given_ExternalList_When_ListModifiedAfterVOInit_Then_VOStateIsPreserved() {
            // Given
            List<TieredPriceVO.PriceTier> mutableTiers = new ArrayList<>();
            mutableTiers.add(TieredPriceVO.PriceTier.of(BigDecimal.ZERO, BigDecimal.TEN, USD));
            TieredPriceVO vo = new TieredPriceVO(DEFAULT_UNIT, mutableTiers, USD);

            // When
            mutableTiers.clear();

            // Then
            assertThat(vo.tiers()).hasSize(1);
        }

        @Test
        void Given_ExcessiveTiers_When_ConstructingVO_Then_ThrowIAE() {
            // Given
            List<TieredPriceVO.PriceTier> tooManyTiers = IntStream.range(0, 101)
                    .mapToObj(i -> TieredPriceVO.PriceTier.of(new BigDecimal(i), BigDecimal.ONE, USD))
                    .toList();

            // When / Then
            assertThatThrownBy(() -> new TieredPriceVO(DEFAULT_UNIT, tooManyTiers, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1 to 100");
        }
    }

    @Nested
    @DisplayName("Cross-Field Consistency & Currency Safety")
    class ConsistencyTests {
        @Test
        void Given_MismatchingCurrencyTier_When_ConstructingVO_Then_ThrowIAE() {
            // Given
            var usdTier = TieredPriceVO.PriceTier.of(BigDecimal.ZERO, BigDecimal.TEN, USD);
            var eurCurrency = Currency.getInstance("EUR");

            // When / Then
            assertThatThrownBy(() -> new TieredPriceVO(DEFAULT_UNIT, List.of(usdTier), eurCurrency))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("same currency");
        }


        @Test
        void Given_DuplicateThresholds_When_ConstructingVO_Then_ThrowIAE() {
            // Given (Distinct scales representing same value)
            var t1 = TieredPriceVO.PriceTier.of(new BigDecimal("100"), BigDecimal.TEN, USD);
            var t2 = TieredPriceVO.PriceTier.of(new BigDecimal("100.00"), BigDecimal.valueOf(20), USD);

            // When / Then
            assertThatThrownBy(() -> new TieredPriceVO(DEFAULT_UNIT, List.of(t1, t2), USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unique thresholds");
        }



        @Test
        void Given_UnsortedTiers_When_ConstructingVO_Then_InternalListIsSorted() {
            // Given
            var highTier = TieredPriceVO.PriceTier.of(BigDecimal.valueOf(100), BigDecimal.ONE, USD);
            var lowTier = TieredPriceVO.PriceTier.of(BigDecimal.ZERO, BigDecimal.TEN, USD);

            // When
            TieredPriceVO vo = new TieredPriceVO(DEFAULT_UNIT, List.of(highTier, lowTier), USD);

            // Then
            assertThat(vo.tiers().getFirst().threshold()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Financial Guardrails")
    class FinancialGuardTests {
        @Test
        void Given_AllFreeTiers_When_ConstructingVO_Then_RejectRevenueRisk() {
            // Given
            var freeTier = TieredPriceVO.PriceTier.free(BigDecimal.ZERO, USD, "PROMO-2026");

            // When / Then
            assertThatThrownBy(() -> new TieredPriceVO(DEFAULT_UNIT, List.of(freeTier), USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive price");
        }

        @Test
        void Given_StandardFactory_When_PriceIsZero_Then_ThrowIAE() {
            assertThatThrownBy(() -> TieredPriceVO.PriceTier.of(BigDecimal.ZERO, BigDecimal.ZERO, USD))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void Given_FreeFactory_When_AcknowledgmentEmpty_Then_ThrowIAE() {
            assertThatThrownBy(() -> TieredPriceVO.PriceTier.free(BigDecimal.ZERO, USD, ""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("Currency Precision Enforcement")
        @CsvSource({
                "USD, 10.99, true",   // Valid USD
                "USD, 10.991, false", // Fractional penny
                "JPY, 100, true",     // Valid JPY (0 decimals)
                "JPY, 100.50, false", // Fractional Yen
                "BHD, 1.250, true",   // Valid BHD (3 decimals)
                "BHD, 1.2505, false"  // Fractional fils
        })
        void Given_PricePrecision_When_CreatingTier_Then_ValidateAgainstISO4217(String code, String price, boolean valid) {
            Currency currency = Currency.getInstance(code);
            BigDecimal amount = new BigDecimal(price);

            if (valid) {
                assertThat(TieredPriceVO.PriceTier.of(BigDecimal.ZERO, amount, currency)).isNotNull();
            } else {
                assertThatThrownBy(() -> TieredPriceVO.PriceTier.of(BigDecimal.ZERO, amount, currency))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayName("Calculation Logic (calculate())")
    class CalculationLogicTests {
        private final TieredPriceVO pricing;

        CalculationLogicTests() {
            pricing = new TieredPriceVO(DEFAULT_UNIT, List.of(
                    TieredPriceVO.PriceTier.of(new BigDecimal("0"), new BigDecimal("50.00"), USD),
                    TieredPriceVO.PriceTier.of(new BigDecimal("100"), new BigDecimal("40.00"), USD)
            ), USD);
        }

        @Test
        void Given_QuantityBelowLowest_When_Calculating_Then_ReturnFirstTier() {
            // Act
            BigDecimal result = pricing.calculate(new BigDecimal("-5"));
            // Assert
            assertThat(result).isEqualByComparingTo("50.00");
        }

        @Test
        void Given_QuantityBetweenThresholds_When_Calculating_Then_ReturnLowerThresholdPrice() {
            // Given quantity 150 (Above 100 threshold)
            assertThat(pricing.calculate(new BigDecimal("150"))).isEqualByComparingTo("40.00");
            // Given quantity 50 (Between 0 and 100)
            assertThat(pricing.calculate(new BigDecimal("50"))).isEqualByComparingTo("50.00");
        }

        @Test
        void Given_NullQuantity_When_Calculating_Then_TreatAsZero() {
            assertThat(pricing.calculate(null)).isEqualByComparingTo("50.00");
        }
    }
}
