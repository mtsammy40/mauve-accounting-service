package com.qloudd.payments.integration.mauve.mpesa;

import com.corneliouzbett.mpesasdk.core.rest.request.OnlinePayment;
import com.google.gson.Gson;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.enums.StatusCode;
import com.qloudd.payments.exceptions.PaymentExecutionException;
import com.qloudd.payments.integration.mauve.PaymentExecutor;
import com.qloudd.payments.integration.mauve.mpesa.model.StkSimulationResponse;
import com.qloudd.payments.model.PaymentResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class StkExecutor extends MpesaExecutor<OnlinePayment, StkSimulationResponse> {
    OnlinePayment payload;

    public StkExecutor() {
    }

    @Override
    public PaymentExecutor<OnlinePayment, StkSimulationResponse> prepare(Transaction transaction) throws PaymentExecutionException {
        try {
            var mpesaConfiguration = configuration.getConfig().getMpesa();
            OnlinePayment onlinePayment = new OnlinePayment();
            onlinePayment.setAccountReference(mpesaConfiguration.getInitiatorName());
            onlinePayment.setBusinessShortCode(Integer.parseInt(mpesaConfiguration.getLnmShortCode()));
            onlinePayment.setCallbackURL("https://posthere.io/f07d-4a20-bbd6");
            onlinePayment.setAmount(transaction.getAmount().toBigInteger().toString());
            onlinePayment.setPartyA(transaction.getDestIdentifier());
            onlinePayment.setPartyB(mpesaConfiguration.getLnmShortCode());
            onlinePayment.setPhoneNumber(transaction.getDestIdentifier());
            onlinePayment.setTimestamp(getFormatedDate(new Date(), "ddMMyyyyHHmmss"));
            String passwordBeforeEncoding = mpesaConfiguration.getShortCode() + mpesaConfiguration.getPasskey() + onlinePayment.getTimestamp();
            String password = Base64.getEncoder().encodeToString(passwordBeforeEncoding.getBytes(StandardCharsets.UTF_8));
            onlinePayment.setPassword(password);
            onlinePayment.setTransactionType("CustomerPayBillOnline");
            onlinePayment.setTransactionDescription(transaction.getMisc().getDescription());
            log.info("Payload {}", new Gson().toJson(onlinePayment));
            this.payload = onlinePayment;
            return this;
        } catch (Exception e) {
            throw new PaymentExecutionException(e.getMessage(), e, StatusCode.UNEXPECTED_ERROR);
        }
    }

    @Override
    public PaymentResponse<StkSimulationResponse> execute() throws PaymentExecutionException {
        try {
            authenticate();
            MpesaResponseWrapper<StkSimulationResponse> response = mpesa.STKPushSimulation(this.payload);
            return PaymentResponse
                    .<StkSimulationResponse>builder()
                    .statusCode(response.getIsSuccessful() ? StatusCode.OK : StatusCode.PAYMENT_FAILED)
                    .data(response.getResponseBody())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaymentExecutionException(e.getMessage(), e, StatusCode.PAYMENT_FAILED);
        }
    }
    public static String getFormatedDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}
