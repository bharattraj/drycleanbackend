
package com.shop.service_shop_management.entity;

import com.shop.service_shop_management.enums.ProcessStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ProcessStatusConverter
        implements AttributeConverter<ProcessStatus, Short> {

    @Override
    public Short convertToDatabaseColumn(ProcessStatus status) {

        if (status == null) {
            return null;
        }

        return status.getCode();
    }

    @Override
    public ProcessStatus convertToEntityAttribute(Short code) {

        if (code == null) {
            return null;
        }

        return ProcessStatus.fromCode(code);
    }
}
