package com.github.calhanwynters.dashboard_admin_lead.common.converter;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // Automatically applies to all UuId fields in the project
public class UuIdConverter implements AttributeConverter<UuId, String> {

    @Override
    public String convertToDatabaseColumn(UuId attribute) {
        return (attribute == null) ? null : attribute.value();
    }

    @Override
    public UuId convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : new UuId(dbData);
    }
}
