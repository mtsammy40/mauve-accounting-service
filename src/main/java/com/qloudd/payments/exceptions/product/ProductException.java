package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.StatusCode;
import com.qloudd.payments.exceptions.GeneralAppException;

import java.util.List;

public class ProductException extends GeneralAppException {
    Product product;
    public ProductException(StatusCode statusCode, Product product) {
        super(statusCode);
        this.product = product;
    }

    public ProductException(StatusCode statusCode, List<String> details, Product product) {
        super(statusCode, details);
        this.product = product;
    }
}
