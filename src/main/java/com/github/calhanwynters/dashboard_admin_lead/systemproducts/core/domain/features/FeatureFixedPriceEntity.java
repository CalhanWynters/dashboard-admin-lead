package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Specialized entity for features with fixed monetary costs across multiple currencies.
 * Optimized for 2025 DDD standards with defensive building and immutable state.
 */
public class FeatureFixedPriceEntity extends FeatureAbstractClass {

    private final Map<Currency, PriceVO> fixedPrices;

    private FeatureFixedPriceEntity(Builder builder) {
        super(
                builder.featureId,
                builder.featureUuId,
                builder.featureName,
                builder.featureLabel,
                builder.featureDescription,
                builder.featureStatus,
                builder.featureVersion,
                builder.lastModified,
                builder.isUnique
        );

        if (builder.fixedPrices == null || builder.fixedPrices.isEmpty()) {
            this.fixedPrices = Map.of(Currency.getInstance("USD"), new PriceVO(BigDecimal.ZERO));
        } else {
            // Ensure the internal map is truly immutable and validated
            this.fixedPrices = Map.copyOf(builder.fixedPrices.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> validatePrice(entry.getKey(), entry.getValue())
                    )));
        }
    }

    private PriceVO validatePrice(Currency currency, PriceVO price) {
        Objects.requireNonNull(currency, "Currency key in price map must not be null");
        Objects.requireNonNull(price, "Price value for currency " + currency + " must not be null");

        if (!price.currency().equals(currency)) {
            throw new IllegalStateException(
                    String.format("Currency mismatch: Map key is %s but PriceVO is %s",
                            currency, price.currency())
            );
        }
        return price;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<Currency, PriceVO> getFixedPrices() {
        return fixedPrices; // Map.copyOf was used in constructor, so this is safe
    }

    public Optional<PriceVO> getPriceForCurrency(Currency currency) {
        Objects.requireNonNull(currency, "Currency lookup parameter must not be null");
        return Optional.ofNullable(fixedPrices.get(currency));
    }

    public static class Builder {
        private PkIdVO featureId;
        private UuIdVO featureUuId;
        private NameVO featureName;
        private LabelVO featureLabel;
        private DescriptionVO featureDescription;
        private StatusEnums featureStatus;
        private VersionVO featureVersion;
        private LastModifiedVO lastModified;
        private Boolean isUnique = false;
        private Map<Currency, PriceVO> fixedPrices = new HashMap<>();

        public Builder featureId(PkIdVO id) { this.featureId = id; return this; }
        public Builder featureUuId(UuIdVO uuid) { this.featureUuId = uuid; return this; }
        public Builder featureName(NameVO name) { this.featureName = name; return this; }
        public Builder featureLabel(LabelVO label) { this.featureLabel = label; return this; }
        public Builder featureDescription(DescriptionVO ds) { this.featureDescription = ds; return this; }
        public Builder featureStatus(StatusEnums st) { this.featureStatus = st; return this; }
        public Builder featureVersion(VersionVO v) { this.featureVersion = v; return this; }
        public Builder lastModified(LastModifiedVO lm) { this.lastModified = lm; return this; }
        public Builder isUnique(Boolean unique) { this.isUnique = unique; return this; }

        public Builder addPrice(Currency currency, PriceVO price) {
            this.fixedPrices.put(currency, price);
            return this;
        }

        public Builder fixedPrices(Map<Currency, PriceVO> prices) {
            this.fixedPrices = prices;
            return this;
        }

        public FeatureFixedPriceEntity build() {
            validate();
            return new FeatureFixedPriceEntity(this);
        }

        private void validate() {
            if (featureName == null || featureLabel == null) {
                throw new IllegalStateException("Naming VOs are required for Fixed Price entities.");
            }

            // Cross-Field Consistency:
            // If a feature is unique (variant-specific), it must not contain multiple currency global mappings.
            if (Boolean.TRUE.equals(isUnique) && fixedPrices.size() > 1) {
                throw new IllegalStateException(
                        "Unique (variant-specific) features cannot have multi-currency price lists."
                );
            }
        }

    }
}
