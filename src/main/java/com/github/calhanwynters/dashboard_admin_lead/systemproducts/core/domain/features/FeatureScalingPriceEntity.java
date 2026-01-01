package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;

public class FeatureScalingPriceEntity extends FeatureAbstractClass {
    private final Map<Currency, ScalingPriceVO> scalingPriceSchemes;

    // 1. Utilizing Java 25 Flexible Constructor Bodies
    private FeatureScalingPriceEntity(PkIdVO featureId, UuIdVO featureUuId, NameVO featureName, LabelVO featureLabel,
                                      DescriptionVO featureDescription, StatusEnums featureStatus, VersionVO featureVersion,
                                      LastModifiedVO lastModified, boolean isUnique, Map<Currency, ScalingPriceVO> schemes) {

        // Validation logic can now happen BEFORE the super constructor call in Java 25
        if (schemes == null || schemes.isEmpty()) {
            throw new DomainValidationException("FeatureScalingPriceEntity requires at least one pricing scheme.");
        }

        // Integrity Check: Validate currency consistency before state is set
        schemes.forEach((currency, vo) -> {
            if (!currency.equals(vo.currency())) {
                throw new DomainValidationException("Mismatch: Key " + currency + " does not match VO currency " + vo.currency());
            }
        });

        // Safe defensive copy
        Map<Currency, ScalingPriceVO> immutableSchemes = Map.copyOf(schemes);

        // Finally call the super constructor
        super(featureId, featureUuId, featureName, featureLabel, featureDescription, featureStatus, featureVersion, lastModified, isUnique);

        this.scalingPriceSchemes = immutableSchemes;
    }

    public static FeatureScalingPriceEntity create(PkIdVO id, UuIdVO uuid, NameVO name, LabelVO label, DescriptionVO desc,
                                                   StatusEnums status, VersionVO version, LastModifiedVO modified, boolean unique,
                                                   Map<Currency, ScalingPriceVO> schemes) {
        return new FeatureScalingPriceEntity(id, uuid, name, label, desc, status, version, modified, unique, schemes);
    }

    /**
     * Calculates the price using the hardened arithmetic logic of the VO.
     * Logic here is minimal because the entity trusts the VO's self-validation.
     */
    public BigDecimal calculatePrice(Currency requestedCurrency, BigDecimal quantity) {
        return scalingPriceSchemes.getOrDefault(
                Objects.requireNonNull(requestedCurrency, "Currency cannot be null"),
                throwMissingCurrencyException(requestedCurrency)
        ).calculate(quantity);
    }

    private ScalingPriceVO throwMissingCurrencyException(Currency currency) {
        throw new DomainValidationException("Pricing not defined for currency: " + currency.getCurrencyCode());
    }


    public Map<Currency, ScalingPriceVO> getScalingPriceSchemes() {
        return scalingPriceSchemes;
    }

    public boolean supportsCurrency(Currency currency) {
        return scalingPriceSchemes.containsKey(currency);
    }
}
