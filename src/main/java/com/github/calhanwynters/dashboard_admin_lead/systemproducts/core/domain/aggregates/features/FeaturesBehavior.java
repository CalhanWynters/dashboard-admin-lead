package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Features.
 * Performs all calculations and invariant checks without side effects.
 */
public final class FeaturesBehavior {

    // Record to group update results
    public record DetailsPatch(FeatureName name, FeatureLabel tag) {}

    /**
     * Logic for creating a new feature.
     */
    public static void validateCreation(FeatureUuId uuId, FeatureBusinessUuId bUuId, FeatureName name, FeatureLabel tag) {
        DomainGuard.notNull(uuId, "Feature UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
        DomainGuard.notNull(name, "Feature Name");
        DomainGuard.notNull(tag, "Compatibility Tag");
    }

    /**
     * Logic for updating details.
     */
    public static DetailsPatch evaluateUpdate(FeatureName newName, FeatureLabel newTag) {
        DomainGuard.notNull(newName, "New Feature Name");
        DomainGuard.notNull(newTag, "New Compatibility Tag");
        return new DetailsPatch(newName, newTag);
    }

    /**
     * Logic for changing the Business ID.
     * Invariants: New ID cannot be null and must be different from the current one.
     */
    public static FeatureBusinessUuId evaluateBusinessIdChange(FeatureBusinessUuId currentId, FeatureBusinessUuId newId) {
        DomainGuard.notNull(newId, "New Business UUID");
        if (currentId.equals(newId)) {
            throw new IllegalArgumentException("The new Business ID must be different from the current one.");
        }
        return newId;
    }

    /**
     * Logic for Soft Deletion.
     * Invariants: Check if already deleted (if status exists in state).
     */
    public static void verifyDeletable() {
        // Add business rules here (e.g., cannot delete if linked to active product)
    }

    /**
     * Logic for Restoration.
     */
    public static void verifyRestorable() {
        // Add business rules here (e.g., cannot restore if parent is deleted)
    }

    /**
     * Validates a tag change against the global compatibility policy.
     * Note: In a Fail-Fast system, complex "Impact Analysis" happens in the
     * Application Service, while this method ensures the tag itself is valid.
     */
    public static FeatureLabel evaluateCompatibilityChange(
            FeatureLabel newTag,
            FeatureLabel currentTag) {

        DomainGuard.notNull(newTag, "New Compatibility Tag");

        if (newTag.equals(currentTag)) {
            throw new IllegalArgumentException("The new tag is identical to the current tag.");
        }

        return newTag;
    }
}
