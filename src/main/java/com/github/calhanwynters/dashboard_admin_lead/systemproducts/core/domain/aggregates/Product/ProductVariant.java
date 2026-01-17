package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.Product;

/*
 * Represents a Product with no Type but has variants.
 * Variants are different from Types in that they represent price deltas rather than the core price.
 * Variants are composed of features to chose from and have minimal effect on overall price.
 *
 * Status of a permutation of variant is handled through an inventory service outside of this aggregate.
 *  - Reason for this design: I did not want to introduce nested entities product entity > variant entity > feature value object.
 *  - I prefer to keep product abstract with product variant concrete class > features
 *  - That way I can reduce time complexity of querying/storing the snapshot of aggregate
 */

public class ProductVariant extends ProductAbstract {
}
