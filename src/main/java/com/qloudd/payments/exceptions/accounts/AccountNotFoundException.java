package com.qloudd.payments.exceptions.accounts;

import com.qloudd.payments.enums.StatusCode;

public class AccountNotFoundException extends AccountException {
    public AccountNotFoundException(String accountNumber) {
        super(StatusCode.ACCOUNT_NOT_FOUND);
        this.addDetail("Account Id : [" + accountNumber + "]");
    }
    public AccountNotFoundException(Long id) {
        super(StatusCode.ACCOUNT_NOT_FOUND);
        this.addDetail("Account Id : [" + id.toString() + "]");
    }
}
