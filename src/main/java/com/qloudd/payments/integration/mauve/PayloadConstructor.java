package com.qloudd.payments.integration.mauve;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.NotImplementedException;
import com.qloudd.payments.integration.mauve.model.MauveTransactionDto;

import java.util.UUID;

public class PayloadConstructor {
    public MauveTransactionDto build(Transaction transaction) throws NotImplementedException {
       throw new NotImplementedException("Payload constructor not implemented");
    }
}
