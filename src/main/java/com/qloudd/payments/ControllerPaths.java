package com.qloudd.payments;

public enum ControllerPaths {
    ACCOUNTS("accounts");

    private final String path;
    ControllerPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
