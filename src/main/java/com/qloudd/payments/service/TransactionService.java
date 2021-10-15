package com.qloudd.payments.service;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.TransactionException;
import com.qloudd.payments.model.api.TransactionDto;

import javax.validation.Valid;

public interface TransactionService {
    Transaction transfer(@Valid TransactionDto transaction) throws TransactionException;
}
