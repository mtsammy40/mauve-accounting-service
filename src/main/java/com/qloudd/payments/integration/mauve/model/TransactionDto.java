package com.qloudd.payments.integration.mauve.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qloudd.payments.integration.mauve.enums.TransactionType;
import com.qloudd.payments.integration.mauve.enums.TrxStage;
import com.qloudd.payments.integration.mauve.enums.TrxStatus;

import java.math.BigDecimal;
import java.util.Map;

public class TransactionDto {
    public class IdentifierDto {
        private String msisdn;
        private String accountNumber;
    }

    private IdentifierDto initiator = new IdentifierDto();
    private IdentifierDto recipient = new IdentifierDto();

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

    public IdentifierDto getInitiator() {
        return initiator;
    }

    public void setInitiator(IdentifierDto initiator) {
        this.initiator = initiator;
    }

    public IdentifierDto getRecipient() {
        return recipient;
    }

    public void setRecipient(IdentifierDto recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Map<String, Object> getTrxDetails() {
        return trxDetails;
    }

    public void setTrxDetails(Map<String, Object> trxDetails) {
        this.trxDetails = trxDetails;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TrxStatus getStatus() {
        return status;
    }

    public void setStatus(TrxStatus status) {
        this.status = status;
    }

    public TrxStage getStage() {
        return stage;
    }

    public void setStage(TrxStage stage) {
        this.stage = stage;
    }

    public String getThirdPartyReference() {
        return thirdPartyReference;
    }

    public void setThirdPartyReference(String thirdPartyReference) {
        this.thirdPartyReference = thirdPartyReference;
    }
}
