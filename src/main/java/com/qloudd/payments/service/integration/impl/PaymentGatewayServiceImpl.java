package com.qloudd.payments.service.integration.impl;

import com.qloudd.payments.commons.CustomLogger;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Configuration;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.enums.CommandCode;
import com.qloudd.payments.enums.StatusCode;
import com.qloudd.payments.exceptions.NotImplementedException;
import com.qloudd.payments.exceptions.PaymentExecutionException;
import com.qloudd.payments.exceptions.TransactionException;
import com.qloudd.payments.model.PaymentResponse;
import com.qloudd.payments.model.RequestLog;
import com.qloudd.payments.repository.ConfigurationRepository;
import com.qloudd.payments.repository.TransactionRepository;
import com.qloudd.payments.service.TransactionService;
import com.qloudd.payments.service.integration.PaymentGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {
    private final CustomLogger LOG = new CustomLogger(PaymentGatewayService.class);

    final TransactionRepository transactionRepository;
    final ConfigurationRepository configurationRepository;

    @Autowired
    public PaymentGatewayServiceImpl(TransactionRepository transactionRepository, ConfigurationRepository configurationRepository) {
        this.transactionRepository = transactionRepository;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public Transaction execute(Transaction transaction) throws TransactionException {
        LOG.update(Function.TRANSACTION_TRANSFER, transaction.getThirdPartyReference());
        List<PaymentResponse> responses = new ArrayList<>();
        Configuration configuration = configurationRepository.findTopByUserId(transaction.getInitiator())
                .orElseThrow(() -> new TransactionException(transaction, TransactionException.Type.CONFIGURATION_NOT_FOUND));
        LOG.info("Got configurations {}", configuration);
        transaction.getProduct().getConfiguration().getCommands().forEach((command) -> {
            RequestLog log = RequestLog.builder().initiated(LocalDateTime.now()).attempt(0).build();
            try {
                PaymentResponse tPaymentResponse = CommandCode
                        .resolve(command.getCode())
                        .getPaymentExecutor()
                        .withConfigs(configuration)
                        .prepare(transaction)
                        .execute();
                responses.add(tPaymentResponse);
                log.setCompleted(LocalDateTime.now());
                log.setResponse(tPaymentResponse);
                log.setResponseCode(tPaymentResponse.getStatusCode().getHttpStatus().value());
                if (!tPaymentResponse.getStatusCode().equals(StatusCode.OK)) {
                    transaction.setReverseTransaction(true);
                }
                transaction.getMisc().getRequestLogs()
                        .add(log);
            } catch (PaymentExecutionException e) {
                e.printStackTrace();
                transaction.setReverseTransaction(true);
                responses.add(PaymentResponse.builder().statusCode(StatusCode.UNEXPECTED_ERROR).build());
            }
        });
        return transaction;
    }

    @Override
    public void query(Long transactionId) throws NotImplementedException {
        throw new NotImplementedException("Execution through payment service is not yet implemented");
    }
}
