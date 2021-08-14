package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.ErrorCode;

public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException(Long productId) {
        super(ErrorCode.PRODUCT_NOT_FOUND, new Product());
    }
}
