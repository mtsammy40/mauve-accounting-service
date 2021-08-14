package com.qloudd.payments.exceptions.accounts;

import java.util.Map;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.ErrorCode;
import com.qloudd.payments.exceptions.GeneralAppException;

public class AccountTrashException extends AccountException {

    public AccountTrashException(Account account, ErrorCode errorCode) {
        super(account, errorCode);
    }
}