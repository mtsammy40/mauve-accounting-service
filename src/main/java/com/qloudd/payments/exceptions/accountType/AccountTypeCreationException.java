package com.qloudd.payments.exceptions.accountType;

import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.enums.StatusCode;
import com.qloudd.payments.exceptions.GeneralAppException;

import java.util.List;

public class AccountTypeCreationException extends GeneralAppException {
    private AccountType accountType;
    public AccountTypeCreationException(AccountType accountType, StatusCode statusCode) {
        super(statusCode);
        this.accountType = accountType;
    }
    public AccountTypeCreationException(AccountType accountType, StatusCode statusCode, List<String> errors) {
        super(statusCode, errors);
        this.accountType = accountType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

}
