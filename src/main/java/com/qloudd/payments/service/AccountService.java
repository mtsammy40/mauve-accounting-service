package com.qloudd.payments.service;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.*;
import com.qloudd.payments.exceptions.accountType.AccountTypeCreationException;
import com.qloudd.payments.exceptions.accounts.AccountCreationException;
import com.qloudd.payments.exceptions.accounts.AccountNotFoundException;
import com.qloudd.payments.exceptions.accounts.AccountTrashException;
import com.qloudd.payments.exceptions.accounts.AccountUpdateException;
import com.qloudd.payments.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface AccountService {
    Account create(Account account) throws AccountCreationException;
    Account update(Long accountId, Account account) throws AccountUpdateException;
    Account trash(Long accountId) throws AccountTrashException;
    Account getAccount(String accountNumber) throws AccountNotFoundException;
    Account getAccount(Long accountId) throws AccountNotFoundException;
    AccountType createAccountType(AccountType accountType) throws AccountTypeCreationException;
    Page<AccountType> get();
    AccountType updateAccountType(Long id, AccountType accountType) throws NotFoundException;
    Transaction transfer(BigDecimal amount, String sourceAccNumber, String destAccNumber) throws Exception;
    void debit(BigDecimal amount, Account account) throws Exception;
    void credit(BigDecimal amount, Account account);
    AccountRepository getRepository();
}
