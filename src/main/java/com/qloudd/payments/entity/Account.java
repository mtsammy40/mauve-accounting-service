package com.qloudd.payments.entity;

import com.qloudd.payments.proto.AccountCreationRequest;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table
public class Account {
    private Long id;
    @NotBlank()
    private String userId;
    @NotBlank()
    @Size(max = 12, min = 10)
    private String accountNumber;
    @NotNull()
    private AccountType accountType;
    private BigDecimal balance;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account [accountNumber=" + accountNumber + ", accountType=" + accountType + ", balance=" + balance
                + ", id=" + id + ", userId=" + userId + "]";
    }

    public static Account from(AccountCreationRequest request) {
        Account account = new Account();
        account.setAccountType(new AccountType((long) request.getAccountTypeId()));
        account.setUserId(String.valueOf(request.getUserId()));
        return account;
    }
    
}
