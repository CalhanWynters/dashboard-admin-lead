package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type.TypeCollection;

import java.util.Objects;

/*
 * Represents a product with a single "Type".
 * A type is a core attribute of the product that determines the core price.
 * For example in a ring's price would reflect the type of metal it is made from.
 *
 * Also can be used for business clients that want to have Types and/or Variants but manually priced.
 */

public class ProductTyped extends ProductAbstract {

    private final TypeCollection types;

    private ProductTyped(Builder builder) {
        super(builder);
        this.types = Objects.requireNonNull(builder.types, "TypeCollection cannot be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public TypeCollection getTypes() {
        return types;
    }

    public static class Builder extends ProductAbstract.Builder<ProductTyped, Builder> {
        private TypeCollection types;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder types(TypeCollection types) {
            this.types = types;
            return self();
        }

        @Override
        public ProductTyped build() {
            return new ProductTyped(this);
        }
    }
}