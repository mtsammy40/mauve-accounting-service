package com.qloudd.payments.commons.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qloudd.payments.model.AccountTypeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class JsonToMapConverter
        implements AttributeConverter<AccountTypeConfiguration, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonToMapConverter.class);

    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(AccountTypeConfiguration dbData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(dbData);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not convert map to json string. {}", e.getMessage());
            return null;
        }
    }

    @Override
    public AccountTypeConfiguration convertToEntityAttribute(String attribute) {
        if (attribute == null) {
            return new AccountTypeConfiguration();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(attribute, AccountTypeConfiguration.class);
        } catch (IOException e) {
            LOGGER.error("Convert error while trying to convert string(JSON) to map data structure. {}", e.getMessage());
        }
        return new AccountTypeConfiguration();
    }
}