package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.CareInstruction;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Dimensions;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Weight;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money.PurchasePricing;
import java.util.Objects;

/*
 * Represents products with no variations or types.
 * Just has a price model.
 */
public class ProductBasic extends ProductAbstract {

    private final PurchasePricing pricingModel;
    private final CareInstruction typeCareInstruction;
    private final Dimensions productDimensions;
    private final Weight productWeight;

    private ProductBasic(Builder builder) {
        super(builder);
        this.pricingModel = Objects.requireNonNull(builder.pricingModel, "Pricing model cannot be null");
        this.typeCareInstruction = Objects.requireNonNull(builder.typeCareInstruction, "Care instructions cannot be null");
        this.productDimensions = Objects.requireNonNull(builder.productDimensions, "Dimensions cannot be null");
        this.productWeight = Objects.requireNonNull(builder.productWeight, "Weight cannot be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public PurchasePricing getPricingModel() { return pricingModel; }
    public CareInstruction getTypeCareInstruction() { return typeCareInstruction; }
    public Dimensions getProductDimensions() { return productDimensions; }
    public Weight getProductWeight() { return productWeight; }

    public static class Builder extends ProductAbstract.Builder<ProductBasic, Builder> {
        private PurchasePricing pricingModel;
        private CareInstruction typeCareInstruction;
        private Dimensions productDimensions;
        private Weight productWeight;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder pricingModel(PurchasePricing pricingModel) {
            this.pricingModel = pricingModel;
            return self();
        }

        public Builder typeCareInstruction(CareInstruction typeCareInstruction) {
            this.typeCareInstruction = typeCareInstruction;
            return self();
        }

        public Builder productDimensions(Dimensions productDimensions) {
            this.productDimensions = productDimensions;
            return self();
        }

        public Builder productWeight(Weight productWeight) {
            this.productWeight = productWeight;
            return self();
        }

        @Override
        public ProductBasic build() {
            return new ProductBasic(this);
        }
    }
}
