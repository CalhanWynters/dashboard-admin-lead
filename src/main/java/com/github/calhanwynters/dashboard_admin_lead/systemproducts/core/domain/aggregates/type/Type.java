package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.Money;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.PurchasePricingFactory;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.SimplePurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.validationchecks.DomainGuard;

import java.math.BigDecimal;
import java.util.Currency;

public record Type(
        Name typeName,
        Dimensions typeDimensions,
        Weight typeWeight,
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

        // Validating dimensions and weight if provided
        if (typeDimensions != null) {
            DomainGuard.notNull(typeDimensions.length(), "Length cannot be null");
            DomainGuard.notNull(typeDimensions.width(), "Width cannot be null");
            DomainGuard.notNull(typeDimensions.height(), "Height cannot be null");
        }

        if (typeWeight != null) {
            DomainGuard.positive(typeWeight.amount(), "Weight amount must be positive");
        }
    }

    /**
     * Static factory for fixed pricing.
     */
    public static Type createWithFixedPricing(
            PurchasePricingFactory factory,
            Name typeName,
            Dimensions typeDimensions,
            Weight typeWeight,
            Description typeDescription,
            CareInstruction typeCareInstruction,
            Money fixedPrice) {

        DomainGuard.notNull(fixedPrice, "Fixed Price cannot be null");
        if (fixedPrice.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fixed price must be non-negative.");
        }

        SimplePurchasePricing pricing = factory.createFixedPurchase(fixedPrice);
        return new Type(typeName, typeDimensions, typeWeight, typeDescription, typeCareInstruction, pricing);
    }

    /**
     * Static factory for types without a purchase price.
     */
    public static Type createWithoutPricing(
            PurchasePricingFactory factory,
            Name typeName,
            Dimensions typeDimensions,
            Weight typeWeight,
            Description typeDescription,
            CareInstruction typeCareInstruction,
            Currency currency) {

        DomainGuard.notNull(currency, "Currency cannot be null");
        SimplePurchasePricing pricing = factory.createNonePurchase(currency);
        return new Type(typeName, typeDimensions, typeWeight, typeDescription, typeCareInstruction, pricing);
    }

    // Additional factory methods could be added here for flexibility

    /**
     * Getter method for Type pricing information.
     */
    public BigDecimal getPrice(BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative.");
        }

        Money price = pricingModel.calculate(quantity);
        return price.amount(); // Assuming Money has a method to get the BigDecimal amount
    }
}
