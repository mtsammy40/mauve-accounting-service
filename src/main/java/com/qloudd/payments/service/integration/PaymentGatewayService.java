package com.qloudd.payments.service.integration;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.NotImplementedException;

public interface PaymentGatewayService {
    void execute(Transaction transaction) throws NotImplementedException;
    void query(Long transactionId) throws NotImplementedException;
}
