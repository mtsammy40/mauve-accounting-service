package com.qloudd.payments.exceptions.accounts;

import java.util.*;

import com.qloudd.payments.entity.Account;

import com.qloudd.payments.enums.ErrorCode;

public class AccountCreationException extends AccountException {

    public AccountCreationException(Account account, ErrorCode errorCode) {
        super(account, errorCode);
    }
}
