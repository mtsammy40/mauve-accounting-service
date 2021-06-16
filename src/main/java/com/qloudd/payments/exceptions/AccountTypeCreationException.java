package com.qloudd.payments.exceptions;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.AccountType;

import java.util.List;
import java.util.Map;

public class AccountTypeCreationException extends Exception {
    public enum Type {
        VALIDATION("Validation Failed: name: [ __NAME__ ]"),
        UNEXPECTED("Unexpected error creating account type __NAME__");

        private final String messageTemplate;
        Type(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public String buildMessage(AccountType accountType) {
            String message = "";
            Map<String, String> values = Common.mapPlaceholdersToValues(accountType);
            for (String key : values.keySet()) {
                message = messageTemplate.replaceAll(key, values.get(key));
            }
            return message;
        }
    }
    private AccountType accountType;
    private Type type;
    private List<String> errors;
    public AccountTypeCreationException(AccountType accountType, Type type) {
        super(type.buildMessage(accountType));
        this.accountType = accountType;
        this.type = type;
    }
    public AccountTypeCreationException(AccountType accountType, Type type, List<String> errors) {
        super(type.buildMessage(accountType));
        this.accountType = accountType;
        this.type = type;
        this.errors = errors;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
