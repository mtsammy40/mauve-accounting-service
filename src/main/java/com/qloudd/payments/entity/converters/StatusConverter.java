package com.qloudd.payments.entity.converters;

import com.qloudd.payments.commons.converters.JsonToMapConverter;
import com.qloudd.payments.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;

public class StatusConverter implements AttributeConverter<Status, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonToMapConverter.class);

    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(Status status) {
            return status.getStatus();
    }

    @Override
    public Status convertToEntityAttribute(String statusString) {
        return Status.resolve(statusString);
    }
}
