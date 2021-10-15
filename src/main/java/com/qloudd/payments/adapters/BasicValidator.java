package com.qloudd.payments.adapters;

import com.qloudd.payments.commons.CustomLogger;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.Status;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.exceptions.accounts.AccountNotFoundException;
import com.qloudd.payments.model.ChargeConfiguration;
import com.qloudd.payments.model.RangeConfigs;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.ProductRepository;
import com.qloudd.payments.service.impl.AccountServiceImpl;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BasicValidator<T> implements Validator<T> {
    protected CustomLogger LOG;

    ProductRepository productRepository;
    AccountRepository accountRepository;
    AccountTypeRepository accountTypeRepository;

    public BasicValidator() {
        this.LOG = new CustomLogger(BasicValidator.class);
    }

    public BasicValidator(ProductRepository productRepository, AccountRepository accountRepository, AccountTypeRepository accountTypeRepository) {
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.LOG = new CustomLogger(BasicValidator.class);
    }

    @Override
    public Validator<T> validate(T object, Function function) throws ValidationException {
        LOG.update(function, "");
        if (object == null) {
            throw new ValidationException(Collections.singletonList("Cannot be null"));
        }
        return this;
    }

    @Override
    public <U> U transform(T object, Class<U> transformTo) throws Exception {
        return null;
    }

    protected Account requireValidAccount(Account account) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (account == null) {
            errors.add("Invalid account");
        } else {
            if (account.getId() != null) {

                Optional<Account> srcAccount = accountRepository.findByIdAndStatus(account.getId(), Status.ACTIVE);
                if (srcAccount.isEmpty()) {
                    errors.add("Invalid account - Account Id does not exist");
                }
            } else if (StringUtils.hasText(account.getAccountNumber())) {
                Optional<Account> srcAccount = accountRepository
                        .findByAccountNumberAndStatus(account.getAccountNumber(), Status.ACTIVE);
                if (srcAccount.isEmpty()) {
                    errors.add("Invalid account - Account number does not exist");
                }
            } else {
                errors.add("Invalid  account - Dest account details are required");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        } else {
            return account;
        }

    }

    List<String> requireValidRangeConfigurations(@Nullable List<ChargeConfiguration> charges) throws ValidationException {
        List<String> errorList = new ArrayList<>();
        if (charges != null
                && charges.size() > 0) {
            // maximum of 10 charges
            if (charges.size() > 10) {
                errorList
                        .add("Field [ configurations ][ charges ] can have a max of 10 elements");
            } else {
                for (int i = 0; i < charges.size(); i++) {
                    if (charges.get(i).getName() == null || charges.get(i).getName().isEmpty()) {
                        errorList.add("Field [ configurations ][ charges ][" + i + "][ name ] is required");
                    }
                    // destination Account is required
                    String destinationAccountNumber = charges.get(i).getDestinationAccount();
                    if (destinationAccountNumber == null
                            || destinationAccountNumber.isEmpty()) {
                        errorList
                                .add("Field [ configurations ][ charges ][" +
                                        i + "][ destinationAccount ] is required");
                    } else {
                        // destination account must be valid account
                        Optional<Account> account = accountRepository.findByAccountNumberAndStatus(destinationAccountNumber, Status.ACTIVE);
                        if (account.isEmpty()) {
                            errorList
                                    .add("Destination Account [" +
                                            destinationAccountNumber
                                            + "] does not exist or is inactive");

                        }
                    }

                    // range is required
                    List<RangeConfigs> rangeConfigs = charges.get(i).getRange();
                    // maximum of 10 bands
                    if (rangeConfigs.size() > 10) {
                        errorList
                                .add("Field [ configurations ][ charges ][" + i +
                                        "][ range ] can have a max of 10 elements");
                    } else {
                        RangeConfigs prevRange = null;
                        for (int j = 0; j < rangeConfigs.size(); j++) {
                            RangeConfigs range = rangeConfigs.get(j);
                            // All fields are required
                            if (range.getMin() == null || range.getMax() == null || range.getValue() == null) {
                                errorList
                                        .add("[ min ] [ max ] [ value ] are required in range configuration");
                                // Do not proceed with validation as they may cause null pointers downstream
                                throw new ValidationException(errorList);
                            }
                            // Max range value must be greater than 0
                            if (range.getMax().compareTo(BigDecimal.ZERO) <= 0) {
                                errorList
                                        .add("Field [ configurations ][ charges ][" + i + "][ range ][" + j +
                                                "][ max ] must be greater than 0");
                            }
                            // Min range value must be 0 or Greater
                            if (range.getMin().compareTo(BigDecimal.ZERO) < 0) {
                                errorList
                                        .add("Field [ configurations ][ charges ][" + i + "][ range ][" + j +
                                                "][ min ] must be 0 or greater");
                            }
                            // Max must be greater than minimum
                            if (range.getMax().compareTo(range.getMin()) <= 0) {
                                errorList
                                        .add("Field [ configurations ][ charges ][" + i + "][ range ][" + j +
                                                "][ max ] must be greater than [ min ]");
                            }
                            // Value must be 0 or greater
                            if (range.getValue().compareTo(BigDecimal.ZERO) < 0) {
                                errorList
                                        .add("Field [ configurations ][ charges ][" + i + "][ range ][" + j +
                                                "][ value ] is must be 0 or greater");
                            }
                            // Range must be successive
                            if (prevRange == null) {
                                // is the first element - min must be zero
                                if (range.getMin().compareTo(BigDecimal.ZERO) != 0) {
                                    errorList
                                            .add("Field [ configurations ][ charges ][" + i + "][ range ][" + j +
                                                    "][ min ]  must be 0 as it is the first band.");
                                }
                            } else {
                                if (!range.getMin().equals(prevRange.getMax().add(BigDecimal.ONE))) {
                                    errorList
                                            .add("Field [ configurations ][ charges ][" + i + "][ range ][" + j +
                                                    "][ min ]  must be max of previous band + 1 ");
                                    // If one band is wrong, no need to continue as all the rest are wrong
                                    throw new ValidationException(errorList);
                                }
                            }
                            prevRange = range;
                        }
                    }
                }
            }
        }
        return errorList;
    }
}
