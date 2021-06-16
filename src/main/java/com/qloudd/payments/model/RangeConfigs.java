package com.qloudd.payments.model;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class RangeConfigs {
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal value;

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
