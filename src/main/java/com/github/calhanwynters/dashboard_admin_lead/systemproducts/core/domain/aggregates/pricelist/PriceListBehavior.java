package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

public final class PriceListBehavior {

    private PriceListBehavior() {}

    /**
     * Standardized creation validation.
     */
    public static void validateCreation(PriceListUuId uuId, PriceListBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "PriceList UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
    }

    // Inside PriceListBehavior.java

    /**
     * SOC 2: Verifies if the actor is authorized to purge pricing from the matrix.
     * Typically follows the same rules as individual price modifications.
     */
    public static void verifyPriceRemovalAuthority(Actor actor) {
        // Reuses the manager-level check for individual record changes
        verifyPriceModificationAuthority(actor);
    }


    // --- OPERATIONAL GUARDS ---

    public static void ensureOperationalActive(boolean isActive) {
        DomainGuard.ensure(isActive, "Price List is currently inactive.", "VAL-099", "OPERATIONAL_LOCK");
    }

    public static void ensureActivationTransition(boolean current, boolean target) {
        if (current == target) {
            throw new IllegalArgumentException("Price List is already " + (current ? "active" : "inactive"));
        }
    }

    // --- FINANCIAL SPECIALIZATION (Keep these!) ---

    public static void verifyPriceModificationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify individual prices.", "SEC-403", actor);
        }
    }

    public static void verifyBulkAdjustmentAuthority(Actor actor) {
        // SOC 2: Escalated privilege for mass changes
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Bulk price adjustments are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void validateStrategyMatch(PricingStrategyType boundary, PurchasePricing pricing) {
        DomainGuard.notNull(pricing, "Pricing strategy");

        boolean matches = switch (boundary) {
            case FIXED -> pricing instanceof PriceFixedPurchase;
            case NONE -> pricing instanceof PriceNonePurchase;
            case FRACT_TIERED_GRAD -> pricing instanceof PriceFractTieredGradPurchase;
            case FRACT_SCALED -> pricing instanceof PriceFractScaledPurchase;
            case INT_SCALED -> pricing instanceof PriceIntScaledPurchase;
            case INT_TIERED_GRAD -> pricing instanceof PriceIntTieredGradPurchase;
            case INT_TIERED_VOL -> pricing instanceof PriceIntTieredVolPurchase;
            case FRACT_TIERED_VOL -> pricing instanceof PriceFractTieredVolPurchase;
        };

        if (!matches) {
            throw new IllegalStateException(
                    "Domain Violation: Strategy mismatch. Expected " + boundary.name() +
                            " but received " + pricing.getClass().getSimpleName()
            );
        }
    }

    public static void validateBulkAdjustment(double percentage) {
        if (percentage < -100.0) {
            throw new IllegalArgumentException("Price decrease cannot exceed 100%");
        }
    }

    public static PriceListVersion incrementVersion(PriceListVersion current) {
        return new PriceListVersion(current.value().next());
    }
}
