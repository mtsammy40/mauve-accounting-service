package com.qloudd.payments.exceptions.accounts;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.StatusCode;

public class AccountTrashException extends AccountException {

    public AccountTrashException(Account account, StatusCode statusCode) {
        super(account, statusCode);
    }
}