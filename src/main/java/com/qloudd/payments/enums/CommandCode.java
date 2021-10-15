package com.qloudd.payments.enums;

import com.qloudd.payments.adapters.BasicCommandValidator;
import com.qloudd.payments.adapters.CommandValidator;
import com.qloudd.payments.adapters.IatValidator;
import com.qloudd.payments.model.command.CommandConfiguration;
import com.qloudd.payments.model.integration.MauvePaymentGatewayConfig;
import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
public enum CommandCode {
    INTER_ACCOUNT_TRANSFER("_IAT", CommandConfiguration.class, new IatValidator()),
    MAUVE_STK_PUSH("_MSP", MauvePaymentGatewayConfig.class, new BasicCommandValidator());

    private final String code;
    private final Class<?> configClass;
    private final CommandValidator commandValidator;

    CommandCode(String code, Class<?> configClass, CommandValidator commandValidator) {
        this.code = code;
        this.configClass = configClass;
        this.commandValidator = commandValidator;
    }

    public static CommandCode resolve(String code) {
        return Arrays.stream(CommandCode.values())
                .filter((commandCode) -> commandCode.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Code [ " + code + " ] does not identify any service"));
    }
}
