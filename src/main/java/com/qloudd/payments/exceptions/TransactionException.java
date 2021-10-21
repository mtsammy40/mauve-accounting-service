package com.qloudd.payments.exceptions;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Transaction;

import java.util.List;
import java.util.Map;

public class TransactionException extends Exception {
    public enum Type {
        VALIDATION("Validation Failed."),
        ACCOUNT_NOT_FOUND("Account with id __SRC_ACCOUNT__ not found."),
        PRODUCT_NOT_FOUND("Account with id __DEST_ACCOUNT__ not found."),
        MIN_DEBIT_UNSATISFIED("Amount is less than the allowed min debit for source account id __SRC_ACCOUNT__"),
        DIRTY_DATA("System data is corrupted"),
        CHARGE_BAND_NOT_FOUND("Charges missing for amount [ __AMOUNT__ ] for charge : [ __PRODUCT__ ]"),
        INVALID_CHARGE_CONFIG_TYPE("Charge config invalid : [ __CHARGE_CONFIG_TYPE__ ]"),
        INSUFFICIENT_FUNDS("Insufficient funds to complete transaction"),
        CONFIGURATION_NOT_FOUND("Configuration for the transaction are not found"),
        UNEXPECTED("Unexpected error processing transaction __NAME__");

        private final String messageTemplate;
        Type(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public String buildMessage(Transaction transaction) {
            String message = "";
            Map<String, String> values = Common.mapPlaceholdersToValues(transaction);
            for (String key : values.keySet()) {
                message = messageTemplate.replaceAll(key, values.get(key));
            }
            return message;
        }
    }

    private Transaction transaction;
    private Type type;
    private List<String> errors;

    public TransactionException(Transaction transaction, Type type) {
        super(type.buildMessage(transaction));
        this.transaction = transaction;
        this.type = type;
    }

    public TransactionException(Type type) {
        super(type.messageTemplate);
        this.type = type;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
}
