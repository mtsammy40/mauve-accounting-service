package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.StatusCode;

public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException(Long productId) {
        super(StatusCode.PRODUCT_NOT_FOUND, new Product());
    }

    public ProductNotFoundException(String commandCode) {
        super(StatusCode.PRODUCT_NOT_FOUND, new Product());
    }
}
