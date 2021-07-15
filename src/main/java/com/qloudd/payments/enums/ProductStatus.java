package com.qloudd.payments.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ProductStatus {
    NEW("NEW"), ACTIVE("ACTIVE"), CLOSED("CLOSED");

    private final String status;

    ProductStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ProductStatus resolve(String status) {
        Optional<ProductStatus> productStatusOptional =  Arrays
                .stream(ProductStatus.values())
                .filter((productStatus -> productStatus.getStatus().equalsIgnoreCase(status)))
                .findFirst();
        return productStatusOptional.orElseThrow();
    }
}
