package com.qloudd.payments.adapters;

import com.qloudd.payments.commons.CustomLogger;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountTypeValidation extends BasicValidator<AccountType> {

    public AccountTypeValidation(AccountRepository accountRepository, AccountTypeRepository accountTypeRepository) {
        super(null, accountRepository, accountTypeRepository);
    }

    @Override
    public Validator<AccountType> validate(AccountType accountType, Function function) throws ValidationException {
        LOG.update(Function.ACCOUNT_TYPE_VALIDATION, accountType.getName());
        LOG.info("Validating | Function : {} | ... ", function);
        List<String> errorList = new ArrayList<>();
        if (function.equals(Function.ACCOUNT_TYPE_CREATION)) {
            // if Creation - ID should be null
            if (accountType.getId() != null) {
                errorList.add("[id] should be null on creation");
            }
        }
        if (accountType.getName() == null || accountType.getName().isEmpty()) {
            errorList.add("Field [ name ] is required");
        }
        if (accountType.getName() == null || accountType.getName().isEmpty()) {
            errorList.add("Field [ name ] is required");
        }
        // Max Debit limit
        if (accountType.getConfigurations() == null) {
            errorList.add("Field [ configurations ] is required");
        } else {
            if (accountType.getConfigurations().getMaxDebitLimit() != null
                    && accountType.getConfigurations().getMaxDebitLimit().compareTo(BigDecimal.ZERO) < 1) {
                errorList.add("Field [ configurations ] is invalid");
            }
        }
        // Min Debit Limit
        if (accountType.getConfigurations() == null) {
            errorList.add("Field [ maxDebit ] is required");
        } else {
            if (accountType.getConfigurations().getMinDebitLimit() != null
                    && accountType.getConfigurations().getMinDebitLimit().compareTo(BigDecimal.ZERO) < 0) {
                errorList.add("Field [ minDebit ] is invalid");
            }
            // charges
            List<String> chargeConfigurationErrors = requireValidRangeConfigurations(accountType.getConfigurations().getCharges());
            errorList.addAll(chargeConfigurationErrors);
        }

        // throw error to return to caller
        if (errorList.size() > 0) {
            throw new ValidationException(errorList);
        } else {
            return this;
        }
    }
}
