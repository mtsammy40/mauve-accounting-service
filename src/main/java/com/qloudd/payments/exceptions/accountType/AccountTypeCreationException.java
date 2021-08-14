package com.qloudd.payments.exceptions.accountType;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.enums.ErrorCode;
import com.qloudd.payments.exceptions.GeneralAppException;

import java.util.List;
import java.util.Map;

public class AccountTypeCreationException extends GeneralAppException {
    private AccountType accountType;
    public AccountTypeCreationException(AccountType accountType, ErrorCode errorCode) {
        super(errorCode);
        this.accountType = accountType;
    }
    public AccountTypeCreationException(AccountType accountType, ErrorCode errorCode, List<String> errors) {
        super(errorCode, errors);
        this.accountType = accountType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

}
