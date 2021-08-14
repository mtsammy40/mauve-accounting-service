package com.qloudd.payments.exceptions.accounts;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.ErrorCode;
import com.qloudd.payments.exceptions.GeneralAppException;

public class AccountException extends GeneralAppException {
    private Account account;

    public AccountException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccountException(Account account, ErrorCode errorCode) {
        super(errorCode);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
