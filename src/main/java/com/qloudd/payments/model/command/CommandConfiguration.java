package com.qloudd.payments.model.command;

public class CommandConfiguration {
    private String clientId;
    private String sendNotification;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(String sendNotification) {
        this.sendNotification = sendNotification;
    }
}
