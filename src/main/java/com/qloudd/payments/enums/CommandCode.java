package com.qloudd.payments.enums;

import com.qloudd.payments.model.command.CommandConfiguration;
import com.qloudd.payments.model.integration.MauvePaymentGatewayConfig;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum CommandCode {
    INTER_ACCOUNT_TRANSFER("_IAT", CommandConfiguration.class),
    MAUVE_PAYMENT_GATEWAY("_MPG", MauvePaymentGatewayConfig.class);

    private final String code;
    private final Class<?> configClass;

    CommandCode(String code, Class<?> configClass) {
        this.code = code;
        this.configClass = configClass;
    }

    public String getCode() {
        return code;
    }

    public Class<?> getConfigClass() {
        return configClass;
    }

    public static CommandCode resolve(String code) {
        return Arrays.stream(CommandCode.values())
                .filter((commandCode) -> commandCode.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Code [ "+code+" ] does not identify any service"));
    }
}
