package com.qloudd.payments.service.impl;

import com.qloudd.payments.commons.CustomLogger;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.commons.Validator;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountingEntry;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.entity.AccountingEntry.Status;
import com.qloudd.payments.exceptions.AccountNotFoundException;
import com.qloudd.payments.exceptions.AccountUpdateException;
import com.qloudd.payments.exceptions.AccountingException;
import com.qloudd.payments.exceptions.ProductNotFoundException;
import com.qloudd.payments.exceptions.TransactionException;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.exceptions.TransactionException.Type;
import com.qloudd.payments.model.ChargeConfiguration;
import com.qloudd.payments.model.ChargeType;
import com.qloudd.payments.model.RangeConfigs;
import com.qloudd.payments.repository.AccountingEntryRepository;
import com.qloudd.payments.repository.TransactionRepository;
import com.qloudd.payments.service.AccountService;
import com.qloudd.payments.service.ProductService;
import com.qloudd.payments.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;


@Service
public class TransactionServiceImpl implements TransactionService {
    private final CustomLogger LOG = new CustomLogger(TransactionService.class);

    private final AccountService accountService;
    private final ProductService productService;
    private final TransactionRepository transactionRepository;
    private final AccountingEntryRepository accountingEntryRepository;

    @Autowired
    public TransactionServiceImpl(AccountService accountService, ProductService productService,
            TransactionRepository transactionRepository, AccountingEntryRepository accountingEntryRepository) {
        this.accountService = accountService;
        this.productService = productService;
        this.transactionRepository = transactionRepository;
        this.accountingEntryRepository = accountingEntryRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Transaction transfer(Transaction transaction) throws TransactionException {
        LOG.update(Function.TRANSACTION_TRANSFER, transaction.getThirdPartyReference());
        // Set time initiated (will persist after validation)
        transaction.setInitiated(new Date(System.currentTimeMillis()));
        // validate input
        try {
            new Validator(accountService.getRepository(), productService.getRepository(), transactionRepository)
                    .test(transaction, Function.TRANSACTION_TRANSFER);
            // validation passed, persist and continue
            transaction.setStatus(Transaction.Status.PROCESSING);
            transactionRepository.save(transaction);
        } catch (ValidationException e) {
            LOG.error("Transaction Failed | Validation | {}", e.getMessage());
            TransactionException transactionException = new TransactionException(transaction, TransactionException.Type.VALIDATION);
            transactionException.setErrors(e.getErrorList());
            throw transactionException;
        } catch (Exception e) {
            LOG.error("Transaction Failed - Unexpected Error | Validation | {}", e.getMessage());
            e.printStackTrace();
            throw new TransactionException(transaction, TransactionException.Type.UNEXPECTED);
        }

        // start processing
        try {
            // Get the product and accounts details (Transaction details)
            Account sourceAccount = null;
            Account destAccount = null;
            Product product = null;
            try {
                LOG.info("Getting transaction details...");
                sourceAccount = accountService.getAccount(transaction.getSourceAccount().getId());
                destAccount = accountService.getAccount(transaction.getDestAccount().getId());
                product = productService.getOne(transaction.getProduct().getId());
                // Update transaction object
                transaction.setSourceAccount(sourceAccount);
                transaction.setDestAccount(destAccount);
                transaction.setProduct(product);
            } catch (AccountNotFoundException e) {
                LOG.error("Transaction Failed | Transaction Details | {}", e.getMessage());
                throw new TransactionException(transaction, TransactionException.Type.ACCOUNT_NOT_FOUND);
            } catch (ProductNotFoundException e) {
                LOG.error("Transaction Failed | Transaction Details | {}", e.getMessage());
                throw new TransactionException(transaction, TransactionException.Type.PRODUCT_NOT_FOUND);
            } catch (Exception e) {
                LOG.error("Transaction Failed - Unexpected | Transaction Details | {}", e.getMessage());
                throw new TransactionException(transaction, Type.UNEXPECTED);
            }

            // Calculate source charges
            List<AccountingEntry> sourceAccountingEntries = new ArrayList<>();
            BigDecimal totalSourceAccountCharges = BigDecimal.ZERO;
            try {
                LOG.info("Calculating total source account charges...");
                List<ChargeConfiguration> chargeConfigurationList = sourceAccount.getAccountType().getConfigurations()
                        .getCharges();
                sourceAccountingEntries = chargeConfigurationList.stream().map((chargeConfig) -> {
                    try {
                        return createAccountingEntry(transaction, chargeConfig);
                    } catch (AccountNotFoundException e) {
                        throw new RuntimeException("Account not found for charge..." + chargeConfig.getName());
                    }
                }).collect(Collectors.toList());
                // Get minimal charge required in account for transaction to occur
                totalSourceAccountCharges = sourceAccountingEntries.stream().map(AccountingEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                LOG.info("Total source account charges | {}", totalSourceAccountCharges);
            } catch (Exception e) {
                LOG.error("Transaction Failed - Unexpected | Charge Calculation (source) | {}", e.getMessage());
                e.printStackTrace();
                throw new TransactionException(transaction, TransactionException.Type.PRODUCT_NOT_FOUND);
            }

            // Calculate product charges
            List<AccountingEntry> productAccountingEntries = new ArrayList<>();
            BigDecimal totalProductAccountCharges = BigDecimal.ZERO;
            try {
                LOG.info("Calculating total product charges...");
                List<ChargeConfiguration> chargeConfigurationList = product.getConfiguration().getCharges();
                productAccountingEntries = chargeConfigurationList.stream().map((chargeConfig) -> {
                    try {
                        return createAccountingEntry(transaction, chargeConfig);
                    } catch (AccountNotFoundException e) {
                        throw new RuntimeException("Account not found for charge..." + chargeConfig.getName());
                    }
                }).collect(Collectors.toList());
                // Get minimal charge required in account for transaction to occur
                totalProductAccountCharges = productAccountingEntries.stream().map(AccountingEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                LOG.info("Total product charges | {}", totalSourceAccountCharges);
            } catch (Exception e) {
                LOG.warn("Transaction Failed - Unexpected | Charge Calculation (product) | {}", e.getMessage());
                throw new TransactionException(transaction, TransactionException.Type.UNEXPECTED);
            }

            // Balance check - check if account has sufficient funds
            try {
                LOG.info("Checking if account has sufficient funds...");
                BigDecimal requiredMinAmount = totalSourceAccountCharges.add(totalProductAccountCharges)
                        .add(transaction.getAmount());
                transaction.setTotalAmount(requiredMinAmount);
                if (sourceAccount.getBalance().compareTo(requiredMinAmount) < 0) {
                    LOG.warn("Insufficient funds for transaction. Current balance | {}", sourceAccount.getBalance());
                    throw new TransactionException(transaction, Type.INSUFFICIENT_FUNDS);
                }
            } catch (TransactionException e) {
                LOG.warn("Transaction Failed | Balance check | {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                LOG.error("Transaction Failed - Unexpected | Balance Check | {}", e.getMessage());
                e.printStackTrace();
                throw new TransactionException(transaction, TransactionException.Type.UNEXPECTED);
            }

            // Do Debits and Credits
            List<AccountingEntry> allAccountingEntries = new ArrayList<>();
            try {
                LOG.info("Starting debits and credits ...");
                // Debit total deductable amount from source account
                allAccountingEntries.add(createAccountingEntry(AccountingEntry.Type.DEBIT, transaction,
                        transaction.getTotalAmount(), transaction.getSourceAccount()));
                // credit configured charge accounts
                allAccountingEntries.addAll(sourceAccountingEntries);
                allAccountingEntries.addAll(productAccountingEntries);
                // credit destination account
                allAccountingEntries.add(createAccountingEntry(AccountingEntry.Type.CREDIT, transaction,
                        transaction.getAmount(), transaction.getDestAccount()));
                // execute accounting
                for (AccountingEntry accountingEntry : allAccountingEntries) {
                    handle(accountingEntry);
                }
            } catch (Exception e) {
                LOG.error("Transaction Failed - Unexpected | Accounting | {}", e.getMessage());
                e.printStackTrace();
                throw new TransactionException(transaction, TransactionException.Type.UNEXPECTED);
            }

            // Update transaction
            try {
                LOG.info("Updating transaction status...");
                transaction.complete();
                transactionRepository.save(transaction);
            } catch (Exception e) {
                LOG.error("Transaction Failed - Unexpected | Transaction Status Update | {}", e.getMessage());
                e.printStackTrace();
                throw new TransactionException(transaction, TransactionException.Type.UNEXPECTED);
            }
        } catch (TransactionException e) {
            // Fail transaction
            transaction.fail();
            transactionRepository.save(transaction);
            throw e;
        }

        return transaction;
    }

    private BigDecimal getChargeAmount(Transaction transaction, ChargeConfiguration chargeConfiguration)
            throws Exception {
        BigDecimal transactionAmount = transaction.getAmount();
        if (chargeConfiguration == null) {
            throw new TransactionException(transaction, TransactionException.Type.DIRTY_DATA);
        }
        if (transactionAmount == null || transactionAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransactionException(transaction, TransactionException.Type.VALIDATION);
        }
        if (chargeConfiguration.getChargeType().equals(ChargeType.FIXED)) {
            // find the appropriate band
            Optional<RangeConfigs> rangeConfigResult = chargeConfiguration.getRange().stream()
                    .filter((range) -> transactionAmount.compareTo(range.getMin()) >= 0
                            && transactionAmount.compareTo(range.getMax()) <= 0)
                    .findFirst();
            if (rangeConfigResult.isEmpty()) {
                throw new TransactionException(transaction, TransactionException.Type.CHARGE_BAND_NOT_FOUND);
            }
            return rangeConfigResult.get().getValue();
        } else if (chargeConfiguration.getChargeType().equals(ChargeType.PERCENTAGE)) {
            // find the appropriate band
            Optional<RangeConfigs> rangeConfigResult = chargeConfiguration.getRange().stream()
                    .filter((range) -> transactionAmount.compareTo(range.getMin()) >= 0
                            && transactionAmount.compareTo(range.getMax()) <= 0)
                    .findFirst();
            if (rangeConfigResult.isEmpty()) {
                throw new TransactionException(transaction, TransactionException.Type.CHARGE_BAND_NOT_FOUND);
            }
            // convert percentage to decimal and multiply with amount
            BigDecimal percentage = rangeConfigResult.get().getValue().divide(new BigDecimal("100"), RoundingMode.CEILING);
            return transactionAmount.multiply(percentage);
        } else {
            throw new TransactionException(transaction, TransactionException.Type.INVALID_CHARGE_CONFIG_TYPE);
        }
    }

    private AccountingEntry createAccountingEntry(Transaction transaction, ChargeConfiguration chargeConfig)
            throws AccountNotFoundException {
        BigDecimal chargeAmount;
        try {
            chargeAmount = getChargeAmount(transaction, chargeConfig);
        } catch (Exception e) {
            throw new RuntimeException("Could not get charges for chargeConfig | " + chargeConfig.getName());
        }
        Account account = accountService.getAccount(chargeConfig.getDestinationAccount());
        return createAccountingEntry(AccountingEntry.Type.CREDIT, transaction, chargeAmount, account);
    }

    private AccountingEntry createAccountingEntry(AccountingEntry.Type type, Transaction transaction, BigDecimal amount,
            Account account) {
        return new AccountingEntry(type, amount, transaction, account, Status.PENDING);
    }

    private AccountingEntry handle(AccountingEntry accountingEntry) throws AccountingException {
        if (accountingEntry == null) {
            throw new AccountingException(null, AccountingException.Type.INVALID_ACCOUNTING_ENTRY);
        }
        if (accountingEntry.getType().equals(AccountingEntry.Type.CREDIT)) {
            return credit(accountingEntry);
        } else if (accountingEntry.getType().equals(AccountingEntry.Type.DEBIT)) {
            return debit(accountingEntry);
        } else {
            throw new AccountingException(accountingEntry, AccountingException.Type.INVALID_ACCOUNTING_ENTRY);
        }
    }

    private AccountingEntry debit(AccountingEntry accountingEntry) throws AccountingException {
        BigDecimal balance = accountingEntry.getAccount().getBalance();
        BigDecimal newBalance = balance.subtract(accountingEntry.getAmount());
        accountingEntry.getAccount().setBalance(newBalance);
        try {
            LOG.info("Debiting account | [{}] | with amount | [{}]", accountingEntry.getAccount().getId(), accountingEntry.getAmount());
            accountService.update(accountingEntry.getAccount().getId(), accountingEntry.getAccount());
        } catch (AccountUpdateException e) {
            throw new AccountingException(accountingEntry, AccountingException.Type.INVALID_ACCOUNTING_ENTRY);
        }

        accountingEntry.setStatus(AccountingEntry.Status.SUCCESS);
        return accountingEntryRepository.save(accountingEntry);
    }

    private AccountingEntry credit(AccountingEntry accountingEntry) throws AccountingException {
        BigDecimal balance = accountingEntry.getAccount().getBalance();
        BigDecimal newBalance = balance.add(accountingEntry.getAmount());
        accountingEntry.getAccount().setBalance(newBalance);
        try {
            LOG.info("Crediting account | [{}] | with amount | [{}]", accountingEntry.getAccount().getId(), accountingEntry.getAmount());
            accountService.update(accountingEntry.getAccount().getId(), accountingEntry.getAccount());
        } catch (AccountUpdateException e) {
            throw new AccountingException(accountingEntry, AccountingException.Type.INVALID_ACCOUNTING_ENTRY);
        }

        accountingEntry.setStatus(AccountingEntry.Status.SUCCESS);
        return accountingEntryRepository.save(accountingEntry);
    }
}
