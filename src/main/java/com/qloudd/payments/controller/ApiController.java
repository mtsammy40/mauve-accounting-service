package com.qloudd.payments.controller;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.*;
import com.qloudd.payments.model.api.ApiResponse;
import com.qloudd.payments.model.api.TransferRequest;
import com.qloudd.payments.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController()
@CrossOrigin()
@RequestMapping("/v1")
public class ApiController {
    private static Logger LOG = LoggerFactory.getLogger(ApiController.class);

    private final AccountService accountService;

    @Autowired
    public ApiController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody() TransferRequest request) {
        Transaction transaction = null;
        try {
            transaction = accountService.transfer(request.getAmount(), request.getSourceAccountNumber(), request.getDestinationAccNumber());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(407).build();
        }
        return ResponseEntity.of(Optional.of(transaction));
    }

    // Accounts
    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        try {
            Account account = accountService.getAccount(accountNumber);
            return ResponseEntity.of(Optional.of(account));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody() Account account) {
        try {
            account = accountService.create(account);
            return ResponseEntity.of(Optional.of(account));
        } catch (AccountCreationException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getAccount());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/accounts/{id}")
    public ResponseEntity<Account> createAccount(@PathVariable Long id, @RequestBody() Account account) {
        try {
            account = accountService.update(id, account);
            return ResponseEntity.of(Optional.of(account));
        } catch (AccountUpdateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getAccount());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Configurations
    @PostMapping("/accounts/types")
    public ResponseEntity<ApiResponse<AccountType>> createAccountType(@RequestBody() AccountType accountType) {
        try {
            AccountType accountTypeResult = accountService.createAccountType(accountType);
            return ResponseEntity.of(Optional.of(new ApiResponse<>(accountType)));
        } catch (AccountTypeCreationException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse<AccountType>(e.getErrors(), accountType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Configurations
    @PutMapping("/accounts/types/{accountTypeId}")
    public ResponseEntity<AccountType> updateAccountType(@PathVariable String accountTypeId, @RequestBody() AccountType accountType) {
        try {
            AccountType accountTypeResult = accountService.updateAccountType(Long.parseLong(accountTypeId), accountType);
            return ResponseEntity.of(Optional.of(accountTypeResult));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
