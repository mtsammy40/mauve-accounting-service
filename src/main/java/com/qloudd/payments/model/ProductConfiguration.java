package com.qloudd.payments.model;

import com.qloudd.payments.model.command.Command;

import java.math.BigDecimal;
import java.util.List;

public class ProductConfiguration {
    private List<ChargeConfiguration> charges;
    private BigDecimal maxTransactionAmount;
    private BigDecimal minTransactionAmount;
    private List<Command> commands;

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

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }
}
