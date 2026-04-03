package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListEditSetTypeDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

public final class TypeListEditSetTypeMapper {
    private TypeListEditSetTypeMapper() {}

    public static TypeListEditSetTypeCommand toCommand(String typeListUuid, TypeListEditSetTypeDTO dto) {
        DomainGuard.notBlank(typeListUuid, "TypeList UUID Path Variable");
        return new TypeListEditSetTypeCommand(
                new TypeListUuId(UuId.fromString(typeListUuid)),
                dto.toTypesUuId(),
                dto.toActor()
        );
    }

    public record TypeListEditSetTypeCommand(TypeListUuId typeListUuId, TypesUuId typesUuId, Actor actor) {}
}
