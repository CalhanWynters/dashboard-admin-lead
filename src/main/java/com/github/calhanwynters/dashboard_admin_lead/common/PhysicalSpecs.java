package com.github.calhanwynters.dashboard_admin_lead.common;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

public record PhysicalSpecs(
        Weight weight,
        Dimensions dimensions,
        CareInstruction careInstructions
) {
    public static final PhysicalSpecs NONE = new PhysicalSpecs(
            Weight.NONE, Dimensions.NONE, CareInstruction.NONE
    );

    public PhysicalSpecs {
        DomainGuard.notNull(weight, "Weight");
        DomainGuard.notNull(dimensions, "Dimensions");
        DomainGuard.notNull(careInstructions, "Care Instructions");
    }

    public boolean isNone() {
        return weight.isNone() && dimensions.isNone() && (careInstructions == null || careInstructions.equals(CareInstruction.NONE));
    }
}
