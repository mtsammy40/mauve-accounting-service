package com.qloudd.payments.entity;

import com.qloudd.payments.entity.converters.StatusConverter;
import com.qloudd.payments.enums.Status;
import com.qloudd.payments.proto.AccountCreationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table
@Builder
@AllArgsConstructor
public class Account implements Serializable {
    private Long id;
    @NotBlank()
    private String userId;
    @NotBlank()
    @Size(max = 12, min = 10)
    private String accountNumber;
    @NotNull()
    private AccountType accountType;
    private BigDecimal balance;
    private Status status;

    public Account() {
    }

    public Account(Long accountId) {
        this.id = accountId;
    }

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

    @Enumerated(value = EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account [accountNumber=" + accountNumber + ", accountType=" + accountType + ", balance=" + balance
                + ", id=" + id + ", userId=" + userId + "]";
    }

    public static Account from(AccountCreationRequest request) {
        Account account = new Account();
        account.setAccountType(new AccountType((long) request.getAccountTypeId(), null, null, null));
        account.setUserId(String.valueOf(request.getUserId()));
        return account;
    }

}
