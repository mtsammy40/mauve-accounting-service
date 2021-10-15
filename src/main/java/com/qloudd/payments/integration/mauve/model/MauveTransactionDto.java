package com.qloudd.payments.integration.mauve.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qloudd.payments.integration.mauve.enums.TransactionType;
import com.qloudd.payments.integration.mauve.enums.TrxStage;
import com.qloudd.payments.integration.mauve.enums.TrxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MauveTransactionDto {
    private IdentifierDto initiator;
    private IdentifierDto recipient;

    private BigDecimal amount;
    @JsonProperty("trx_details")
    private Map<String, Object> trxDetails;
    @JsonProperty("trx_type")
    private TransactionType transactionType;
    private String description;
    private TrxStatus status;
    private TrxStage stage;
    @JsonProperty("third_party_reference")
    private String thirdPartyReference;

}
