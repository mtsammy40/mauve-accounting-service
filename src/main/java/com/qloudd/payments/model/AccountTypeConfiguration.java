package com.qloudd.payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountTypeConfiguration {
    private List<ChargeConfiguration> charges;
    private BigDecimal maxDebitLimit;
    private BigDecimal minDebitLimit;
    private String accountPrefix;
}
