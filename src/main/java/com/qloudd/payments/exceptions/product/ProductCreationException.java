package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.StatusCode;

import java.util.List;

public class ProductCreationException extends ProductException {


    public ProductCreationException(Product product, StatusCode statusCode) {
        super(statusCode, product);
    }

    public ProductCreationException(StatusCode statusCode, List<String> details, Product product) {
        super(statusCode, details, product);
    }
}
