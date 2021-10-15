package com.qloudd.payments.service.impl;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.accounts.AccountNotFoundException;
import com.qloudd.payments.model.api.Identifier;
import com.qloudd.payments.model.api.TransactionDto;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.ProductRepository;
import com.qloudd.payments.repository.TransactionRepository;
import com.qloudd.payments.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ValidationServiceImpl implements ValidationService {
    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public ValidationServiceImpl(AccountRepository accountRepository,
                                 AccountTypeRepository accountTypeRepository,
                                 ProductRepository productRepository,
                                 TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Returns true if account exists
     *
     * @param account
     * @return
     */
    public Account fetchAccountDetails(Account account) throws AccountNotFoundException {
        Optional<Account> accountResult = accountRepository.findByIdOrAccountNumber(account.getId(), account.getAccountNumber());
        return accountResult.orElseThrow(() -> new AccountNotFoundException(account.getId()));
    }

    /**
     * Returns true if the identifier object has either the accountId of accountNumber set.
     * Otherwise it returns false
     *
     * @param identifier
     * @return
     */
    public boolean hasAccountDetails(Identifier identifier) {
        return identifier.getAccountId() != null || StringUtils.hasText(identifier.getAccountNumber());
    }

    /**
     * Returns true if product exists and is active
     *
     * @param product
     * @return
     */
    public boolean productExists(Product product) {
        Optional<Product> productResult = productRepository.findById(product.getId());
        product = productResult.orElseGet(() -> Product.builder().build());
        return productResult.isPresent();
    }

    public List<String> validateProductLimits(Product product, TransactionDto transactionDto) {
        List<String> errors = new ArrayList<>();
        // product
        if (product != null) {
            // amount must be greater than product min limit
            BigDecimal minTransactionAmount = product.getConfiguration().getMinTransactionAmount();
            if (transactionDto.getAmount().compareTo(minTransactionAmount) < 0) {
                errors.add("Minimum allowed amount for this product is [ " + product.getConfiguration().getMinTransactionAmount() + " ]");
            }
            // amount must be smaller than product max limit
            BigDecimal maxTransactionAmount = product.getConfiguration().getMaxTransactionAmount();
            if (transactionDto.getAmount().compareTo(maxTransactionAmount) > 0) {
                errors.add("Maximum allowed amount for this product is [ " + product.getConfiguration().getMinTransactionAmount() + " ]");
            }
        }
        return errors;
    }

    public List<String> validateSourceAccountLimits(Account account, TransactionDto transactionDto) {
        List<String> errors = new ArrayList<>();
        // amount must be greater than min debit limit
        if (account != null) {
            // Amount should be greater than the min debit allowed
            BigDecimal minDebitLimit = account.getAccountType().getConfigurations().getMinDebitLimit();
            if (transactionDto.getAmount().compareTo(minDebitLimit) < 0) {
                errors.add("Minimum allowed debit for this account is [ " + account.getAccountType().getConfigurations().getMinDebitLimit() + " ]");
            }
            BigDecimal maxDebitLimit = account.getAccountType().getConfigurations().getMaxDebitLimit();
            // Amount should be less than max allowed debit
            if (transactionDto.getAmount().compareTo(maxDebitLimit) > 0) {
                errors.add("Maximum allowed debit for this account is [ " + account.getAccountType().getConfigurations().getMinDebitLimit() + " ]");
            }
        } else {
            errors.add("Account not found");
        }
        return errors;
    }
}
