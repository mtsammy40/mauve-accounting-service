package com.qloudd.payments.exceptions.accounts;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.ErrorCode;

public class AccountNotFoundException extends AccountException {
    public AccountNotFoundException(String accountNumber) {
        super(ErrorCode.ACCOUNT_NOT_FOUND);
        this.addDetail("Account Id : [" + accountNumber + "]");
    }
    public AccountNotFoundException(Long id) {
        super(ErrorCode.ACCOUNT_NOT_FOUND);
        this.addDetail("Account Id : [" + id.toString() + "]");
    }
}
