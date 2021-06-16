package com.qloudd.payments.commons;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.qloudd.payments.entity.Account;

import com.qloudd.payments.entity.AccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Common {
    private Logger LOG = LoggerFactory.getLogger(Common.class);

    public static Map<String, String> mapPlaceholdersToValues(Account account) {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("__ACCOUNT_NO__", account.getAccountNumber());
        valueMap.put("__USER_ID__", account.getUserId());
        valueMap.put("__ACCOUNT_TYPE__", account.getAccountType().getId().toString());
        valueMap.put("__BALANCE__", account.getBalance() != null ? account.getBalance().toString() : BigDecimal.ZERO.toPlainString());
        return valueMap;
    }
    public static Map<String, String> mapPlaceholdersToValues(AccountType accountType) {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("__NAME__", accountType.getName());
        valueMap.put("__STATUS__", accountType.getStatus());
        valueMap.put("__CONFIGURATIONS__", new Gson().toJson(accountType.getConfigurations()));
        return valueMap;
    }
}
