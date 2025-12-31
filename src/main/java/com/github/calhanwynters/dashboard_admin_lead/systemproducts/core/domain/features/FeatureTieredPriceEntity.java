package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;


import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FeatureTieredPriceEntity extends FeatureAbstractClass{

    private final Map<Currency, TieredPriceVO> tieredPriceSchemes;

    public FeatureTieredPriceEntity(
            PkIdVO featureId,
            UuIdVO featureUuId,
            NameVO featureName,
            LabelVO featureLabel,
            DescriptionVO featureDescription,
            StatusEnums featureStatus,
            VersionVO featureVersion,
            LastModifiedVO lastModified,
            Boolean isUnique,
            Map<Currency, TieredPriceVO> tieredPriceSchemes
    ) {
        super(featureId, featureUuId, featureName, featureLabel,
                featureDescription, featureStatus, featureVersion,
                lastModified, isUnique);

        // Validate and initialize price schemes
        this.tieredPriceSchemes = validateAndInitializePriceSchemes(tieredPriceSchemes);
    }

    // Validation method for price schemes
    private Map<Currency, TieredPriceVO> validateAndInitializePriceSchemes(
            Map<Currency, TieredPriceVO> inputSchemes) {

        // If no schemes provided, create a default USD scheme
        if (inputSchemes == null || inputSchemes.isEmpty()) {
            Currency usd = Currency.getInstance("USD");
            TieredPriceVO defaultScheme = new TieredPriceVO(
                    "default",
                    List.of(
                            new TieredPriceVO.PriceTier(BigDecimal.ZERO, BigDecimal.ZERO),
                            new TieredPriceVO.PriceTier(BigDecimal.ONE, BigDecimal.TEN)
                    ),
                    usd
            );
            return Map.of(usd, defaultScheme);
        }

        // Validate each scheme
        return Map.copyOf(inputSchemes);
    }

    // Retrieve all tiered price schemes
    public Map<Currency, TieredPriceVO> getTieredPriceSchemes() {
        return Map.copyOf(tieredPriceSchemes);
    }

    // Get tiered price scheme for a specific currency
    public Optional<TieredPriceVO> getTieredPriceForCurrency(Currency currency) {
        return Optional.ofNullable(tieredPriceSchemes.get(currency));
    }

    // Calculate price for a specific currency and quantity
    public Optional<BigDecimal> calculatePrice(Currency currency, BigDecimal quantity) {
        return getTieredPriceForCurrency(currency)
                .map(scheme -> scheme.calculate(quantity));
    }


}
