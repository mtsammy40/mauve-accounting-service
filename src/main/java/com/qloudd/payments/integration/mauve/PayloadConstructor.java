package com.qloudd.payments.integration.mauve;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.integration.mauve.model.TransactionDto;

import java.util.UUID;

public class PayloadConstructor {
    public TransactionDto build(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        try {
            // set thirdPartyReference
            transactionDto.setThirdPartyReference(UUID.randomUUID().toString());
        } catch (Exception e) {

        }
    }
}
