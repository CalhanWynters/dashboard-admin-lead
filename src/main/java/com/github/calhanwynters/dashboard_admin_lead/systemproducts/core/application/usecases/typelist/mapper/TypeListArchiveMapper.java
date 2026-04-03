package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListArchiveDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

public final class TypeListArchiveMapper {
    private TypeListArchiveMapper() {}

    public static TypeListArchiveCommand toCommand(String uuid, TypeListArchiveDTO dto) {
        DomainGuard.notBlank(uuid, "TypeList UUID Path Variable");
        return new TypeListArchiveCommand(
                new TypeListUuId(UuId.fromString(uuid)),
                dto.toActor()
        );
    }

    public record TypeListArchiveCommand(TypeListUuId typeListUuId, Actor actor) {}
}
