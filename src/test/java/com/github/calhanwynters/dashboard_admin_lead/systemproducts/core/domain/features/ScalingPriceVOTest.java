package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScalingPriceVOTest {

    private static final Currency USD = Currency.getInstance("USD");

    @Test
    @DisplayName("Should create a valid ScalingPriceVO and normalize price scale")
    void validCreation() {
        ScalingPriceVO vo = new ScalingPriceVO(
                "USD/kg",
                new BigDecimal("10.0"),
                new BigDecimal("100"), // base price
                new BigDecimal("1.0"),
                new BigDecimal("5.5"), // price per step
                2,
                USD
        );

        assertEquals("USD/kg", vo.unit());
        assertEquals(new BigDecimal("100.00"), vo.basePrice(), "Base price should be normalized to precision 2");
        assertEquals(new BigDecimal("5.50"), vo.pricePerStep(), "Step price should be normalized to precision 2");
    }

    @Test
    @DisplayName("Should reject arithmetic DoS (massive scale)")
    void arithmeticDosMitigation() {
        BigDecimal massiveScale = new BigDecimal("1.0").setScale(101);
        assertThrows(IllegalArgumentException.class, () ->
                ScalingPriceVO.of("unit", massiveScale, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, USD)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "10.0, 100.00", // Exactly at threshold -> base price
            "5.0,  100.00", // Below threshold -> base price
            "11.0, 105.50", // 1 step over (11-10=1) -> base + 1*5.50
            "10.1, 105.50", // Partial step (CEILING) -> base + 1*5.50
            "12.0, 111.00", // 2 steps over -> base + 2*5.50
            "0.0,  100.00"  // Zero quantity -> base price
    })
    @DisplayName("Calculate: Stepped pricing logic checks")
    void calculationLogic(String qty, String expected) {
        ScalingPriceVO vo = ScalingPriceVO.of(
                "unit",
                new BigDecimal("10.0"),
                new BigDecimal("100.00"),
                new BigDecimal("1.0"),
                new BigDecimal("5.50"),
                USD
        );

        BigDecimal result = vo.calculate(new BigDecimal(qty));
        assertEquals(new BigDecimal(expected), result);
    }

    @Test
    @DisplayName("Should reject invalid unit lexical patterns")
    void invalidUnitPatterns() {
        // Restricted to ^[a-zA-Z0-9\-/]{1,20}$
        assertThrows(IllegalArgumentException.class, () ->
                ScalingPriceVO.of("Invalid Unit!", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, USD)
        );
    }

    @Test
    @DisplayName("Should reject negative or zero increment steps")
    void invalidStep() {
        assertThrows(IllegalArgumentException.class, () ->
                ScalingPriceVO.of("unit", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, USD)
        );
    }

    @Test
    @DisplayName("Should reject prices exceeding MAX_PRICE_LIMIT")
    void priceBoundaryCheck() {
        BigDecimal tooRich = new BigDecimal("1000000001.00");
        assertThrows(IllegalArgumentException.class, () ->
                ScalingPriceVO.of("unit", BigDecimal.ONE, tooRich, BigDecimal.ONE, BigDecimal.ONE, USD)
        );
    }

    @Test
    @DisplayName("Example: Print scaling price scenarios for a typical SaaS seat model")
    void printScalingExample() {
        // Setup a SaaS-style scaling price:
        // First 5 units are $50.00 total. Every 2 additional units cost $15.00.
        ScalingPriceVO saasPricing = new ScalingPriceVO(
                "SaaS-Seats",
                new BigDecimal("5.0"),     // Base Threshold
                new BigDecimal("50.00"),   // Base Price
                new BigDecimal("2.0"),     // Increment Step (blocks of 2)
                new BigDecimal("15.00"),   // Price per Step
                2,
                Currency.getInstance("USD")
        );

        System.out.println("=== SCALING PRICE MODEL EXAMPLE ===");
        System.out.println(saasPricing); // Uses Record's default toString()
        System.out.println("------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s%n", "Qty Requested", "Price Applied", "Step Logic");
        System.out.println("------------------------------------");

        List<BigDecimal> scenarios = List.of(
                new BigDecimal("3.0"),   // Well under threshold
                new BigDecimal("5.0"),   // Exactly at threshold
                new BigDecimal("5.1"),   // Just over (triggers 1 full step)
                new BigDecimal("7.0"),   // Exactly 1 step over
                new BigDecimal("10.0")   // 2.5 steps over (triggers 3 steps)
        );

        for (BigDecimal qty : scenarios) {
            BigDecimal total = saasPricing.calculate(qty);

            // Logic explanation for the printout
            String stepInfo = qty.compareTo(new BigDecimal("5.0")) <= 0
                    ? "Base Price"
                    : "Base + Steps";

            System.out.printf("%-15s | %-15s | %-15s%n",
                    qty,
                    saasPricing.currency().getSymbol() + total,
                    stepInfo);
        }
    }
}