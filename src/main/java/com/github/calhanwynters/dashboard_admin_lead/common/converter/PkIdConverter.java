package com.github.calhanwynters.dashboard_admin_lead.common.converter;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PkIdConverter implements AttributeConverter<PkId, Long> {
    @Override
    public Long convertToDatabaseColumn(PkId attribute) {
        return attribute == null ? null : attribute.value();
    }
    @Override
    public PkId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new PkId(dbData);
    }
}