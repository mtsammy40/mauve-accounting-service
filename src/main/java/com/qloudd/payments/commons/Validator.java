package com.qloudd.payments.commons;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.model.ChargeConfiguration;
import com.qloudd.payments.model.RangeConfigs;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.service.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Validator {
    private CustomLogger LOG = new CustomLogger(AccountServiceImpl.class);

    private AccountRepository accountRepository;
    private AccountTypeRepository accountTypeRepository;


    public Validator(AccountRepository accountRepository, AccountTypeRepository accountTypeRepository) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    public void test(AccountType accountType, Function function) throws ValidationException {
        LOG.update(Function.ACCOUNT_TYPE_VALIDATION, accountType.getName());
        LOG.info("Validating... ");
        List<String> errorList = new ArrayList<>();
        if(function.equals(Function.ACCOUNT_TYPE_CREATION)) {
            // if Creation - ID should be null
            if(accountType.getId() != null) {
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
        }
        // charges
        if (accountType.getConfigurations().getCharges() != null
                && accountType.getConfigurations().getCharges().size() > 0) {
            List<ChargeConfiguration> charges = accountType.getConfigurations().getCharges();
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
                    }
                    // destination account must be valid account
                    Optional<Account> destinationAccount = accountRepository
                            .findByAccountNumber(destinationAccountNumber);
                    if (destinationAccount.isEmpty()) {
                        errorList
                                .add("Destination Account [" +
                                        destinationAccountNumber
                                        + "] does not exist or is inactive");
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
        // throw error to return to caller
        if (errorList.size() > 0) {
            throw new ValidationException(errorList);
        }

    }
}
