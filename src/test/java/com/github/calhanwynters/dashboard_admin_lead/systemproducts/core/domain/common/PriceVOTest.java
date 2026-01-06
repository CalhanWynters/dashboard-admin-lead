package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;




@DisplayName("PriceVO Invariant and Validation Tests")
class PriceVOTest {

    // Helper for Jackson serialization tests
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Nested
    @DisplayName("Invariant Validation: Negative Prices & Boundaries")
    class InvariantValidationTests {

        private final Currency USD = Currency.getInstance("USD");

        @Test
        @DisplayName("GIVEN a negative price WHEN creating PriceVO THEN IllegalArgumentException is thrown")
        void givenNegativePrice_whenCreatingPriceVO_thenThrowIllegalArgumentException() {
            // Given
            BigDecimal negativePrice = new BigDecimal("-0.01");

            // When / Then
            assertThatThrownBy(() -> new PriceVO(negativePrice, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Price cannot be negative.");
        }


        @Test
        @DisplayName("GIVEN a price exceeding $100M USD WHEN creating PriceVO THEN IllegalArgumentException is thrown")
        void givenPriceExceedingBoundary_whenCreatingPriceVO_thenThrowIllegalArgumentException() {
            // Given
            BigDecimal highPrice = new BigDecimal("100000000.01"); // $100M and one penny

            // When / Then
            assertThatThrownBy(() -> new PriceVO(highPrice, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Price exceeds maximum logical system boundary.");
        }




        @Test
        @DisplayName("GIVEN an explicit precision exceeding 10 WHEN creating PriceVO THEN IllegalArgumentException is thrown")
        void givenPrecisionExceedingMax_whenCreatingPriceVO_thenThrowIllegalArgumentException() {
            // Given
            int excessivePrecision = 11;

            // When / Then
            assertThatThrownBy(() -> new PriceVO(BigDecimal.TEN, excessivePrecision, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Precision must be between 0 and 10");
        }


    }


    @Nested
    @DisplayName("Cross-Field Consistency: Fractional Pennies & Currency Rules")
    class ConsistencyTests {

        private final Currency USD = Currency.getInstance("USD"); // 2 decimal places

        @Test
        @DisplayName("GIVEN a price with fractional pennies (e.g., 10.123 USD) WHEN creating PriceVO THEN IllegalArgumentException is thrown")
        void givenFractionalPennies_whenCreatingPriceVO_thenThrowIllegalArgumentException() {
            // Given
            BigDecimal fractionalPrice = new BigDecimal("10.123");

            // When / Then
            assertThatThrownBy(() -> new PriceVO(fractionalPrice, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Input price scale (3) exceeds currency USD allowed precision (2).");
        }

        @Test
        @DisplayName("GIVEN valid price (e.g., 10.12 USD) WHEN creating PriceVO THEN object is created successfully")
        void givenValidPrice_whenCreatingPriceVO_thenObjectIsCreated() {
            // Given
            BigDecimal validPrice = new BigDecimal("10.12");

            // When
            PriceVO vo = new PriceVO(validPrice, USD);

            // Then
            assertThat(vo.price()).isEqualByComparingTo("10.12");
            assertThat(vo.precision()).isEqualTo(2);
            assertThat(vo.currency()).isEqualTo(USD);
        }

    }

    @Nested
    @DisplayName("Canonical Normalization & Equality")
    class NormalizationTests {

        @Test
        @DisplayName("GIVEN different representations of the same value (10, 10.00) WHEN creating PriceVO THEN resulting objects are equal")
        void givenDifferentRepresentations_whenCreatingPriceVO_thenObjectsAreEqualAndHaveSameHashCode() {
            // Given
            PriceVO vo1 = new PriceVO(new BigDecimal("10"), Currency.getInstance("USD"));
            PriceVO vo2 = new PriceVO(new BigDecimal("10.00"), Currency.getInstance("USD"));

            // When / Then
            assertThat(vo1).isEqualTo(vo2);
            assertThat(vo1.hashCode()).isEqualTo(vo2.hashCode());
            // Verify internal normalized price representation
            assertThat(vo1.price().stripTrailingZeros()).isEqualByComparingTo(new BigDecimal("10"));
            assertThat(vo2.price().stripTrailingZeros()).isEqualByComparingTo(new BigDecimal("10"));
        }
    }


    @Nested
    @DisplayName("Currency Flexibility (Parameterized)")
    class CurrencyFlexibilityTests {


        @ParameterizedTest(name = "Test {0} with input {1}, expected scale {2}")
        @CsvSource({
                "JPY, 100, 0",    // Japanese Yen has 0 decimal places
                "BHD, 1.234, 3",  // Bahraini Dinar has 3 decimal places
                "TND, 5.500, 3",  // Tunisian Dinar has 3 decimal places
                "USD, 10.00, 2"   // US Dollar
        })
        @DisplayName("GIVEN various currencies with different scales WHEN creating PriceVO THEN the correct precision is enforced")
        void givenVariousCurrencies_whenCreatingPriceVO_thenCorrectPrecisionIsUsed(String currencyCode, BigDecimal priceInput, int expectedPrecision) {
            // Given
            Currency currency = Currency.getInstance(currencyCode);

            // When
            PriceVO vo = new PriceVO(priceInput, currency);

            // Then
            assertThat(vo.precision()).isEqualTo(expectedPrecision);
            assertThat(vo.price().scale()).isEqualTo(expectedPrecision);
            assertThat(vo.price()).isEqualByComparingTo(priceInput.setScale(expectedPrecision, java.math.RoundingMode.HALF_UP));        }

        @ParameterizedTest(name = "Test invalid input {1} for {0}")
        @CsvSource({
                "JPY, 100.5",   // JPY doesn't allow .5
                "BHD, 1.2345",  // BHD doesn't allow 4th decimal place
                "USD, 10.123"   // USD doesn't allow 3rd decimal place
        })
        @DisplayName("GIVEN invalid input scale for a currency WHEN creating PriceVO THEN IllegalArgumentException is thrown")
        void givenInvalidScaleForCurrency_whenCreatingPriceVO_thenThrowIllegalArgumentException(String currencyCode, BigDecimal priceInput) {
            // Given
            Currency currency = Currency.getInstance(currencyCode);

            // When / Then
            assertThatThrownBy(() -> new PriceVO(priceInput, currency))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Input price scale");
        }
    }

    @Nested
    @DisplayName("Serialization & Immutability Integrity")
    class SerializationTests {

        @Test
        @DisplayName("GIVEN a JSON with negative price WHEN deserializing THEN compact constructor validation triggers")
        void givenInvalidJson_whenDeserializing_thenValidationPreventsInvariantBypass() {
            // Given: JSON that would result in an invalid domain state
            String invalidJson = """
            {
              "price": -50.00,
              "precision": 2,
              "currency": "USD"
            }
            """;

            // When / Then: Jackson wraps construction exceptions in a JsonMappingException
            assertThatThrownBy(() -> objectMapper.readValue(invalidJson, PriceVO.class))
                    .hasStackTraceContaining("Price cannot be negative.");
        }

        @Test
        @DisplayName("GIVEN valid JSON WHEN deserializing THEN valid PriceVO is created")
        void givenValidJson_whenDeserializing_thenObjectIsCreatedSuccessfully() throws Exception {
            String validJson = "{\"price\": 100.50, \"precision\": 2, \"currency\": \"USD\"}";

            PriceVO result = objectMapper.readValue(validJson, PriceVO.class);

            assertThat(result.price()).isEqualByComparingTo("100.50");
            assertThat(result.currency().getCurrencyCode()).isEqualTo("USD");
        }
    }

    @Nested
    @DisplayName("Null Safety & Robustness")
    class NullSafetyTests {

        @Test
        @DisplayName("GIVEN a null price WHEN creating PriceVO THEN NullPointerException is thrown")
        void givenNullPrice_whenCreatingPriceVO_thenThrowNullPointerException() {
            // Given
            BigDecimal nullPrice = null;
            Currency usd = Currency.getInstance("USD");

            // When / Then
            assertThatThrownBy(() -> new PriceVO(nullPrice, usd)) // Value 'nullPrice' is always 'null'
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Price cannot be null");
        }

        @Test
        @DisplayName("GIVEN a null currency WHEN creating PriceVO THEN NullPointerException is thrown")
        void givenNullCurrency_whenCreatingPriceVO_thenThrowNullPointerException() {
            // Given
            Currency nullCurrency = null;
            BigDecimal price = BigDecimal.TEN;

            // When / Then
            assertThatThrownBy(() -> new PriceVO(price, nullCurrency)) // Value 'nullCurrency' is always 'null'
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Currency must not be null");
        }
    }
}

