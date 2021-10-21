package com.qloudd.payments.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.enums.CommandCode;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.model.api.TransactionDto;
import com.qloudd.payments.model.command.Command;
import com.qloudd.payments.model.integration.MauvePaymentGatewayConfig;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.ProductRepository;
import com.qloudd.payments.repository.TransactionRepository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TransactionValidator extends BasicValidator<Transaction> {
    TransactionRepository transactionRepository;

    private Account destAccount;

    public TransactionValidator(ProductRepository productRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        super(productRepository, accountRepository, null);
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Validator<Transaction> validate(Transaction transaction, Function function) throws ValidationException {
        Account source = null;
        Account destination = null;
        Product product = null;
        List<String> errors = new ArrayList<>();

        // Source account cannot be null
        if (transaction.getSourceAccount() == null || transaction.getSourceAccount().getId() == null) {
            errors.add("Field [ sourceAccount ][ id ] is required");
        } else {
            // No need to run this test is dest account is not provided
            // Account must exist
            LOG.info("Source account {}", transaction.getSourceAccount(), accountRepository);
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
        return this;
    }

    List<String> requireValidThirdPartyReference(String thirdPartyReference) {
        List<String> errors  = new ArrayList<>();
        if (!StringUtils.hasText(thirdPartyReference)) {
            errors.add("Field [ thirdPartyReference ] is required");
        } else {
            if (transactionRepository.existsByThirdPartyReference(thirdPartyReference)) {
                errors.add("Transaction with thirdPartyReference [" + thirdPartyReference + "] already exists");
            }
        }
        return errors;
    }
}
