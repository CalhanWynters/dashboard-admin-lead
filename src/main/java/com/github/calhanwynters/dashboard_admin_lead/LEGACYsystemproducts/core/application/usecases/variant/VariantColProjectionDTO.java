package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.variant;

import java.util.List;

public class VariantColProjectionDTO {
    public int primaryKey;
    public String variantColId;
    public String businessId;
    public List<String> variantIds; // Mapped from Set<UuId>
}
