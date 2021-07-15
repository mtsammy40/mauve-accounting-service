package com.qloudd.payments.commons;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.TransactionException;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.model.ChargeConfiguration;
import com.qloudd.payments.model.RangeConfigs;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.ProductRepository;
import com.qloudd.payments.repository.TransactionRepository;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Validator {
    private CustomLogger LOG = new CustomLogger(Validator.class);

    private AccountRepository accountRepository;
    private AccountTypeRepository accountTypeRepository;
    private ProductRepository productRepository;
    private TransactionRepository transactionRepository;

    public Validator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Validator(AccountRepository accountRepository, AccountTypeRepository accountTypeRepository) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    public Validator(AccountRepository accountRepository, ProductRepository productRepository) {
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
    }


    public Validator(AccountRepository accountRepository, ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    public void test(AccountType accountType, Function function) throws ValidationException {
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
        }
        // charges
        List<String> chargeConfigurationErrors = validateChargeConfiguration(accountType.getConfigurations().getCharges());
        errorList.addAll(chargeConfigurationErrors);

        // throw error to return to caller
        if (errorList.size() > 0) {
            throw new ValidationException(errorList);
        }
    }

    public void test(Product product, Function function) throws ValidationException {
        LOG.update(Function.PRODUCT_VALIDATION, product.getName());
        LOG.info("Validating... ");
        List<String> errorList = new ArrayList<>();
        // If Creation, id should be null
        if (function == Function.PRODUCT_CREATION) {
            if (product.getId() != null) {
                errorList.add("[id] should be null on creation");
            }
        }

        // If update, id should not be null
        if (function.equals(Function.PRODUCT_UPDATE)) {
            if (product.getId() == null) {
                errorList.add("[id] should not be null on update");
            }
        }

        // Name is required
        if (!StringUtils.hasText(product.getName())) {
            errorList.add("Field [ name ] is required");
        }

        // validate charge configurations
        List<String> chargeConfigurationErrors = validateChargeConfiguration(product.getConfiguration().getCharges());
        errorList.addAll(chargeConfigurationErrors);

        if (errorList.size() > 0) {
            throw new ValidationException(errorList);
        }
    }


    private List<String> validateChargeConfiguration(@Nullable List<ChargeConfiguration> charges) throws ValidationException {
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
        return errorList;
    }

    public void test(Transaction transaction, Function function) throws ValidationException {
        Account source = null;
        Account destination = null;
        Product product = null;
        List<String> errors = new ArrayList<>();
        // ThirdPartyReference cannot be null
        if (!StringUtils.hasText(transaction.getThirdPartyReference())) {
            errors.add("Field [ thirdPartyReference ] is required");
        } else {
            if (transactionRepository.existsByThirdPartyReference(transaction.getThirdPartyReference())) {
                errors.add("Transaction with thirdPartyReference [" + transaction.getThirdPartyReference() + "] already exists");
            }
        }
        // Source account cannot be null
        if (transaction.getSourceAccount() == null || transaction.getSourceAccount().getId() == null) {
            errors.add("Field [ sourceAccount ][ id ] is required");
        } else {
            // No need to run this test is dest account is not provided
            // Account must exist
            Optional<Account> accountResult = accountRepository.findById(transaction.getSourceAccount().getId());
            if (accountResult.isEmpty()) {
                errors.add("Account with id [ " + transaction.getDestAccount().getId() + " ] does not exist");
            } else {
                source = accountResult.get();
            }
        }
        // Destination account cannot be null
        if (transaction.getDestAccount() == null || transaction.getDestAccount().getId() == null) {
            errors.add("Field [ destinationAccount ][ id ] is required");
        } else {
            // No need to run this test if dest account is not provided
            // Account must exist
            Optional<Account> accountResult = accountRepository.findById(transaction.getDestAccount().getId());
            if (accountResult.isEmpty()) {
                errors.add("Account with id [ " + transaction.getDestAccount().getId() + " ] does not exist");
            } else {
                destination = accountResult.get();
            }
        }
        // product cannot be null
        if (transaction.getProduct() == null || transaction.getProduct().getId() == null) {
            errors.add("Field [ product ][ id ] is required");
        } else {
            // No need to run this test if product is not provided
            // Product must exist
            Optional<Product> productResult = productRepository.findById(transaction.getProduct().getId());
            if (productResult.isEmpty()) {
                errors.add("Product id [ " + transaction.getProduct().getId() + " ] does not exist or is inactive");
            } else {
                product = productResult.get();
            }
        }
        // amount cannot be null
        if (transaction.getAmount() == null) {
            errors.add("Field [ amount ] is required");
        } else {
            // No need to run this test if amount is null
            // amount must be a valid positive number
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Field [ amount ] must be a valid positive number");
            }
            // amount must be greater than min debit limit
            if (source != null) {
                // Amount should be greater than the min debit allowed
                BigDecimal minDebitLimit = source.getAccountType().getConfigurations().getMinDebitLimit();
                if (transaction.getAmount().compareTo(minDebitLimit) < 0) {
                    errors.add("Minimum allowed debit for this account is [ " + source.getAccountType().getConfigurations().getMinDebitLimit() + " ]");
                }
                BigDecimal maxDebitLimit = source.getAccountType().getConfigurations().getMaxDebitLimit();
                // Amount should be less than max allowed debit
                if (transaction.getAmount().compareTo(maxDebitLimit) > 0) {
                    errors.add("Maximum allowed debit for this account is [ " + source.getAccountType().getConfigurations().getMinDebitLimit() + " ]");
                }
            }
            // product
            if (product != null) {
                // amount must be greater than product min limit
                BigDecimal minTransactionAmount = product.getConfiguration().getMinTransactionAmount();
                if (transaction.getAmount().compareTo(minTransactionAmount) < 0) {
                    errors.add("Minimum allowed amount for this product is [ " + product.getConfiguration().getMinTransactionAmount() + " ]");
                }
                // amount must be smaller than product max limit
                BigDecimal maxTransactionAmount = product.getConfiguration().getMaxTransactionAmount();
                if (transaction.getAmount().compareTo(maxTransactionAmount) > 0) {
                    errors.add("Maximum allowed amount for this product is [ " + product.getConfiguration().getMinTransactionAmount() + " ]");
                }
            }
        }

        if (errors.size() > 0) {
            throw new ValidationException(errors);
        }
    }
}
