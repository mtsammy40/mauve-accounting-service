package com.qloudd.payments.exceptions.accounts;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.StatusCode;
import com.qloudd.payments.exceptions.GeneralAppException;

import java.util.List;

public class AccountException extends GeneralAppException {
    private Account account;

    public AccountException(StatusCode statusCode) {
        super(statusCode);
    }

    public AccountException(Account account, StatusCode statusCode) {
        super(statusCode);
    }

    public AccountException(Account account, List<String> errors, StatusCode statusCode) {
        super(statusCode, errors);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
