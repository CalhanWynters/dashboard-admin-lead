package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;
import java.util.Map;

public final class PriceListBehavior {

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Price List creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyPriceModificationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify individual prices.", "SEC-403", actor);
        }
    }

    public static void verifyBulkAdjustmentAuthority(Actor actor) {
        // SOC 2: Bulk changes often require higher clearance (Admin)
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Bulk price adjustments are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void verifyHardDeleteAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Hard deletes are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void verifyActivationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Activation status changes require Manager or Admin roles.", "SEC-403", actor);
        }
    }


    public static void validateStrategyMatch(Class<? extends PurchasePricing> boundary, PurchasePricing pricing) {
        DomainGuard.notNull(pricing, "Pricing strategy");
        if (!boundary.isInstance(pricing)) {
            throw new IllegalStateException("Domain Violation: Price strategy mismatch for " + boundary.getSimpleName());
        }
    }

    public static void ensureActive(boolean isActive) {
        DomainGuard.ensure(isActive, "Cannot modify pricing on an inactive Price List.", "VAL-099", "LIFECYCLE_VIOLATION");
    }

    public static boolean evaluateActivation(boolean current, boolean next) {
        if (current == next) {
            throw new IllegalArgumentException("Price List is already " + (current ? "active" : "inactive"));
        }
        return next;
    }

    public static PriceListVersion evaluateVersionIncrement(PriceListVersion current) {
        return new PriceListVersion(current.value().next());
    }

    public static void ensureTargetExists(Map<UuId, Map<Currency, PurchasePricing>> prices, UuId targetId, Currency currency) {
        DomainGuard.notNull(targetId, "Target Identity");
        DomainGuard.notNull(currency, "Currency");
        if (!prices.containsKey(targetId) || !prices.get(targetId).containsKey(currency)) {
            throw new IllegalArgumentException("Pricing for target %s in %s not found.".formatted(targetId.value(), currency.getCurrencyCode()));
        }
    }

    public static void ensureActivationTransition(boolean current, boolean target) {
        if (current == target) {
            throw new IllegalArgumentException("Price List is already " + (current ? "active" : "inactive"));
        }
    }

    public static void validateBulkAdjustment(double percentage) {
        if (percentage < -100.0) {
            throw new IllegalArgumentException("Price decrease cannot exceed 100%");
        }
    }
}
