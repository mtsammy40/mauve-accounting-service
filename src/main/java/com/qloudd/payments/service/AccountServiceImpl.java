package com.qloudd.payments.service;

import com.qloudd.payments.commons.CustomLogger;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.commons.Validator;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.*;
import com.qloudd.payments.exceptions.AccountCreationException.AccountCreationExceptionType;
import com.qloudd.payments.exceptions.AccountUpdateException.AccountUpdateExceptionType;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private CustomLogger LOG = new CustomLogger(AccountServiceImpl.class);

    AccountRepository accountRepository;
    TransactionRepository transactionRepository;
    AccountTypeRepository accountTypeRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository,
                              AccountTypeRepository accountTypeRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    @Override
    public AccountType createAccountType(AccountType accountType) throws AccountTypeCreationException {
        LOG.update(Function.ACCOUNT_TYPE_CREATION, accountType.getName());
        // Validate input
        try {
            new Validator(accountRepository, accountTypeRepository).test(accountType, Function.ACCOUNT_TYPE_CREATION);
        } catch (ValidationException e) {
            LOG.error("Account Type Creation Failed | Validation | {}", e.getErrorList());
            throw new AccountTypeCreationException(accountType, AccountTypeCreationException.Type.VALIDATION, e.getErrorList());
        } catch (Exception e) {
            LOG.error("Account Type Creation Failed - Unexpected Error | Validation | {}", e.getMessage());
            e.printStackTrace();
            throw new AccountTypeCreationException(accountType, AccountTypeCreationException.Type.UNEXPECTED);
        }
        // Persist account type
        try {
            LOG.info("Account type request is valid. Persisting account type...");
            accountTypeRepository.save(accountType);
        } catch (Exception e) {
            LOG.error("Account type Creation Failed - Unexpected error | Persistence | {} ", e.getMessage());
            e.printStackTrace();
            throw new AccountTypeCreationException(accountType, AccountTypeCreationException.Type.UNEXPECTED);
        }
        return accountType;
    }

    @Override
    public Account create(Account account) throws AccountCreationException {
        LOG.update(Function.ACCOUNT_CREATION, account.getUserId());
        // Validate user
        try {
            LOG.info("Commencing account creation. Validating input...");
            assert account.getUserId() != null;
            assert account.getAccountType() != null && account.getAccountType().getId() != null;
            // Check that the account type exists
            Optional<AccountType> accountType = accountTypeRepository.findById(account.getAccountType().getId());
            if (accountType.isEmpty()) {
                LOG.error("AccountType id [ {} ] not found or inactive.", account.getAccountType().getId());
                throw new AccountCreationException(account, AccountCreationExceptionType.VALIDATION_FAILED);
            }
        } catch (AccountCreationException e) {
            LOG.error("Error creating account | details validation", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Error creating account | details validation", e);
            e.printStackTrace();
            throw new AccountCreationException(account, AccountCreationExceptionType.VALIDATION_FAILED);
        }

        // Create account number
        try {
            String accountNumber = generateAccountNumber();
            account.setAccountNumber(accountNumber);
            LOG.info("New Account Number generated | {} ", accountNumber);
        } catch (Exception e) {
            LOG.error("Error creating account | Account number generation ", e.getMessage());
            e.printStackTrace();
            throw new AccountCreationException(account, AccountCreationExceptionType.UNEXPECTED);
        }

        // Persist
        try {
            LOG.info("Setting defaults...");
            account.setBalance(BigDecimal.ZERO);
            LOG.info("Persisting account | {}", account);
            this.accountRepository.save(account);
        } catch (Exception e) {
            LOG.error("Error creating account | persisting ", e.getMessage());
            e.printStackTrace();
            throw new AccountCreationException(account, AccountCreationExceptionType.UNEXPECTED);
        }
        return account;
    }

    @Override
    public Account update(Long accountId, Account account) throws AccountUpdateException {
        LOG.update(Function.ACCOUNT_UPDATE, account.getAccountNumber());
        // Get account
        Account existingAccount;
        try {
            existingAccount = getAccount(accountId);
        } catch (AccountNotFoundException e) {
            throw new AccountUpdateException(account, AccountUpdateExceptionType.ACCOUNT_NOT_FOUND);
        }
        // Only copy updateable fields
        existingAccount.setAccountType(account.getAccountType());

        this.accountRepository.save(account);
        return account;
    }

    @Override
    @Transactional
    public Transaction transfer(BigDecimal amount, String sourceAccNumber, String destAccNumber) throws Exception {
        Optional<Account> sourceAccountResult = accountRepository.findByAccountNumber(sourceAccNumber);
        Optional<Account> destAccountResult = accountRepository.findByAccountNumber(destAccNumber);
        if (sourceAccountResult.isEmpty()) {
            LOG.warn("Source account not found : {}", sourceAccNumber);
            throw new Exception("Source not found");
        }
        if (destAccountResult.isEmpty()) {
            LOG.warn("Destination account not found {}", destAccNumber);
            throw new Exception("Destination not found");
        }
        Transaction transaction = new Transaction(amount, sourceAccountResult.get(), destAccountResult.get());
        transactionRepository.save(transaction);

        debit(amount, transaction.getSourceAccount());

        credit(amount, transaction.getDestAccount());

        transaction.setStatus(Transaction.Status.COMPLETED_OK);
        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public void debit(BigDecimal amount, Account account) throws Exception {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new Exception("Source account has insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Override
    public void credit(BigDecimal amount, Account account) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    @Override
    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Optional<Account> accountResult = accountRepository.findByAccountNumber(accountNumber);
        if (accountResult.isEmpty()) {
            throw new AccountNotFoundException(accountNumber);
        }
        return accountResult.get();
    }

    @Override
    public AccountType updateAccountType(Long id, AccountType updatedAccountType) throws NotFoundException {
        Optional<AccountType> accountTypeResult = accountTypeRepository.findById(id);
        if (accountTypeResult.isEmpty()) {
            throw new NotFoundException(AccountType.class, updatedAccountType.getId().toString());
        }
        accountTypeRepository.save(updatedAccountType);
        return updatedAccountType;
    }

    private Account getAccount(Long accountId) throws AccountNotFoundException {
        Optional<Account> accountResult = this.accountRepository.findById(accountId);
        if (accountResult.isEmpty()) {
            throw new AccountNotFoundException(accountId);
        }
        return accountResult.get();
    }

    private String generateAccountNumber() throws AccountNotFoundException {
        Optional<Account> latestAccountResult = accountRepository.getTopOrderByIdDesc();
        if (latestAccountResult.isEmpty()) {
            throw new AccountNotFoundException("Latest created account not found!");
        }
        Account latestAccount = latestAccountResult.get();
        return String.valueOf(Long.parseLong(latestAccount.getAccountNumber()) + 1);
    }
}
