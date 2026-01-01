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

        // --- PROLOGUE (Before super) ---
        // Validate existence before parent initialization
        if (schemes == null || schemes.isEmpty()) {
            throw new DomainValidationException("FeatureScalingPriceEntity requires at least one pricing scheme.");
        }

        // Defensive Copy: Capture unmodifiable snapshot in prologue to prevent external mutations
        Map<Currency, ScalingPriceVO> snapshot = Map.copyOf(schemes);

        // Integrity Check: Use the snapshot to ensure cross-field consistency
        snapshot.forEach((currency, vo) -> {
            if (!currency.equals(vo.currency())) {
                throw new DomainValidationException("Mismatch: Key " + currency + " does not match VO currency " + vo.currency());
            }
        });

        // 2. Execute super constructor
        super(featureId, featureUuId, featureName, featureLabel, featureDescription, featureStatus, featureVersion, lastModified, isUnique);

        // --- EPILOGUE (After super) ---
        // 3. Assign the validated snapshot to the final field
        this.scalingPriceSchemes = snapshot;
    }

    public static FeatureScalingPriceEntity create(PkIdVO id, UuIdVO uuid, NameVO name, LabelVO label, DescriptionVO desc,
                                                   StatusEnums status, VersionVO version, LastModifiedVO modified, boolean unique,
                                                   Map<Currency, ScalingPriceVO> schemes) {
        return new FeatureScalingPriceEntity(id, uuid, name, label, desc, status, version, modified, unique, schemes);
    }

    /**
     * FIXED: Implementation avoids eager evaluation.
     * Manual null check ensures the exception is ONLY thrown if the key is missing.
     */
    public BigDecimal calculatePrice(Currency requestedCurrency, BigDecimal quantity) {
        Objects.requireNonNull(requestedCurrency, "Currency cannot be null");

        // Perform lookup
        ScalingPriceVO scheme = scalingPriceSchemes.get(requestedCurrency);

        // Lazy Throw: Only execute exception logic if the scheme is not found
        if (scheme == null) {
            throw createMissingCurrencyException(requestedCurrency);
        }

        return scheme.calculate(quantity);
    }

    /**
     * Factory method for the exception to maintain clean stack traces.
     */
    private DomainValidationException createMissingCurrencyException(Currency currency) {
        return new DomainValidationException("Pricing not defined for currency: " + currency.getCurrencyCode());
    }

    public Map<Currency, ScalingPriceVO> getScalingPriceSchemes() {
        return scalingPriceSchemes;
    }

    public boolean supportsCurrency(Currency currency) {
        return scalingPriceSchemes.containsKey(currency);
    }
}
