package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.StatusCode;

import java.util.List;

public class ProductUpdateException extends ProductException {

    public ProductUpdateException(StatusCode statusCode, Product product) {
        super(statusCode, product);
    }

    public ProductUpdateException(StatusCode statusCode, List<String> details, Product product) {
        super(statusCode, details, product);
    }
}
