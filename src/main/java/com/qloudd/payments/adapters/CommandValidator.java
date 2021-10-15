package com.qloudd.payments.adapters;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.exceptions.product.ProductNotFoundException;
import com.qloudd.payments.model.api.TransactionDto;
import com.qloudd.payments.service.AccountService;
import com.qloudd.payments.service.ProductService;

public interface CommandValidator {
    CommandValidator using(AccountService accountService, ProductService productService);
    void validate(TransactionDto transactionDto) throws ValidationException;
    Transaction buildTransaction(TransactionDto transactionDto) throws ProductNotFoundException;
}
