package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.services;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Encapsulates the outcome of a compatibility check.
 */
public record SelectionValidationResult(
        boolean isValid,
        Set<FeaturesAggregate> conflictingFeatures,
        String failureReason
) {
    public static SelectionValidationResult valid() {
        return new SelectionValidationResult(true, Collections.emptySet(), null);
    }

    public static SelectionValidationResult invalid(Set<FeaturesAggregate> violations) {
        String names = violations.stream()
                .map(f -> f.getFeaturesUuId().value().toString()) // Or feature name if available
                .collect(Collectors.joining(", "));

        return new SelectionValidationResult(
                false,
                violations,
                "The following features are incompatible with your current selections: " + names
        );
    }

    /**
     * Helper for Application Layer to quickly extract UuIds of problematic features.
     */
    public Set<UuId> getConflictingIds() {
        return conflictingFeatures.stream()
                .map(f -> f.getFeaturesUuId().value())
                .collect(Collectors.toUnmodifiableSet());
    }
}
