package com.qloudd.payments.commons;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.qloudd.payments.entity.Account;

import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.entity.AccountingEntry;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
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
    public static Map<String, String> mapPlaceholdersToValues(Product product) {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("__NAME__", product.getName());
        valueMap.put("__STATUS__", product.getStatus().getStatus());
        valueMap.put("__CONFIGURATION__", new Gson().toJson(product.getConfiguration()));
        return valueMap;
    }
    public static Map<String, String> mapPlaceholdersToValues(Transaction transaction) {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("__TPR__", transaction.getThirdPartyReference());
        valueMap.put("__STATUS__", new Gson().toJson(transaction.getStatus()));
        valueMap.put("__AMOUNT__", new Gson().toJson(transaction.getAmount()));
        valueMap.put("__PRODUCT__", transaction.getProduct().getId().toString());
        valueMap.put("__SRC_ACCOUNT__", transaction.getSourceAccount().getId().toString());
        valueMap.put("__DEST_ACCOUNT__", transaction.getDestAccount().getId().toString());
        return valueMap;
    }
    public static Map<String, String> mapPlaceholdersToValues(AccountingEntry accountingEntry) {
        Map<String, String> valueMap = new HashMap<>();
        if(accountingEntry != null) {
            valueMap.put("__STATUS__", accountingEntry.getStatus().toString());
            valueMap.put("__AMOUNT__", new Gson().toJson(accountingEntry.getAmount()));
        }
        return valueMap;
    }
}
