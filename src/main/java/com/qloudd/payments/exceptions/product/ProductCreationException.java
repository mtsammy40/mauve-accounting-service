package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.ErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductCreationException extends ProductException {


    public ProductCreationException(Product product, ErrorCode errorCode) {
        super(errorCode, product);
    }

    public ProductCreationException(ErrorCode errorCode, List<String> details, Product product) {
        super(errorCode, details, product);
    }
}
