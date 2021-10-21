package com.qloudd.payments.service.integration;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.NotImplementedException;
import com.qloudd.payments.exceptions.TransactionException;
import com.qloudd.payments.model.PaymentResponse;

public interface PaymentGatewayService {
    Transaction execute(Transaction transaction) throws NotImplementedException, TransactionException;
    void query(Long transactionId) throws NotImplementedException;
}
