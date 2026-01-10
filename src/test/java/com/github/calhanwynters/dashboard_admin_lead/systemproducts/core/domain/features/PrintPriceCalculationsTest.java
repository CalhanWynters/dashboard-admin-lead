package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.PriceVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.TieredPriceVO.PriceTier;

@DisplayName("Audit: Multi-Strategy Price Calculation Comparisons")
class PrintPriceCalculationsTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency JPY = Currency.getInstance("JPY");
    private static final Currency BHD = Currency.getInstance("BHD");

    record PricingScenario(
            String description,
            BigDecimal input,
            String expectedOutput,
            Object vo
    ) {}

    @TestFactory
    @DisplayName("1. Scaling Strategy Audit")
    Stream<DynamicTest> verifyScalingCalculations() {
        ScalingPriceVO scalingVo = ScalingPriceVO.of("kg", new BigDecimal("10"), new BigDecimal("50.00"), new BigDecimal("5"), new BigDecimal("20.00"), USD);
        List<PricingScenario> scenarios = List.of(
                new PricingScenario("Under threshold", new BigDecimal("5"), "50.00", scalingVo),
                new PricingScenario("Micro-overage (Step Up)", new BigDecimal("10.0001"), "70.00", scalingVo),
                new PricingScenario("Partial second step", new BigDecimal("15.1"), "90.00", scalingVo)
        );
        return scenarios.stream().map(s -> dynamicTest("Scaling: " + s.description(), () -> runAudit("SCALING", s, ((ScalingPriceVO) s.vo()).calculate(s.input()))));
    }

    @TestFactory
    @DisplayName("2. Tiered Strategy Audit")
    Stream<DynamicTest> verifyTieredCalculations() {
        TieredPriceVO tieredVo = new TieredPriceVO("license", List.of(
                PriceTier.of(BigDecimal.ZERO, new BigDecimal("100.00"), USD),
                PriceTier.of(new BigDecimal("10"), new BigDecimal("80.00"), USD)
        ), USD);
        List<PricingScenario> scenarios = List.of(
                new PricingScenario("Tier 1 - Upper Bound", new BigDecimal("9.99"), "100.00", tieredVo),
                new PricingScenario("Tier 2 - Lower Bound", new BigDecimal("10"), "80.00", tieredVo)
        );
        return scenarios.stream().map(s -> dynamicTest("Tiered: " + s.description(), () -> runAudit("TIERED ", s, ((TieredPriceVO) s.vo()).calculate(s.input()))));
    }

    @TestFactory
    @DisplayName("3. PriceVO Normalization Audit")
    Stream<DynamicTest> verifyPriceVoNormalization() {
        List<PricingScenario> scenarios = List.of(
                new PricingScenario("USD Standard", new BigDecimal("10.5"), "10.50", new PriceVO(new BigDecimal("10.5"), USD)),
                new PricingScenario("JPY Zero-Decimal", new BigDecimal("500"), "500", new PriceVO(new BigDecimal("500"), JPY)),
                new PricingScenario("BHD Three-Decimal", new BigDecimal("1.25"), "1.250", new PriceVO(new BigDecimal("1.25"), BHD)),
                // FIX: Input 10.5 triggers 2nd constructor; precision defaults to 2 (USD).
                // Price is normalized to 10.50. Input 10.555 is REJECTED by domain rules.
                new PricingScenario("Explicit Precision Normalization", new BigDecimal("10.55"), "10.55", new PriceVO(new BigDecimal("10.55"), USD))
        );

        return scenarios.stream().map(scenario -> dynamicTest(
                "PriceVO: " + scenario.description(),
                () -> {
                    PriceVO vo = (PriceVO) scenario.vo();
                    runAudit("PRICEVO", scenario, vo.price());
                }
        ));
    }

    private void runAudit(String strategy, PricingScenario scenario, BigDecimal actual) {
        String inputLabel = strategy.equals("PRICEVO") ? "RawPrice" : "Qty";
        System.out.printf("[AUDIT 2026] [%s] %-25s | %s: %-8s | Expected: %-8s | Actual: %-8s%n",
                strategy, scenario.description(), inputLabel, scenario.input(), scenario.expectedOutput(), actual.toPlainString());

        assertThat(actual)
                .as("%s mismatch for: %s", strategy, scenario.description())
                .isEqualByComparingTo(scenario.expectedOutput());
    }
}
