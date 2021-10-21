package com.qloudd.payments.enums;

import com.qloudd.payments.adapters.BasicCommandValidator;
import com.qloudd.payments.adapters.CommandValidator;
import com.qloudd.payments.adapters.IatValidator;
import com.qloudd.payments.integration.mauve.PaymentExecutor;
import com.qloudd.payments.integration.mauve.mpesa.StkExecutor;
import com.qloudd.payments.model.command.CommandConfiguration;
import com.qloudd.payments.model.integration.MauvePaymentGatewayConfig;
import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
public enum CommandCode {
    INTER_ACCOUNT_TRANSFER("_IAT", CommandConfiguration.class, new IatValidator(), null),
    MPESA_STK_PUSH("_MSP", MauvePaymentGatewayConfig.class, new BasicCommandValidator(), new StkExecutor());

    private final String code;
    private final Class<?> configClass;
    private final CommandValidator commandValidator;
    private final PaymentExecutor paymentExecutor;

    CommandCode(String code, Class<?> configClass, CommandValidator commandValidator, PaymentExecutor paymentExecutor) {
        this.code = code;
        this.configClass = configClass;
        this.commandValidator = commandValidator;
        this.paymentExecutor = paymentExecutor;
    }

    public static CommandCode resolve(String code) {
        return Arrays.stream(CommandCode.values())
                .filter((commandCode) -> commandCode.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Code [ " + code + " ] does not identify any service"));
    }
}
