package com.qloudd.payments.exceptions.product;

import com.qloudd.payments.commons.Common;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.ErrorCode;

import java.util.List;
import java.util.Map;

public class ProductUpdateException extends ProductException {

    public ProductUpdateException(ErrorCode errorCode, Product product) {
        super(errorCode, product);
    }

    public ProductUpdateException(ErrorCode errorCode, List<String> details, Product product) {
        super(errorCode, details, product);
    }
}
