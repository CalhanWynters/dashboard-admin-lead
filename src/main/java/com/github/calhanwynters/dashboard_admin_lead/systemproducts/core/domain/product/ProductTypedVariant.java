package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

/*
 * Represents a product with types and variants.
 */

public class ProductTypedVariant extends ProductAbstract {

    // Super builder
    // Care instructions are individual to Product type
    // Core Pricing Model is individual to Product type
    // Delta Pricing Models are based on Product Features available
    // Dimensions are individual to Product type
    // Weight is individual to Product type

    // ADDITIONAL FIELD: Set of Features  * Consider Decoupling list/set into another aggregate/entity
    // ADDITIONAL FIELD: Set of Type  * Consider Decoupling list/set into another aggregate/entity
    // Reference list or sets with lifecycle ID

    // * Consider Feature incompatibilities to other features and/or type


}
