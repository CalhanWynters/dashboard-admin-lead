package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

/*
 * Represents a product with a single "Type".
 * A type is a core attribute of the product that determines the core price.
 * For example in a ring's price would reflect the type of metal it is made from.
 *
 * Also can be used for business clients that want to have Types and/or Variants but manually priced.
 */


public class ProductTyped extends ProductAbstract {

    // Super builder

    // ADDITIONAL FIELD: Set of Type  * Consider Decoupling list/set into another aggregate/entity
    // Reference list or sets with lifecycle ID

}
