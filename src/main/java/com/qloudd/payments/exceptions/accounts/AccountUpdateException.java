package com.qloudd.payments.exceptions.accounts;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.StatusCode;

public class AccountUpdateException extends AccountException {

    public AccountUpdateException(Account account, StatusCode statusCode) {
        super(account, statusCode);
    }
}
