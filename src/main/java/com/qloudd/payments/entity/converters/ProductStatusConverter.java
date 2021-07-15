package com.qloudd.payments.entity.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qloudd.payments.commons.converters.JsonToMapConverter;
import com.qloudd.payments.enums.ProductStatus;
import com.qloudd.payments.model.ProductConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class ProductStatusConverter implements AttributeConverter<ProductStatus, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonToMapConverter.class);

    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(ProductStatus status) {
            return status.getStatus();
    }

    @Override
    public ProductStatus convertToEntityAttribute(String statusString) {
        return ProductStatus.resolve(statusString);
    }
}
