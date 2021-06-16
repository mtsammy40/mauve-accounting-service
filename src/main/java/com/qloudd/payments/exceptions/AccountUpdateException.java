package com.qloudd.payments.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Account;

public class AccountUpdateException extends Exception {
    public enum AccountUpdateExceptionType {
        ACCOUNT_NOT_FOUND("Account Number {accountNumber} does not exist"),
        UNEXPECTED("Unexpected error while updating account {accountNumber}");

        private final String messageTemplate;
        private AccountUpdateExceptionType(String messageTemplate) {
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
    private AccountUpdateExceptionType type;
    public AccountUpdateException(Account account, AccountUpdateExceptionType type) {
        super(type.buildMessage(account));
        this.account = account;
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public AccountUpdateExceptionType getType() {
        return type;
    }

}
