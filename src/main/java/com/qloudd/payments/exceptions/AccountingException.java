package com.qloudd.payments.exceptions;


import com.qloudd.payments.entity.AccountingEntry;
import com.qloudd.payments.enums.StatusCode;

public class AccountingException extends GeneralAppException {

    AccountingEntry accountingEntry;

    public AccountingException(AccountingEntry accountingEntry, StatusCode statusCode) {
        super(statusCode);
        this.accountingEntry = accountingEntry;
    }

    public AccountingEntry getAccountingEntry() {
        return accountingEntry;
    }
}
