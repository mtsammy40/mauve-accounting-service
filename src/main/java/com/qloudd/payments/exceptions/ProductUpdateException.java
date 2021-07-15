package com.qloudd.payments.exceptions;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.Product;

import java.util.Map;

public class ProductUpdateException extends Exception {
    public enum Type {
        ACCOUNT_NOT_FOUND("Product - Name : [ __NAME__ ] does not exist"),
        VALIDATION("Validation failed for Product - Name : [ __NAME__ ]"),
        UNEXPECTED("Unexpected error while updating product - name : [ __NAME__ ]");

        private final String messageTemplate;
        private Type(String messageTemplate) {
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
    private Type type;
    public ProductUpdateException(Product product, Type type) {
        super(type.buildMessage(product));
        this.product = product;
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public Type getType() {
        return type;
    }

}
