package com.qloudd.payments.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "AccountingEntry")
public class AccountingEntry {
    public enum Type {
        DEBIT, CREDIT
    }
    public enum Status {
        PENDING, PROCESSING, SUCCESS, FAILED
    }

    private Long id;
    private Type type;
    private BigDecimal amount;
    private Date createdAt;
    private Transaction transaction;
    private Account account;
    private Status status;

    public AccountingEntry(Type type, BigDecimal amount, Transaction transaction, Account account, Status status) {
        this.type = type;
        this.amount = amount;
        this.transaction = transaction;
        this.account = account;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JoinColumn(referencedColumnName = "id")
    @ManyToOne
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @JoinColumn(referencedColumnName = "id")
    @ManyToOne
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account source) {
        this.account = source;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
