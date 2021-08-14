package com.qloudd.payments.exceptions;


import com.qloudd.payments.entity.AccountingEntry;
import com.qloudd.payments.enums.ErrorCode;

public class AccountingException extends GeneralAppException {

    AccountingEntry accountingEntry;

    public AccountingException(AccountingEntry accountingEntry, ErrorCode errorCode) {
        super(errorCode);
        this.accountingEntry = accountingEntry;
    }

    public AccountingEntry getAccountingEntry() {
        return accountingEntry;
    }
}
