package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.services;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeatureCompatibilityPolicy;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces.IncompatibilityRuleRepository;

import java.util.Set;
import java.util.stream.Collectors;

public class FeatureSelectionService {
    private final IncompatibilityRuleRepository ruleRepository;

    public FeatureSelectionService(IncompatibilityRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public SelectionValidationResult validate(Set<FeaturesAggregate> selected, Set<TypesAggregate> types) {
        var rules = ruleRepository.findAllActiveRules();
        var policy = new FeatureCompatibilityPolicy(rules);

        // Identifies IDs marked as 'forbidden' by the selected features/types
        Set<UuId> forbiddenIds = policy.getIncompatibleWith(selected, types);

        // Cross-reference: Are any of our currently selected features in that forbidden list?
        Set<FeaturesAggregate> violations = selected.stream()
                .filter(feature -> forbiddenIds.contains(feature.getFeaturesUuId().value()))
                .collect(Collectors.toUnmodifiableSet());

        return violations.isEmpty()
                ? SelectionValidationResult.valid()
                : SelectionValidationResult.invalid(violations);
    }
}
