package com.qloudd.payments.model.integration;

import com.qloudd.payments.model.command.CommandConfiguration;

public class MauvePaymentGatewayConfig extends CommandConfiguration {
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
