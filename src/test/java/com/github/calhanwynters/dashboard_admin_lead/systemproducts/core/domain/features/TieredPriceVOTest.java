package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TieredPriceVOTest {

    private static final Currency USD = Currency.getInstance("USD");

    @Test
    @DisplayName("Should sort tiers automatically and maintain immutability")
    void semanticSortingAndImmutability() {
        // Tiers provided out of order
        List<TieredPriceVO.PriceTier> inputTiers = new ArrayList<>();
        inputTiers.add(new TieredPriceVO.PriceTier(new BigDecimal("100.0"), new BigDecimal("50.00")));
        inputTiers.add(new TieredPriceVO.PriceTier(new BigDecimal("10.0"), new BigDecimal("90.00")));

        TieredPriceVO vo = new TieredPriceVO("units", inputTiers, USD);

        // Verify sorting: Tier with threshold 10.0 must come first
        assertEquals(new BigDecimal("10.0"), vo.tiers().getFirst().threshold());

        // Verify Defensive Copying: Modifying original list shouldn't affect VO
        inputTiers.clear();
        assertFalse(vo.tiers().isEmpty(), "VO tiers should remain unchanged after external list modification.");

        // Verify Immutability: Record tiers list should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> vo.tiers().add(
                new TieredPriceVO.PriceTier(BigDecimal.ONE, BigDecimal.ONE)
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "5,    90.00", // Below first tier (10) -> uses first tier price
            "10,   90.00", // Exactly first tier -> uses first tier price
            "11,   50.00", // Just over first tier -> uses second tier (100) price
            "100,  50.00", // Exactly second tier -> uses second tier price
            "150,  50.00"  // Over last tier -> uses last tier price (orElseGet logic)
    })
    @DisplayName("Calculation: Verify tiered lookup logic")
    void calculationLogic(String qty, String expectedPrice) {
        List<TieredPriceVO.PriceTier> tiers = List.of(
                new TieredPriceVO.PriceTier(new BigDecimal("10.0"), new BigDecimal("90.00")),
                new TieredPriceVO.PriceTier(new BigDecimal("100.0"), new BigDecimal("50.00"))
        );
        TieredPriceVO vo = new TieredPriceVO("seats", tiers, USD);

        BigDecimal result = vo.calculate(new BigDecimal(qty));
        assertEquals(new BigDecimal(expectedPrice), result);
    }

    @Test
    @DisplayName("Should reject duplicate thresholds to prevent ambiguity")
    void rejectDuplicateThresholds() {
        List<TieredPriceVO.PriceTier> duplicates = List.of(
                new TieredPriceVO.PriceTier(new BigDecimal("10.0"), new BigDecimal("50.00")),
                new TieredPriceVO.PriceTier(new BigDecimal("10.0"), new BigDecimal("40.00"))
        );
        assertThrows(IllegalArgumentException.class, () -> new TieredPriceVO("unit", duplicates, USD));
    }

    @Test
    @DisplayName("Should reject arithmetic DoS (massive scale)")
    void arithmeticDosMitigation() {
        BigDecimal massiveScale = new BigDecimal("1.0").setScale(11); // MAX_SCALE is 10
        assertThrows(IllegalArgumentException.class, () ->
                new TieredPriceVO.PriceTier(massiveScale, BigDecimal.ONE)
        );
    }

    @Test
    @DisplayName("Should reject unit names violating lexical whitelist")
    void lexicalWhitelist() {
        List<TieredPriceVO.PriceTier> validTiers = List.of(new TieredPriceVO.PriceTier(BigDecimal.TEN, BigDecimal.ONE));
        // UNIT_PATTERN = ^[a-zA-Z0-9\-/]{1,20}$
        assertThrows(IllegalArgumentException.class, () -> new TieredPriceVO("Invalid!", validTiers, USD));
    }

    @Test
    @DisplayName("Example: Print tiered price scenarios for Bulk Wholesale model")
    void printTieredExample() {
        // Setup a Wholesale model:
        // 0-10 units: $100.00 each
        // 11-50 units: $85.00 each
        // 51+ units: $60.00 each
        List<TieredPriceVO.PriceTier> wholesaleTiers = List.of(
                new TieredPriceVO.PriceTier(new BigDecimal("10.0"), new BigDecimal("100.00")),
                new TieredPriceVO.PriceTier(new BigDecimal("50.0"), new BigDecimal("85.00")),
                new TieredPriceVO.PriceTier(new BigDecimal("999999.0"), new BigDecimal("60.00"))
        );

        TieredPriceVO wholesalePricing = new TieredPriceVO(
                "Bulk-Pallets",
                wholesaleTiers,
                Currency.getInstance("USD")
        );

        System.out.println("=== TIERED PRICING MODEL REPORT (2025) ===");
        System.out.println("Unit: " + wholesalePricing.unit());
        System.out.println("Currency: " + wholesalePricing.currency().getCurrencyCode());
        System.out.println("-------------------------------------------");
        System.out.printf("%-15s | %-15s | %-15s%n", "Qty Ordered", "Price/Unit", "Tier Applied");
        System.out.println("-------------------------------------------");

        List<BigDecimal> scenarios = List.of(
                new BigDecimal("5.0"),    // First tier
                new BigDecimal("10.0"),   // Edge of first tier
                new BigDecimal("11.0"),   // Into second tier
                new BigDecimal("50.0"),   // Edge of second tier
                new BigDecimal("51.0"),   // Into bulk tier
                new BigDecimal("100.0")   // Deep into bulk tier
        );

        for (BigDecimal qty : scenarios) {
            BigDecimal pricePerUnit = wholesalePricing.calculate(qty);

            // Logic to determine which threshold label to show
            String tierLabel = wholesalePricing.tiers().stream()
                    .filter(t -> t.threshold().compareTo(qty) >= 0)
                    .findFirst()
                    .map(t -> "Up to " + t.threshold())
                    .orElse("Max Tier");

            System.out.printf("%-15s | %-15s | %-15s%n",
                    qty,
                    wholesalePricing.currency().getSymbol() + pricePerUnit,
                    tierLabel);
        }
    }
}