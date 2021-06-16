package com.qloudd.payments.model.api;

import java.math.BigDecimal;

public class TransferRequest {
    private String sourceAccountNumber;
    private String destinationAccNumber;
    private BigDecimal amount;

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getDestinationAccNumber() {
        return destinationAccNumber;
    }

    public void setDestinationAccNumber(String destinationAccNumber) {
        this.destinationAccNumber = destinationAccNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
