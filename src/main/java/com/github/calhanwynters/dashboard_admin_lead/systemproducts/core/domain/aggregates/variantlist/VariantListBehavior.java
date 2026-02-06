package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import java.util.Set;

public final class VariantListBehavior {

    private static final int MAX_VARIANTS = 100;

    private VariantListBehavior() {}

    public static void ensureActive(boolean deleted) {
        if (deleted) {
            throw new IllegalStateException("Operation failed: VariantList is deleted.");
        }
    }

    public static void ensureCanAttach(Set<VariantsUuId> current, VariantsUuId next) {
        DomainGuard.notNull(next, "Variant UUID");
        if (current.size() >= MAX_VARIANTS) {
            throw new IllegalStateException("VariantList limit reached (%d)".formatted(MAX_VARIANTS));
        }
        if (current.contains(next)) {
            throw new IllegalArgumentException("Variant already attached to this list.");
        }
    }

    public static void ensureCanDetach(Set<VariantsUuId> current, VariantsUuId target) {
        DomainGuard.notNull(target, "Variant UUID");
        if (!current.contains(target)) {
            throw new IllegalArgumentException("Variant not found in this list.");
        }
    }

    public static void ensureCanReorder(Set<VariantsUuId> current) {
        if (current.size() < 2) {
            throw new IllegalStateException("Cannot reorder a list with fewer than 2 variants.");
        }
    }
}
