package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;

public class TypeListBehavior {

    private final TypeListAggregate typeList;

    public TypeListBehavior(TypeListAggregate typeList) {
        DomainGuard.notNull(typeList, "TypeList instance");
        this.typeList = typeList;
    }

    public TypeListAggregate attachType(TypesUuId typeUuId, Actor actor) {
        // Orchestrate the call to the aggregate
        typeList.attachType(typeUuId, actor);
        return typeList;
    }

    public TypeListAggregate detachType(TypesUuId typeUuId, Actor actor) {
        typeList.detachType(typeUuId, actor);
        return typeList;
    }
}
