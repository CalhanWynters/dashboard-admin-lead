package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("Audit: Scaling Price Calculation Comparisons")
class PrintPriceCalculationsTest {

    private static final Currency USD = Currency.getInstance("USD");

    // Define a record to hold test case data for clear printing
    record PricingScenario(
            String description,
            BigDecimal quantity,
            String expectedPrice,
            ScalingPriceVO vo
    ) {}

    @TestFactory
    @DisplayName("Verify and print price calculations for auditing")
    Stream<DynamicTest> verifyCalculations() {
        // Shared Configuration: $50 base up to 10kg, then $20 per 5kg step
        ScalingPriceVO standardVo = ScalingPriceVO.of(
                "kg",
                new BigDecimal("10"),   // Threshold
                new BigDecimal("50.00"),// Base Price
                new BigDecimal("5"),    // Increment Step
                new BigDecimal("20.00"),// Step Price
                USD
        );

        List<PricingScenario> scenarios = List.of(
                new PricingScenario("Under threshold", new BigDecimal("5"), "50.00", standardVo),
                new PricingScenario("Exactly at threshold", new BigDecimal("10"), "50.00", standardVo),
                new PricingScenario("Micro-overage (Ceiling Rule)", new BigDecimal("10.0001"), "70.00", standardVo),
                new PricingScenario("One full step over", new BigDecimal("15"), "70.00", standardVo),
                new PricingScenario("Partial second step", new BigDecimal("15.1"), "90.00", standardVo)
        );

        return scenarios.stream().map(scenario -> dynamicTest(
                "Scenario: " + scenario.description() + " [Qty: " + scenario.quantity() + "]",
                () -> {
                    BigDecimal actual = scenario.vo().calculate(scenario.quantity());

                    // Print calculation details for manual audit review
                    System.out.printf("[AUDIT 2026] Scenario: %-30s | Qty: %-8s | Expected: %-8s | Actual: %-8s%n",
                            scenario.description(), scenario.quantity(), scenario.expectedPrice(), actual.toPlainString());

                    // Use isEqualByComparingTo to verify the numeric value accurately
                    assertThat(actual)
                            .as("Price calculation for %s with qty %s", scenario.description(), scenario.quantity())
                            .isEqualByComparingTo(scenario.expectedPrice());
                }
        ));
    }
}