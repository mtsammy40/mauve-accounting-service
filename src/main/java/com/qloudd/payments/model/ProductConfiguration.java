package com.qloudd.payments.model;

import java.math.BigDecimal;
import java.util.List;

public class ProductConfiguration {
    private List<ChargeConfiguration> charges;
    private BigDecimal maxTransactionAmount;
    private BigDecimal minTransactionAmount;

    public List<ChargeConfiguration> getCharges() {
        return charges;
    }

    public void setCharges(List<ChargeConfiguration> charges) {
        this.charges = charges;
    }

    public BigDecimal getMaxTransactionAmount() {
        return maxTransactionAmount;
    }

    public void setMaxTransactionAmount(BigDecimal maxTransactionAmount) {
        this.maxTransactionAmount = maxTransactionAmount;
    }

    public BigDecimal getMinTransactionAmount() {
        return minTransactionAmount;
    }

    public void setMinTransactionAmount(BigDecimal minTransactionAmount) {
        this.minTransactionAmount = minTransactionAmount;
    }
}
