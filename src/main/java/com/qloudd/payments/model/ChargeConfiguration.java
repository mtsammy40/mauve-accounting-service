package com.qloudd.payments.model;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class ChargeConfiguration {
    private String name;
    private String destinationAccount;
    private ChargeType chargeType;
    private List<RangeConfigs> range;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    public List<RangeConfigs> getRange() {
        return range;
    }

    public void setRange(List<RangeConfigs> range) {
        this.range = range;
    }
}
