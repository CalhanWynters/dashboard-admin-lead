package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.IncompatibilityRule;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.VariantCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.Feature;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.CareInstruction;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.PurchasePricing;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductVariant extends ProductAbstract {

    private final VariantCollection features;
    private final PurchasePricing pricingModel;
    private final CareInstruction typeCareInstruction;
    private final Set<IncompatibilityRule> compatibilityRules; // The "Grey-out" Rules

    private ProductVariant(Builder builder) {
        super(builder);
        this.features = Objects.requireNonNull(builder.features, "Variant features cannot be null");
        this.pricingModel = Objects.requireNonNull(builder.pricingModel, "Pricing model cannot be null");
        this.typeCareInstruction = Objects.requireNonNull(builder.typeCareInstruction, "Care instructions cannot be null");
        this.compatibilityRules = Objects.requireNonNull(builder.compatibilityRules, "Rules cannot be null");
        validateIntegrity();
    }

    private void validateIntegrity() {
        // 1. Extract IDs and Compatibility Tags from the selected features
        Set<UuId> selectedIds = features.getFeatures().stream()
                .map(Feature::featureUuId)
                .collect(Collectors.toSet());

        Set<Label> selectedTags = features.getFeatures().stream()
                .map(Feature::compatibilityTag)
                .collect(Collectors.toSet());

        // 2. Iterate through rules using the dual-trigger check
        for (IncompatibilityRule rule : compatibilityRules) {
            // rule.isTriggeredBy checks both triggerUuId and triggerTag
            if (rule.isTriggeredBy(selectedIds, selectedTags) &&
                    selectedIds.contains(rule.forbiddenFeatureUuId())) {

                throw new IllegalStateException(String.format(
                        "Domain Violation: Feature configuration conflict. Trigger (ID: %s / Tag: %s) forbids Feature: %s",
                        rule.triggerUuId(),
                        rule.triggerTag() != null ? rule.triggerTag().value() : "None",
                        rule.forbiddenFeatureUuId()
                ));
            }
        }
    }


    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public VariantCollection getFeatures() { return features; }
    public PurchasePricing getPricingModel() { return pricingModel; }
    public CareInstruction getTypeCareInstruction() { return typeCareInstruction; }
    public Set<IncompatibilityRule> getCompatibilityRules() { return compatibilityRules; }

    public static class Builder extends ProductAbstract.Builder<ProductVariant, Builder> {
        private VariantCollection features;
        private PurchasePricing pricingModel;
        private CareInstruction typeCareInstruction;
        private Set<IncompatibilityRule> compatibilityRules = Set.of(); // Default to empty

        @Override
        protected Builder self() {
            return this;
        }

        public Builder features(VariantCollection features) {
            this.features = features;
            return self();
        }

        public Builder pricingModel(PurchasePricing pricingModel) {
            this.pricingModel = pricingModel;
            return self();
        }

        public Builder typeCareInstruction(CareInstruction typeCareInstruction) {
            this.typeCareInstruction = typeCareInstruction;
            return self();
        }

        public Builder compatibilityRules(Set<IncompatibilityRule> rules) {
            this.compatibilityRules = rules;
            return self();
        }

        @Override
        public ProductVariant build() {
            return new ProductVariant(this);
        }
    }
}
