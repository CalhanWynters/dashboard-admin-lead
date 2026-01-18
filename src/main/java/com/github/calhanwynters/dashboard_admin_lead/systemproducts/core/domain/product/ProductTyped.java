package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

/*
 * Represents a product with a single "Type".
 * A type is a core attribute of the product that determines the core price.
 * For example in a ring's price would reflect the type of metal it is made from.
 *
 * Also can be used for business clients that want to have Types and/or Variants but manually priced.
 */


public class ProductTyped extends ProductAbstract {

    // Super builder
    // Care instructions are individual to Product type
    // Pricing Model is individual to Product type
    // Dimensions are individual to Product type
    // Weight is individual to Product type

    // ADDITIONAL FIELD: Set of Type  * Consider Decoupling list/set into another aggregate/entity
    // Reference list or sets with lifecycle ID

}
