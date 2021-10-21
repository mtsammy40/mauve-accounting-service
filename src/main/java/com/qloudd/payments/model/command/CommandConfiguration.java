package com.qloudd.payments.model.command;

public class CommandConfiguration {
    private String clientId;
    private Boolean sendNotification;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Boolean getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }
}
