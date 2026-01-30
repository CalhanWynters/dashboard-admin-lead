package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.type.Type;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.Set;
import java.util.stream.Collectors;

public record FeatureCompatibilityPolicy(Set<IncompatibilityRule> rules) {

    /**
     * Calculates which feature IDs should be greyed out on the UI.
     *
     * @param selectedFeatures The full set of Feature Value Objects currently selected.
     * @param selectedTypes    The full set of Type Value Objects currently selected.
     */
    public Set<UuId> getIncompatibleWith(Set<Feature> selectedFeatures, Set<Type> selectedTypes) {
        // 1. Map selections to their stable IDs
        Set<UuId> selectedIds = selectedFeatures.stream()
                .map(Feature::featureUuId).collect(Collectors.toSet());
        selectedIds.addAll(selectedTypes.stream()
                .map(Type::typeId).collect(Collectors.toSet()));

        // 2. Map selections to their compatibility Labels (Tags)
        Set<Label> selectedTags = selectedFeatures.stream()
                .map(Feature::compatibilityTag).collect(Collectors.toSet());
        selectedTags.addAll(selectedTypes.stream()
                .map(Type::compatibilityTag).collect(Collectors.toSet()));

        // 3. Filter rules triggered by current selection and return forbidden IDs
        return rules.stream()
                .filter(rule -> rule.isTriggeredBy(selectedIds, selectedTags))
                .map(IncompatibilityRule::forbiddenFeatureUuId)
                .collect(Collectors.toSet());
    }

}