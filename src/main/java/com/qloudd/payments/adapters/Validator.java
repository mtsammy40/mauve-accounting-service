package com.qloudd.payments.adapters;

import com.qloudd.payments.commons.Function;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.ProductRepository;

public interface Validator<T> {
    Validator<T> validate(T object, Function function) throws ValidationException;
}
