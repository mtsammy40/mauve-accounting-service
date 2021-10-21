package com.qloudd.payments.integration.mauve;

import com.qloudd.payments.entity.Configuration;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.PaymentExecutionException;
import com.qloudd.payments.model.PaymentResponse;

public interface PaymentExecutor<P, R> {
    PaymentExecutor<P, R> withConfigs(Configuration configuration);
    PaymentExecutor<P, R> prepare(Transaction transaction) throws PaymentExecutionException;

    PaymentResponse<R> execute() throws PaymentExecutionException;
}
