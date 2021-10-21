package com.qloudd.payments.integration.mauve.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IdentifierDto {
    private String msisdn;
    private String accountNumber;
}
