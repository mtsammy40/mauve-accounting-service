package com.qloudd.payments.exceptions;

import java.util.Map;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.AccountingEntry;

public class AccountingException extends Exception {

    public enum Type {
        INVALID_ACCOUNTING_ENTRY("Provided Accounting entry is invalid"),
        UNEXPECTED("Unexpected error creating account __ACCOUNT_NO__");

        private final String messageTemplate;
        private Type(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public String buildMessage(AccountingEntry accountingEntry) {
            String message = "";
            Map<String, String> values = Common.mapPlaceholdersToValues(accountingEntry);
            for (String key : values.keySet()) {
                message = messageTemplate.replaceAll(key, values.get(key));
            }
            return message;
        }
    }

    AccountingEntry accountingEntry;
    public Type type;

    public AccountingException(AccountingEntry accountingEntry, Type type) {
        super(type.buildMessage(accountingEntry));
        this.accountingEntry = accountingEntry;
        this.type = type;
    }

    
}
