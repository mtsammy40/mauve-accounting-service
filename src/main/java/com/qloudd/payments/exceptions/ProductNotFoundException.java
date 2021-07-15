package com.qloudd.payments.exceptions;

public class ProductNotFoundException extends Exception {
    private Long productId;

    public ProductNotFoundException(Long id) {
        super("Product with id [ " + id.toString() + " ] does not exist");
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
