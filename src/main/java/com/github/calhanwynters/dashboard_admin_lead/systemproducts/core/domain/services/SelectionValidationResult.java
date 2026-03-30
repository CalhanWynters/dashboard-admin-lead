package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.services;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Encapsulates the outcome of a compatibility check.
 * 2026 Edition: Integrated with rich Domain Name and UuId wrappers.
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
        // Corrected mapping: FeaturesName -> Name -> String value
        String names = violations.stream()
                .map(f -> f.getFeaturesName().value().value())
                .collect(Collectors.joining(", "));

        return new SelectionValidationResult(
                false,
                violations,
                "Incompatibility detected. The following features are forbidden under current constraints: " + names
        );
    }

    /**
     * Extracts the raw UuIds of problematic features for API error responses.
     */
    public Set<UuId> getConflictingIds() {
        return conflictingFeatures.stream()
                .map(f -> f.getUuId().value()) // From BaseAggregateRoot.getUuId().value()
                .collect(Collectors.toUnmodifiableSet());
    }
}
