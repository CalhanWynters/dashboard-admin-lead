package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.type;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.validationchecks.DomainGuard;

public record Type(
        UuId typeId,
        Label compatibilityTag,
        Name typeName,
        Dimensions typeDimensions,
        Weight typeWeight,
        Description typeDescription,
        CareInstruction typeCareInstruction
) {
    /**
     * Compact Constructor must be PUBLIC to match the record.
     * Use this for final validation of all components.
     */
    public Type {
        DomainGuard.notNull(typeId, "Type ID");
        DomainGuard.notNull(compatibilityTag, "Compatibility Tag");
        DomainGuard.notNull(typeName, "Type featuresName");
        DomainGuard.notNull(typeDescription, "Type Description");
        DomainGuard.notNull(typeCareInstruction, "Type Care Instruction");

        DomainGuard.ensure(
                !typeName.value().equalsIgnoreCase(typeDescription.text()),
                "Type description must provide additional context beyond the featuresName.",
                "VAL-022", "SEMANTICS"
        );
        DomainGuard.ensure(
                !typeName.value().equalsIgnoreCase(typeCareInstruction.instructions()),
                "Type care instruction must provide specific maintenance guidance.",
                "VAL-023", "SEMANTICS"
        );

        // Validating dimensions and weight if provided
        if (typeDimensions != null) {
            DomainGuard.notNull(typeDimensions.length(), "Length cannot be null");
            DomainGuard.notNull(typeDimensions.width(), "Width cannot be null");
            DomainGuard.notNull(typeDimensions.height(), "Height cannot be null");
        }

        if (typeWeight != null) {
            DomainGuard.positive(typeWeight.amount(), "Weight amount must be positive");
        }
    }
}
