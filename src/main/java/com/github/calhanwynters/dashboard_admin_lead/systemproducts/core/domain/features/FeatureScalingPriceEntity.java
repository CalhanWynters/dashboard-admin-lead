package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;

public class FeatureScalingPriceEntity extends FeatureAbstractClass {

    private final Map<Currency, ScalingPriceVO> scalingPriceSchemes;

    public FeatureScalingPriceEntity(
            PkIdVO featureId,
            UuIdVO featureUuId,
            NameVO featureName,
            LabelVO featureLabel,
            DescriptionVO featureDescription,
            StatusEnums featureStatus,
            VersionVO featureVersion,
            LastModifiedVO lastModified,
            Boolean isUnique,
            Map<Currency, ScalingPriceVO> scalingPriceSchemes
    ) {
        super(featureId, featureUuId, featureName, featureLabel,
                featureDescription, featureStatus, featureVersion,
                lastModified, isUnique);

        // Null and empty map handling
        this.scalingPriceSchemes = validateAndInitializePriceSchemes(scalingPriceSchemes);
    }

    // Validation method for price schemes
    private Map<Currency, ScalingPriceVO> validateAndInitializePriceSchemes(
            Map<Currency, ScalingPriceVO> inputSchemes) {

        // If no schemes provided, create a default USD scheme
        if (inputSchemes == null || inputSchemes.isEmpty()) {
            Currency usd = Currency.getInstance("USD");
            ScalingPriceVO defaultScheme = ScalingPriceVO.of(
                    "default",
                    BigDecimal.ONE,      // base threshold
                    BigDecimal.ZERO,     // base price
                    BigDecimal.ONE,      // increment step
                    BigDecimal.ZERO,     // price per step
                    usd
            );
            return Map.of(usd, defaultScheme);
        }

        // Validate each scheme
        return Map.copyOf(inputSchemes);
    }

    // Retrieve all scaling price schemes
    public Map<Currency, ScalingPriceVO> getScalingPriceSchemes() {
        return Map.copyOf(scalingPriceSchemes);
    }

    // Get scaling price scheme for a specific currency
    public Optional<ScalingPriceVO> getScalingPriceForCurrency(Currency currency) {
        return Optional.ofNullable(scalingPriceSchemes.get(currency));
    }

    // Calculate price for a specific currency and quantity
    public Optional<BigDecimal> calculatePrice(Currency currency, BigDecimal quantity) {
        return getScalingPriceForCurrency(currency)
                .map(scheme -> scheme.calculate(quantity));
    }

}
