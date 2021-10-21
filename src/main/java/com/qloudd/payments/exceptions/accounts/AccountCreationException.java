package com.qloudd.payments.exceptions.accounts;

import java.util.*;

import com.qloudd.payments.entity.Account;

import com.qloudd.payments.enums.StatusCode;

public class AccountCreationException extends AccountException {

    public AccountCreationException(Account account, StatusCode statusCode) {
        super(account, statusCode);
    }
    public AccountCreationException(Account account, List<String> errors, StatusCode statusCode) {
        super(account, errors, statusCode);
    }
}
