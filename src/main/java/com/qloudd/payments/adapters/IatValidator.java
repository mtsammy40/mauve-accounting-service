package com.qloudd.payments.adapters;

import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.model.api.TransactionDto;

import java.util.ArrayList;
import java.util.List;

public class IatValidator extends BasicCommandValidator {

    @Override
    public void validate(TransactionDto transactionDto) throws ValidationException {
        List<String> errors = new ArrayList<>();
        try {
            requireValidAccount(transactionDto.getSourceAccount());
            requireValidAccount(transactionDto.getDestAccount());
            requireValidAmount(transactionDto.getAmount());
            requireValidThirdPartyReference(transactionDto.getThirdPartyReference());
        } catch (ValidationException e) {
            errors.addAll(e.getErrorList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
