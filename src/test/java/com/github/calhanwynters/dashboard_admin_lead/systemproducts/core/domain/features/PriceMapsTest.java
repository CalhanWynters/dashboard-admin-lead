package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.PriceVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.TieredPriceVO.PriceTier;

@DisplayName("Audit: Global Multi-Currency Pricing Strategy Maps (2026)")
class PriceMapsTest {

    private final Map<Currency, PriceVO> fixedPrices = new HashMap<>();
    private final Map<Currency, ScalingPriceVO> scalingPriceSchemes = new HashMap<>();
    private final Map<Currency, TieredPriceVO> tieredPriceSchemes = new HashMap<>();

    // --- Varied Precision Currencies (All Resolved) ---
    private static final Currency USD = Currency.getInstance("USD"); // 2-decimal
    private static final Currency JPY = Currency.getInstance("JPY"); // 0-decimal
    private static final Currency BHD = Currency.getInstance("BHD"); // 3-decimal
    private static final Currency EUR = Currency.getInstance("EUR"); // 2-decimal
    private static final Currency KRW = Currency.getInstance("KRW"); // 0-decimal
    private static final Currency KWD = Currency.getInstance("KWD"); // 3-decimal
    private static final Currency OMR = Currency.getInstance("OMR"); // 3-decimal
    private static final Currency CLP = Currency.getInstance("CLP"); // 0-decimal
    private static final Currency HUF = Currency.getInstance("HUF"); // 0-decimal
    private static final Currency GBP = Currency.getInstance("GBP"); // 2-decimal <-- NOW RESOLVED

    @BeforeEach
    void setUp() {
        // 1. FIXED PRICE MAP
        fixedPrices.put(USD, new PriceVO(new BigDecimal("100.00"), USD));
        fixedPrices.put(JPY, new PriceVO(new BigDecimal("15000"), JPY));
        fixedPrices.put(BHD, new PriceVO(new BigDecimal("37.500"), BHD));
        fixedPrices.put(HUF, new PriceVO(new BigDecimal("35000"), HUF));

        // 2. SCALING SCHEMES (Using component 'baseThreshold')
        scalingPriceSchemes.put(EUR, ScalingPriceVO.of("unit", new BigDecimal("100"), new BigDecimal("95.00"), new BigDecimal("10"), new BigDecimal("5.50"), EUR));
        scalingPriceSchemes.put(KRW, ScalingPriceVO.of("box", new BigDecimal("1"), new BigDecimal("120000"), new BigDecimal("1"), new BigDecimal("110000"), KRW));
        scalingPriceSchemes.put(KWD, ScalingPriceVO.of("hr", new BigDecimal("0.5"), new BigDecimal("15.250"), new BigDecimal("0.25"), new BigDecimal("5.750"), KWD));

        // 3. TIERED SCHEMES
        tieredPriceSchemes.put(GBP, new TieredPriceVO("license", List.of(PriceTier.of(BigDecimal.ZERO, new BigDecimal("10.00"), GBP)), GBP));
        tieredPriceSchemes.put(CLP, new TieredPriceVO("compute", List.of(PriceTier.of(BigDecimal.ZERO, new BigDecimal("45000"), CLP)), CLP));
        tieredPriceSchemes.put(OMR, new TieredPriceVO("api-call", List.of(PriceTier.of(BigDecimal.ZERO, new BigDecimal("0.385"), OMR)), OMR));
    }

    @Test
    @DisplayName("Print Audit: Comprehensive Global Pricing View")
    void printPriceMapsAudit() {
        System.out.println("\n[AUDIT 2026] --- GLOBAL PRICING MAPS (VARIED PRECISION) ---");

        System.out.println("\n1. FIXED PRICE MAP (Mixed ISO Scales):");
        fixedPrices.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Currency::getCurrencyCode)))
                .forEach(e -> System.out.printf("   %-5s | %-12s (Scale: %d)%n",
                        e.getKey().getCurrencyCode(), format(e.getValue().price(), e.getKey()), e.getKey().getDefaultFractionDigits()));

        System.out.println("\n2. SCALING SCHEMES (Mixed ISO Scales):");
        scalingPriceSchemes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Currency::getCurrencyCode)))
                .forEach(e -> System.out.printf("   %-5s | %-12s (Starts at %s %s, Scale: %d)%n",
                        e.getKey().getCurrencyCode(), format(e.getValue().basePrice(), e.getKey()),
                        e.getValue().baseThreshold(), e.getValue().unit(), e.getKey().getDefaultFractionDigits()));

        System.out.println("\n3. TIERED SCHEMES (Mixed ISO Scales):");
        tieredPriceSchemes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Currency::getCurrencyCode)))
                .forEach(e -> System.out.printf("   %-5s | %-12s (Unit: %-10s, Scale: %d)%n",
                        e.getKey().getCurrencyCode(), format(e.getValue().tiers().getFirst().price(), e.getKey()),
                        e.getValue().unit(), e.getKey().getDefaultFractionDigits()));

        System.out.println("\n-------------------------------------------------------------");
    }

    private String format(BigDecimal amount, Currency currency) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setCurrency(currency);

        int precision = currency.getDefaultFractionDigits();
        formatter.setMinimumFractionDigits(precision);
        formatter.setMaximumFractionDigits(precision);

        return formatter.format(amount);
    }
}
