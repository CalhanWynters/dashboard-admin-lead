package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

/*
 * Represents a Product with no Type but has variants.
 * Variants are different from Types in that they represent price deltas rather than the core price.
 * Variants are composed of features to chose from and have minimal effect on overall price.
 */

public class ProductVariant extends ProductAbstract {

    // Super builder
    // ADDITIONAL FIELD: Care Instructions
    // ADDITIONAL FIELD: PurchasePricing
    // ADDITIONAL FIELD: Dimensions
    // ADDITIONAL FIELD: Weight

    // ADDITIONAL FIELD: Set of Features  * Consider Decoupling list/set into another aggregate/entity
    // Reference list or sets with lifecycle ID

}
