package com.qloudd.payments.exceptions.accounts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.ErrorCode;
import com.qloudd.payments.exceptions.GeneralAppException;

public class AccountUpdateException extends AccountException {

    public AccountUpdateException(Account account, ErrorCode errorCode) {
        super(account, errorCode);
    }
}
