package com.qloudd.payments.service;

import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.TransactionException;

public interface TransactionService {
    Transaction transfer(Transaction transaction) throws TransactionException;
}
