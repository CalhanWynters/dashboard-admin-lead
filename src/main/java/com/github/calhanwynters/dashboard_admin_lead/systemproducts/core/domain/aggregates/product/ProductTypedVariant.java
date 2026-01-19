package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type.Type;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type.TypeCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.Feature;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.IncompatibilityRule;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.VariantCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a complex product with both types and variants.
 * Orchestrates compatibility rules across both dimensions.
 */
public class ProductTypedVariant extends ProductAbstract {

    private final TypeCollection types;
    private final VariantCollection features;
    private final Set<IncompatibilityRule> internalRules; // Feature vs Feature
    private final Set<IncompatibilityRule> contextualRules; // Type vs Feature

    private ProductTypedVariant(Builder builder) {
        super(builder);
        this.types = Objects.requireNonNull(builder.types, "TypeCollection cannot be null");
        this.features = Objects.requireNonNull(builder.features, "VariantCollection cannot be null");
        this.internalRules = Objects.requireNonNull(builder.internalRules, "Internal rules cannot be null");
        this.contextualRules = Objects.requireNonNull(builder.contextualRules, "Contextual rules cannot be null");
        validateIntegrity();
    }

    private void validateIntegrity() {
        // 1. Collect all selected IDs
        Set<UuId> selectedFeatureIds = features.getFeatures().stream()
                .map(Feature::featureUuId).collect(Collectors.toSet());
        Set<UuId> selectedTypeIds = types.getTypes().stream()
                .map(Type::typeId).collect(Collectors.toSet());

        // 2. Collect all selected Compatibility Tags (Labels) for categorical rules
        Set<Label> selectedFeatureTags = features.getFeatures().stream()
                .map(Feature::compatibilityTag).collect(Collectors.toSet());
        Set<Label> selectedTypeTags = types.getTypes().stream()
                .map(Type::compatibilityTag).collect(Collectors.toSet());

        // 3. Check Internal Feature Incompatibility (Feature vs Feature)
        for (IncompatibilityRule rule : internalRules) {
            // Use the new multi-trigger check
            if (rule.isTriggeredBy(selectedFeatureIds, selectedFeatureTags)
                    && selectedFeatureIds.contains(rule.forbiddenFeatureUuId())) {

                throw new IllegalStateException(String.format(
                        "Feature conflict: Trigger (ID: %s / Tag: %s) blocks Feature ID: %s",
                        rule.triggerUuId(), rule.triggerTag(), rule.forbiddenFeatureUuId()
                ));
            }
        }

        // 4. Check Type vs Feature Incompatibility (Contextual)
        for (IncompatibilityRule rule : contextualRules) {
            // Contextual rules are triggered by Types (ID or Tag) and block Features
            if (rule.isTriggeredBy(selectedTypeIds, selectedTypeTags)
                    && selectedFeatureIds.contains(rule.forbiddenFeatureUuId())) {

                throw new IllegalStateException(String.format(
                        "Type-Feature conflict: Type Trigger blocks Feature ID: %s",
                        rule.forbiddenFeatureUuId()
                ));
            }
        }
    }


    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public TypeCollection getTypes() { return types; }
    public VariantCollection getFeatures() { return features; }
    public Set<IncompatibilityRule> getInternalRules() { return internalRules; }
    public Set<IncompatibilityRule> getContextualRules() { return contextualRules; }

    public static class Builder extends ProductAbstract.Builder<ProductTypedVariant, Builder> {
        private TypeCollection types;
        private VariantCollection features;
        private Set<IncompatibilityRule> internalRules = Set.of();
        private Set<IncompatibilityRule> contextualRules = Set.of();

        @Override
        protected Builder self() { return this; }

        public Builder types(TypeCollection types) { this.types = types; return self(); }
        public Builder features(VariantCollection features) { this.features = features; return self(); }
        public Builder internalRules(Set<IncompatibilityRule> rules) { this.internalRules = rules; return self(); }
        public Builder contextualRules(Set<IncompatibilityRule> rules) { this.contextualRules = rules; return self(); }

        @Override
        public ProductTypedVariant build() { return new ProductTypedVariant(this); }
    }
}
