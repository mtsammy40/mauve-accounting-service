package com.qloudd.payments.service.integration.impl;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.NotImplementedException;
import com.qloudd.payments.service.integration.PaymentGatewayService;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    @Override
    public void execute(Transaction transaction) throws NotImplementedException {
        throw new NotImplementedException("Execution through payment service is not yet implemented");
    }

    @Override
    public void query(Long transactionId) throws NotImplementedException {
        throw new NotImplementedException("Execution through payment service is not yet implemented");
    }
}
