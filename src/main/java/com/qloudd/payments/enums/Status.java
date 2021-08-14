package com.qloudd.payments.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Status {
    NEW("NEW"), ACTIVE("ACTIVE"), SUSPENDED("SUSPENDED"), CLOSED("CLOSED");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Status resolve(String status) {
        Optional<Status> productStatusOptional =  Arrays
                .stream(Status.values())
                .filter((productStatus -> productStatus.getStatus().equalsIgnoreCase(status)))
                .findFirst();
        return productStatusOptional.orElseThrow();
    }
}
