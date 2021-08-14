package com.qloudd.payments.entity.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qloudd.payments.commons.converters.JsonToMapConverter;
import com.qloudd.payments.model.ProductConfiguration;
import com.qloudd.payments.model.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class ProductConfigurationConverter implements AttributeConverter<ProductConfiguration, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonToMapConverter.class);

    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(ProductConfiguration dbData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(dbData);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not convert map to json string. {}", e.getMessage());
            return null;
        }
    }

    @Override
    public ProductConfiguration convertToEntityAttribute(String attribute) {
        if (attribute == null) {
            return new ProductConfiguration();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(attribute, ProductConfiguration.class);
        } catch (IOException e) {
            LOGGER.error("Convert error while trying to convert string(JSON) to map data structure. {}", e.getMessage());
        }
        return new ProductConfiguration();
    }
}
