package com.qloudd.payments.integration.mauve.mpesa;

import com.qloudd.payments.entity.Configuration;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.enums.StatusCode;
import com.qloudd.payments.exceptions.PaymentExecutionException;
import com.qloudd.payments.integration.mauve.PaymentExecutor;
import com.qloudd.payments.model.PaymentResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MpesaExecutor<P, R> implements PaymentExecutor<P, R> {
    MpesaApi mpesa;
    Configuration configuration;

    public MpesaExecutor() {}

    protected String authenticate() throws PaymentExecutionException {
        String accessToken;
        try {
            accessToken = mpesa.authenticate().orElseThrow(() -> new PaymentExecutionException("Mpesa Auth Failed", StatusCode.PAYMENT_FAILED));
        } catch (IOException e) {
            e.printStackTrace();
            throw new PaymentExecutionException("Mpesa Authentication Failed", e, StatusCode.PAYMENT_FAILED);
        }
        return accessToken;
    }

    @Override
    public PaymentExecutor<P, R> withConfigs(Configuration configuration) {
        this.configuration = configuration;
        var mpesaConfig = configuration.getConfig().getMpesa();
        this.mpesa = new MpesaApi(mpesaConfig.getConsumerKey(), mpesaConfig.getConsumerSecret());
        return this;
    }

    @Override
    public PaymentExecutor<P, R> prepare(Transaction transaction) throws PaymentExecutionException {
        throw new PaymentExecutionException("Not implemented", StatusCode.UNEXPECTED_ERROR);
    }

    @Override
    public PaymentResponse<R> execute() throws PaymentExecutionException {
        throw new PaymentExecutionException("Not implemented", StatusCode.UNEXPECTED_ERROR);
    }
}
