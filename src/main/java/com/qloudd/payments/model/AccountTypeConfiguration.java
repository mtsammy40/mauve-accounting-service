package com.qloudd.payments.model;

import java.math.BigDecimal;
import java.util.List;

public class AccountTypeConfiguration {
    private List<ChargeConfiguration> charges;
    private BigDecimal maxDebitLimit;
    private BigDecimal minDebitLimit;
    private String accountPrefix;

    public List<ChargeConfiguration> getCharges() {
        return charges;
    }

    public void setCharges(List<ChargeConfiguration> charges) {
        this.charges = charges;
    }

    public BigDecimal getMaxDebitLimit() {
        return maxDebitLimit;
    }

    public void setMaxDebitLimit(BigDecimal maxDebitLimit) {
        this.maxDebitLimit = maxDebitLimit;
    }

    public BigDecimal getMinDebitLimit() {
        return minDebitLimit;
    }

    public void setMinDebitLimit(BigDecimal minDebitLimit) {
        this.minDebitLimit = minDebitLimit;
    }
}
