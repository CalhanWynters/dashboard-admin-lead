package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Features.
 * Specific validation logic that extends the base SOC 2 protections.
 */
public final class FeaturesBehavior {

    private FeaturesBehavior() {}

    public record DetailsPatch(FeatureName name, FeatureLabel tag) {}

    /**
     * Feature-specific creation validation.
     * Uses BaseAggregateRoot's static auth check for consistency.
     */
    public static void validateCreation(FeatureUuId uuId, FeatureBusinessUuId bUuId,
                                        FeatureName name, FeatureLabel tag, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);

        DomainGuard.notNull(uuId, "Feature UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
        DomainGuard.notNull(name, "Feature Name");
        DomainGuard.notNull(tag, "Compatibility Tag");
    }

    /**
     * Feature-specific rename logic.
     * Note: Uses Management role check as requested.
     */
    public static FeatureName evaluateRename(FeatureName current, FeatureName next, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify Feature names.", "SEC-403", actor);
        }

        DomainGuard.notNull(next, "New Feature Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

    /**
     * Feature-specific tag update logic.
     */
    public static FeatureLabel evaluateCompatibilityTagUpdate(FeatureLabel currentTag, FeatureLabel newTag, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify compatibility tags.", "SEC-403", actor);
        }

        DomainGuard.notNull(newTag, "New Compatibility Tag");
        if (newTag.equals(currentTag)) {
            throw new IllegalArgumentException("The new tag is identical to the current tag.");
        }
        return newTag;
    }
}
