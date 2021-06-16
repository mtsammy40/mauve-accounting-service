package com.qloudd.payments.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Transaction")
public class Transaction {

    public enum Types {
        DEBIT, CREDIT
    }

    public enum Status {
        INITIATED, COMPLETED_OK, COMPLETED_FAILED, PROCESSING, UNKNOWN
    }

    private Long id;
    private Account sourceAccount;
    private Account destAccount;
    private BigDecimal amount;
    private Types transactionType;
    private Status status;
    private Date initiated;
    private Date completed;
    private String misc;
    private String thirdPartyReference;

    public Transaction() {
    }

    public Transaction(BigDecimal amount, Account sourceAccount, Account destAccount) {
        this.sourceAccount = sourceAccount;
        this.destAccount = destAccount;
        this.amount = amount;
        this.transactionType = Types.DEBIT;
        this.status = Status.PROCESSING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Enumerated(EnumType.STRING)
    public Types getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Types transactionType) {
        this.transactionType = transactionType;
    }

    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getInitiated() {
        return initiated;
    }

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    public void setInitiated(Date initiated) {
        this.initiated = initiated;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date compledted) {
        this.completed = compledted;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getThirdPartyReference() {
        return thirdPartyReference;
    }

    public void setThirdPartyReference(String thirdPartyReference) {
        this.thirdPartyReference = thirdPartyReference;
    }

    @JoinColumn(referencedColumnName = "id")
    @ManyToOne
    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    @JoinColumn(referencedColumnName = "id")
    @ManyToOne
    public Account getDestAccount() {
        return destAccount;
    }

    public void setDestAccount(Account destAccount) {
        this.destAccount = destAccount;
    }
    
    public Transaction complete() {
        this.status = Status.COMPLETED_OK;
        this.completed = new Date(System.currentTimeMillis());
        return this;
    }
}
