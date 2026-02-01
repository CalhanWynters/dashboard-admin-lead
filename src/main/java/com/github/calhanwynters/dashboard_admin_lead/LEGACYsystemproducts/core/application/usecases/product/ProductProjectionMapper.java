package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.product;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.product.ProductAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductProjectionMapper {

    public static ProductProjectionDTO toDTO(ProductAggregateRoot product) {
        if (product == null) return null;

        return new ProductProjectionDTO(
                product.getProductUuId().value(),
                product.getBusinessId().value(),
                product.getProductName().value(),
                product.getProductCategory().value(),
                product.getProductDesc().text(),
                product.getStatus().name(),
                product.getVersion().value(),
                product.getAudit().lastModified().value().toString(),
                product.getGalleryColId().value(),
                product.getTypeColId().map(UuId::value).orElse(null),
                product.getVariantColId().map(UuId::value).orElse(null),
                mapSpecs(product),
                mapRules(product)
        );
    }

    private static ProductProjectionDTO.PhysicalSpecsDTO mapSpecs(ProductAggregateRoot product) {
        // Safe Extraction: Resolve XOR logic by checking for presence or NONE sentinel
        Dimensions dim = product.getProductDimensions().orElse(null);
        Weight weight = product.getProductWeight().orElse(null);
        CareInstruction care = product.getProductCareInstruction().orElse(null);

        return new ProductProjectionDTO.PhysicalSpecsDTO(
                (dim != null && dim.length() != null) ? dim.length().toPlainString() : "0.00",
                (dim != null && dim.width() != null) ? dim.width().toPlainString() : "0.00",
                (dim != null && dim.height() != null) ? dim.height().toPlainString() : "0.00",
                (dim != null && dim.sizeUnit() != null) ? dim.sizeUnit().getCode() : "",
                (weight != null && weight.amount() != null) ? weight.amount().toPlainString() : "0.00",
                (weight != null && weight.weightUnit() != null) ? weight.weightUnit().name() : "",
                (care != null && care.instructions() != null) ? care.instructions() : ""
        );
    }

    private static Set<ProductProjectionDTO.IncompatibilityRuleDTO> mapRules(ProductAggregateRoot product) {
        return Stream.concat(product.getInternalRules().stream(), product.getContextualRules().stream())
                .map(rule -> new ProductProjectionDTO.IncompatibilityRuleDTO(
                        rule.triggerUuId() != null ? rule.triggerUuId().value() : null,
                        rule.triggerTag() != null ? rule.triggerTag().value() : null,
                        rule.forbiddenFeatureUuId().value()
                ))
                .collect(Collectors.toSet());
    }
}
