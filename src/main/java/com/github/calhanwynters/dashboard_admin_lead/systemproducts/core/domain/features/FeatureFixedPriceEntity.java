package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class FeatureFixedPriceEntity extends FeatureAbstractClass {

    private final Map<Currency, PriceVO> fixedPrices;

    public FeatureFixedPriceEntity(
            PkIdVO featureId,
            UuIdVO featureUuId,
            NameVO featureName,
            LabelVO featureLabel,
            DescriptionVO featureDescription,
            StatusEnums featureStatus,
            VersionVO featureVersion,
            LastModifiedVO lastModified,
            Boolean isUnique,
            Map<Currency, PriceVO> fixedPrices) {

        super(featureId, featureUuId, featureName, featureLabel,
                featureDescription, featureStatus, featureVersion,
                lastModified, isUnique);

        if (fixedPrices == null || fixedPrices.isEmpty()) {
            // Default to USD with zero price if no prices provided
            this.fixedPrices = Map.of(
                    Currency.getInstance("USD"),
                    new PriceVO(BigDecimal.ZERO)
            );
        } else {
            // Validate each price entry
            this.fixedPrices = fixedPrices.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> validatePrice(entry.getKey(), entry.getValue())
                    ));
        }
    }

    // Validation method for individual prices
    private PriceVO validatePrice(Currency currency, PriceVO price) {
        Objects.requireNonNull(currency, "Currency cannot be null");
        Objects.requireNonNull(price, "Price cannot be null for currency " + currency);

        // Ensure price currency matches the map key
        if (!price.currency().equals(currency)) {
            throw new IllegalArgumentException(
                    "Price currency " + price.currency() +
                            " does not match map key currency " + currency
            );
        }

        return price;
    }

    // Retrieve all fixed prices in their associated currencies
    public Map<Currency, PriceVO> getFixedPrices() {
        return Map.copyOf(fixedPrices);
    }

    // Convenience method to get price for a specific currency
    public Optional<PriceVO> getPriceForCurrency(Currency currency) {
        Objects.requireNonNull(currency, "Currency cannot be null");
        return Optional.ofNullable(fixedPrices.get(currency));
    }

}
