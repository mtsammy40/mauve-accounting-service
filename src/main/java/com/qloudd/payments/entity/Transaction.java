package com.qloudd.payments.entity;

import com.qloudd.payments.enums.CommandCode;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.model.api.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Entity
@Table(name = "Transaction")
@AllArgsConstructor
@Builder
public class Transaction {

    public enum Status {
        NEW, INITIATED, COMPLETED_OK, COMPLETED_FAILED, PROCESSING, UNKNOWN
    }

    private Long id;
    private Account sourceAccount;
    private Account destAccount;
    private BigDecimal amount;
    private Product product;
    private Status status;
    private LocalDateTime initiated;
    private LocalDateTime completed;
    private String misc;
    private String thirdPartyReference;

    @Transient
    private BigDecimal totalAmount;

    public Transaction() {
    }

    public Transaction(BigDecimal amount, Account sourceAccount, Account destAccount, Product product) {
        this.sourceAccount = sourceAccount;
        this.destAccount = destAccount;
        this.amount = amount;
        this.status = Status.PROCESSING;
        this.product = product;
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

    @ManyToOne()
    @JoinColumn(name = "product")
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product transactionType) {
        this.product = transactionType;
    }

    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getInitiated() {
        return initiated;
    }

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    public void setInitiated(LocalDateTime initiated) {
        this.initiated = initiated;
    }

    public LocalDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(LocalDateTime compledted) {
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
        this.completed = LocalDateTime.now();
        return this;
    }

    public Transaction fail() {
        this.status = Status.COMPLETED_FAILED;
        this.completed = LocalDateTime.now();
        return this;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void validate() {
       // todo implement
    }

}
