package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.ErrorCode;
import com.qloudd.payments.exceptions.GeneralAppException;

import java.util.List;

public class ProductException extends GeneralAppException {
    Product product;
    public ProductException(ErrorCode errorCode, Product product) {
        super(errorCode);
        this.product = product;
    }

    public ProductException(ErrorCode errorCode, List<String> details, Product product) {
        super(errorCode, details);
        this.product = product;
    }
}
