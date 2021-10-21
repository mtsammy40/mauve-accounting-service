package com.qloudd.payments.service.impl;

import com.qloudd.payments.adapters.AccountTypeValidation;
import com.qloudd.payments.adapters.AccountValidator;
import com.qloudd.payments.commons.CustomLogger;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.enums.StatusCode;
import com.qloudd.payments.enums.Status;
import com.qloudd.payments.exceptions.*;
import com.qloudd.payments.exceptions.accountType.AccountTypeCreationException;
import com.qloudd.payments.exceptions.accounts.AccountNotFoundException;
import com.qloudd.payments.exceptions.accounts.AccountUpdateException;
import com.qloudd.payments.exceptions.accounts.AccountCreationException;
import com.qloudd.payments.exceptions.accounts.AccountTrashException;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.TransactionRepository;
import com.qloudd.payments.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
            new AccountTypeValidation(accountRepository, accountTypeRepository)
                    .validate(accountType, Function.ACCOUNT_TYPE_CREATION);
        } catch (ValidationException e) {
            LOG.error("Account Type Creation Failed | Validation | {}", e.getErrorList());
            throw new AccountTypeCreationException(accountType, StatusCode.VALIDATION_FAILED,
                    e.getErrorList());
        } catch (Exception e) {
            LOG.error("Account Type Creation Failed - Unexpected Error | Validation | {}", e.getMessage());
            e.printStackTrace();
            throw new AccountTypeCreationException(accountType, StatusCode.UNEXPECTED_ERROR);
        }
        // Persist account type
        try {
            LOG.info("Account type request is valid. Persisting account type...");
            accountTypeRepository.save(accountType);
        } catch (Exception e) {
            LOG.error("Account type Creation Failed - Unexpected error | Persistence | {} ", e.getMessage());
            e.printStackTrace();
            throw new AccountTypeCreationException(accountType, StatusCode.UNEXPECTED_ERROR);
        }
        return accountType;
    }

    @Override
    public Page<AccountType> get() {
        LOG.update(Function.ACCOUNT_CREATION, "List Products");
        try {
            LOG.info("Listing account type | params [ ] ...");
            Pageable pageable = Pageable.unpaged();
            return accountTypeRepository.findAll(pageable);
        } catch (Exception e) {
            LOG.error("Account type List Failed - Unexpected Error | {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Account create(Account account) throws AccountCreationException {
        LOG.update(Function.ACCOUNT_CREATION, account.getUserId());
        // Validate user
        try {
            LOG.info("Commencing account creation. Validating input...");
            new AccountValidator(accountRepository, accountTypeRepository)
                    .validate(account, Function.ACCOUNT_CREATION);
        } catch (ValidationException e) {
            LOG.error("Error creating account | validation | {}", e.getErrorList());
            throw new AccountCreationException(account, e.getErrorList(), StatusCode.VALIDATION_FAILED);
        } catch (Exception e) {
            LOG.error("Error creating account | details validation", e);
            e.printStackTrace();
            throw new AccountCreationException(account, StatusCode.VALIDATION_FAILED);
        }

        // Create account number
        try {
            String accountNumber = generateAccountNumber();
            account.setAccountNumber(accountNumber);
            LOG.info("New Account Number generated | {} ", accountNumber);
        } catch (Exception e) {
            LOG.error("Error creating account | Account number generation ", e.getMessage());
            e.printStackTrace();
            throw new AccountCreationException(account, StatusCode.UNEXPECTED_ERROR);
        }

        // Persist
        try {
            LOG.info("Setting defaults...");
            account.setBalance(BigDecimal.ZERO);
            account.setStatus(Status.ACTIVE);
            LOG.info("Persisting account | {}", account);
            this.accountRepository.save(account);
        } catch (Exception e) {
            LOG.error("Error creating account | persisting ", e.getMessage());
            e.printStackTrace();
            throw new AccountCreationException(account, StatusCode.UNEXPECTED_ERROR);
        }
        return account;
    }

    /**
     * @param accountId
     * @param account
     * @return
     * @throws AccountUpdateException
     * @Deprecated No need to update account. Just close it and start another
     */
    @Override
    public Account update(Long accountId, Account account) throws AccountUpdateException {
        LOG.update(Function.ACCOUNT_UPDATE, account.getAccountNumber());
        // Get account
        Account existingAccount;
        try {
            existingAccount = getAccount(accountId);
        } catch (AccountNotFoundException e) {
            throw new AccountUpdateException(account, StatusCode.ACCOUNT_NOT_FOUND);
        }
        // Only copy update-able fields
        existingAccount.setAccountType(account.getAccountType());

        this.accountRepository.save(account);
        return account;
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
    public AccountRepository getRepository() {
        return accountRepository;
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
    public Account getActiveAccount(String accountNumber) throws AccountNotFoundException {
        Optional<Account> accountResult = accountRepository.findByAccountNumber(accountNumber);
        if (accountResult.isEmpty()) {
            throw new AccountNotFoundException(accountNumber);
        }
        return accountResult.get();
    }

    @Override
    public Account getActiveAccount(Long accountId) throws AccountNotFoundException {
        Optional<Account> accountResult = accountRepository.findByIdAndStatus(accountId, Status.ACTIVE);
        if (accountResult.isEmpty()) {
            throw new AccountNotFoundException(accountId);
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

    @Override
    public Transaction transfer(BigDecimal amount, String sourceAccNumber, String destAccNumber) throws Exception {
        return null;
    }

    @Override
    public Account getAccount(Long accountId) throws AccountNotFoundException {
        Optional<Account> accountResult = this.accountRepository.findById(accountId);
        if (accountResult.isEmpty()) {
            throw new AccountNotFoundException(accountId);
        }
        return accountResult.get();
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    private String generateAccountNumber() throws AccountNotFoundException {
        Optional<Account> latestAccountResult = accountRepository.findTopByIdNotNullOrderByIdDesc();
        if (latestAccountResult.isEmpty()) {
            throw new AccountNotFoundException("Latest created account not found!");
        }
        Account latestAccount = latestAccountResult.get();
        return String.valueOf(Long.parseLong(latestAccount.getAccountNumber()) + 1);
    }

    @Override
    public Account trash(Long accountId) throws AccountTrashException {
        Account account;
        try {
            account = getAccount(accountId);
        } catch (AccountNotFoundException e) {
            throw new AccountTrashException(new Account(accountId), StatusCode.ACCOUNT_NOT_FOUND);
        }

        try {
            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                LOG.warn("Cannot trash non-empty account | id: {} | balance: {} ", account.getId(),
                        account.getBalance());
                throw new AccountTrashException(account, StatusCode.NON_EMPTY_ACCOUNT);
            }
            account.setBalance(BigDecimal.ZERO);
            update(accountId, account);
        } catch (Exception e) {
            throw new AccountTrashException(account, StatusCode.UNEXPECTED_ERROR);
        }
        return account;
    }
}
