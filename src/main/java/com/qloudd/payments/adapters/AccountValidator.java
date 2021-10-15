package com.qloudd.payments.adapters;

import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountValidator extends BasicValidator<Account> {

    public AccountValidator(AccountRepository accountRepository, AccountTypeRepository accountTypeRepository) {
        super(null, accountRepository, accountTypeRepository);
    }

    @Override
    public Validator<Account> validate(Account account, Function function) throws ValidationException {
        LOG.update(Function.ACCOUNT_VALIDATION, account.getUserId());
        List<String> errors = new ArrayList<>();
        // Account Number cannot be null for creation
        if (function.equals(Function.ACCOUNT_CREATION)) {
            if (!StringUtils.hasText(account.getAccountNumber())) {
                errors.add("Invalid or missing Field [ accountNumber ]");
            }
        }
        // Account type is required
        if (account.getAccountType() == null || account.getAccountType().getId() == null) {
            errors.add("Fields [accountType] and [accountType][id] are required");
        } else {
            // Has Account type -> Account Type should exist and be status:active
            accountTypeRepository
                    .findById(account.getAccountType().getId())
                    .ifPresentOrElse((accountType) -> {
                        LOG.debug("Account type exists | {}", accountType);
                    }, () -> {
                        LOG.debug("Account type id: [ {} ] not found ", account.getId());
                        errors.add("Account Type does not exist or is not active.");
                    });
        }
        List<String> userIdErrors = requireValidUserId(account.getUserId());
        errors.addAll(userIdErrors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return this;
    }

    private List<String> requireValidUserId(String userId) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if(!StringUtils.hasText(userId)) {
            errors.add("Invalid or missing userId");
        }
        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        } else {
            return errors;
        }
    }
}
