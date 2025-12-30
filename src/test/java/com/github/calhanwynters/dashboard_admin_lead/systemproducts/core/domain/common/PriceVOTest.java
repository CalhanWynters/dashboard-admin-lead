package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class PriceVOTest {

    @Test
    @DisplayName("Should create valid USD instance with default constructor")
    void acceptsValidUsdPrice() {
        BigDecimal input = new BigDecimal("99.99");
        PriceVO vo = new PriceVO(input);

        assertEquals(new BigDecimal("99.99"), vo.price());
        assertEquals(2, vo.precision());
        assertEquals("USD", vo.currency().getCurrencyCode());
    }

    @Test
    @DisplayName("Should normalize scale (e.g., 10 -> 10.00) for canonical equality")
    void normalizesPriceScale() {
        PriceVO vo = new PriceVO(new BigDecimal("10"));

        // Assert that the scale is set to 2 (USD default)
        assertEquals("10.00", vo.price().toPlainString());
        assertEquals(2, vo.price().scale());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null price or currency")
    void rejectsNulls() {
        assertThrows(NullPointerException.class, () -> new PriceVO(null));
        assertThrows(NullPointerException.class, () -> new PriceVO(BigDecimal.ONE, 2, null));
    }

    @Test
    @DisplayName("Should reject negative prices")
    void rejectsNegativePrice() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new PriceVO(new BigDecimal("-0.01")));
        assertEquals("Price cannot be negative.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject prices exceeding the $100M logical boundary")
    void rejectsExtremePrices() {
        BigDecimal tooHigh = new BigDecimal("100000000.01");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new PriceVO(tooHigh));
        assertEquals("Price exceeds maximum logical system boundary.", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "1, JPY, 0", // JPY usually has 0 fraction digits, so precision 1 is fine
            "2, USD, 2", // USD has 2, so precision 2 is fine
            "3, BHD, 3"  // Bahraini Dinar has 3
    })
    @DisplayName("Should allow precision equal to or greater than currency defaults")
    void allowsValidPrecision(int precision, String currencyCode, int minDigits) {
        Currency currency = Currency.getInstance(currencyCode);
        assertDoesNotThrow(() -> new PriceVO(BigDecimal.TEN, precision, currency));
    }

    @Test
    @DisplayName("Should reject precision lower than currency default (Cross-Field validation)")
    void rejectsInsufficientPrecision() {
        Currency usd = Currency.getInstance("USD"); // Default is 2
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new PriceVO(BigDecimal.TEN, 1, usd));

        assertTrue(ex.getMessage().contains("insufficient for currency USD"));
    }

    @Test
    @DisplayName("Should trigger Arithmetic DoS protection if precision exceeds 10")
    void rejectsExcessivePrecision() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new PriceVO(BigDecimal.ONE, 11, Currency.getInstance("USD")));
        assertTrue(ex.getMessage().contains("Precision must be between 0 and 10"));
    }

    @Test
    @DisplayName("Should correctly round extra decimals using HALF_UP")
    void roundsPriceCorrecty() {
        // USD default precision is 2. 10.555 should become 10.56
        PriceVO vo = new PriceVO(new BigDecimal("10.555"));
        assertEquals("10.56", vo.price().toPlainString());
    }

    @Test
    @DisplayName("Should provide standardized toString format")
    void verifiesToString() {
        PriceVO vo = new PriceVO(new BigDecimal("50.00"), 2, Currency.getInstance("EUR"));
        // Symbol for EUR is €
        assertEquals("€ 50.00", vo.toString());
    }
}
