package com.qloudd.payments.exceptions;

public class AccountNotFoundException extends Exception {
    private String accountNumber;
    private Long accountId;

    public AccountNotFoundException(String accountNumber) {
        super("Account with account number [ " + accountNumber + " ] does not exist");
        this.accountNumber = accountNumber;
    }

    public AccountNotFoundException(Long accountId) {
        super("Account with account number [ " + accountId + " ] does not exist");
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Long getAccountId() {
        return accountId;
    }
}
