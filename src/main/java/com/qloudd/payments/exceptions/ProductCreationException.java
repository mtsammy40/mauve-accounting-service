package com.qloudd.payments.exceptions;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductCreationException extends Exception {
    public enum Type {
        VALIDATION("Validation Failed."),
        UNEXPECTED("Unexpected error creating product __NAME__");

        private final String messageTemplate;
        Type(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public String buildMessage(Product product) {
            String message = "";
            Map<String, String> values = Common.mapPlaceholdersToValues(product);
            for (String key : values.keySet()) {
                message = messageTemplate.replaceAll(key, values.get(key));
            }
            return message;
        }
    }
    private Product product;
    private ProductCreationException.Type type;
    private List<String> errors = new ArrayList<>();

    public ProductCreationException(Product product, Type type) {
        super(type.buildMessage(product));
        this.errors.add(this.getMessage());
        this.product = product;
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public ProductCreationException withErrors(List<String> errorList) {
        errorList = errorList != null ? errorList : Collections.emptyList();
        this.errors.addAll(errorList);
        return this;
    }
}
