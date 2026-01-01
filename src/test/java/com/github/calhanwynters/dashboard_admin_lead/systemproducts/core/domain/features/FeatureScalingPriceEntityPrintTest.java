package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeatureScalingPriceEntity State Inspection Tests")
class FeatureScalingPriceEntityPrintTest {

    @Mock private PkIdVO mockId;
    @Mock private UuIdVO mockUuid;
    @Mock private NameVO mockName;
    @Mock private LabelVO mockLabel;
    @Mock private DescriptionVO mockDesc;
    @Mock private VersionVO mockVersion;
    @Mock private LastModifiedVO mockModified;

    private static final Currency USD = Currency.getInstance("USD");
    private Map<Currency, ScalingPriceVO> validSchemes;

    @BeforeEach
    void setUp() {
        validSchemes = new HashMap<>();
        var standardVo = ScalingPriceVO.of(
                "units",
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                new BigDecimal("5.00"),
                new BigDecimal("20.00"),
                USD
        );
        validSchemes.put(USD, standardVo);
    }

    @Test
    @DisplayName("DEBUG: Print internal data format and snapshots")
    void printDataFormat() {
        System.out.println("=== [START] DOMAIN DATA INSPECTION ===");

        // 1. Inspect the Source Map
        System.out.println("SOURCE MAP (Before Entity Creation):");
        validSchemes.forEach((curr, vo) ->
                System.out.printf("  Currency: %s -> %s%n", curr.getCurrencyCode(), vo)
        );

        // 2. Create the Entity
        var entity = FeatureScalingPriceEntity.create(
                mockId, mockUuid, mockName, mockLabel, mockDesc,
                StatusEnums.ACTIVE, mockVersion, mockModified, true,
                validSchemes
        );

        // 3. Inspect Internal Entity State
        System.out.println("\nENTITY SNAPSHOT (Internal scalingPriceSchemes):");
        entity.getScalingPriceSchemes().forEach((curr, vo) ->
                System.out.printf("  Stored Key: %s | VO Value: %s%n", curr.getCurrencyCode(), vo)
        );

        // 4. Verify Defensive Copy Proof
        validSchemes.clear();
        System.out.println("\nSOURCE MAP CLEARED. Verifying Entity Persistence...");
        System.out.println("  Entity still contains USD: " + entity.supportsCurrency(USD));

        System.out.println("=== [END] DOMAIN DATA INSPECTION ===");
    }
}
