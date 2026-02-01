package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.*;

public interface ProductDomainWrapper {
    record ProductId(PkId value) {}
    record ProductUuId(UuId value) {}
    record ProductBusinessUuId(UuId value) {}
    record ProductName(Name value) {}
    record ProductCategory(Category value) {}
    record ProductVersion(Version value) {}
    record ProductDescription(Description value) {}
    record ProductStatus(StatusEnums value) {}    // Status
    record ProductWeight(Weight value) {}
    record ProductDimensions(Dimensions value) {}
    record ProductCareInstructions(CareInstruction value) {}
}
