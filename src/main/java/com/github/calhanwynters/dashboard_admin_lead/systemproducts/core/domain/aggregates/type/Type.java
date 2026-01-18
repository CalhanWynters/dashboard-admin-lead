package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.CareInstruction;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.Money;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.PurchasePricingFactory;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.SimplePurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;

import java.math.BigDecimal;
import java.util.Currency;

public record Type(
        Name typeName,
        Description typeDescription,
        CareInstruction typeCareInstruction,
        SimplePurchasePricing pricingModel
) {
    /**
     * Compact Constructor must be PUBLIC to match the record.
     * Use this for final validation of all components.
     */
    public Type {
        DomainGuard.notNull(typeName, "Type Name");
        DomainGuard.notNull(typeDescription, "Type Description");
        DomainGuard.notNull(typeCareInstruction, "Type Care Instruction");
        DomainGuard.notNull(pricingModel, "Pricing Model");

        DomainGuard.ensure(
                !typeName.value().equalsIgnoreCase(typeDescription.text()),
                "Type description must provide additional context beyond the name.",
                "VAL-022", "SEMANTICS"
        );
        DomainGuard.ensure(
                !typeName.value().equalsIgnoreCase(typeCareInstruction.instructions()),
                "Type care instruction must provide specific maintenance guidance.",
                "VAL-023", "SEMANTICS"
        );
    }

    /**
     * Static factory for fixed pricing.
     */
    public static Type createWithFixedPricing(
            PurchasePricingFactory factory,
            Name typeName,
            Description typeDescription,
            CareInstruction typeCareInstruction,
            Money fixedPrice) {

        DomainGuard.notNull(fixedPrice, "Fixed Price cannot be null");
        if (fixedPrice.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fixed price must be non-negative.");
        }

        SimplePurchasePricing pricing = factory.createFixedPurchase(fixedPrice);
        return new Type(typeName, typeDescription, typeCareInstruction, pricing);
    }

    /**
     * Static factory for types without a purchase price.
     */
    public static Type createWithoutPricing(
            PurchasePricingFactory factory,
            Name typeName,
            Description typeDescription,
            CareInstruction typeCareInstruction,
            Currency currency) {

        DomainGuard.notNull(currency, "Currency cannot be null");
        SimplePurchasePricing pricing = factory.createNonePurchase(currency);
        return new Type(typeName, typeDescription, typeCareInstruction, pricing);
    }
}
