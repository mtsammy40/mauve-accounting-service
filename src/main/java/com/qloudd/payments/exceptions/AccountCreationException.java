package com.qloudd.payments.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Account;

public class AccountCreationException extends Exception {
    public enum AccountCreationExceptionType {
        DUPLICATE_ACCOUNT_NUMBER("Account Number __ACCOUNT_NO__ already exists"),
        VALIDATION_FAILED("Account details are invalid"),
        UNEXPECTED("Unexpected error creating account __ACCOUNT_NO__");

        private final String messageTemplate;
        private AccountCreationExceptionType(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public String buildMessage(Account account) {
            String message = "";
            Map<String, String> values = Common.mapPlaceholdersToValues(account);
            for (String key : values.keySet()) {
                message = messageTemplate.replaceAll(key, values.get(key));
            }
            return message;
        }
    }
    
    private Account account;
    private AccountCreationExceptionType type;
    public AccountCreationException(Account account, AccountCreationExceptionType type) {
        super(type.buildMessage(account));
        this.account = account;
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public AccountCreationExceptionType getType() {
        return type;
    }

}
