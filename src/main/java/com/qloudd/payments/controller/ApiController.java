package com.qloudd.payments.controller;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.AccountType;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.*;
import com.qloudd.payments.model.api.ApiResponse;
import com.qloudd.payments.service.AccountService;
import com.qloudd.payments.service.ProductService;
import com.qloudd.payments.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private final ProductService productService;
    private final TransactionService transactionService;

    @Autowired
    public ApiController(AccountService accountService, ProductService productService, TransactionService transactionService) {
        this.accountService = accountService;
        this.productService = productService;
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Transaction>> transfer(@RequestBody() Transaction transactionRequest) {
        Transaction transaction = null;
        try {
            transaction = transactionService.transfer(transactionRequest);
        } catch (TransactionException e) {
            if (e.getType().equals(TransactionException.Type.VALIDATION)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<Transaction>().addErrors(e.getErrors()));
            }
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse<Transaction>().addError(e.getMessage()));
        }
        return ResponseEntity.of(Optional.of(new ApiResponse<>(transaction)));
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
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody() Account account) {
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
                    .body(new ApiResponse<AccountType>(accountType).addErrors(e.getErrors()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // List
    @GetMapping("/accounts/types")
    public ResponseEntity<ApiResponse<Page<AccountType>>> listAccountTypes() {
        try {
            Page<AccountType> accountTypePage = accountService.get();
            return ResponseEntity.of(Optional.of(new ApiResponse<>(accountTypePage)));
        } catch (Exception e) {
            e.printStackTrace();
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

    //  -- Products

    // Create
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody() Product product) {
        try {
            Product productResult = productService.create(product);
            return ResponseEntity.of(Optional.of(new ApiResponse<>(productResult)));
        } catch (ProductCreationException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse<Product>(product).addErrors(e.getErrors()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update
    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable String productId, @RequestBody() Product product) {
        try {
            Product productResult = productService.update(Long.parseLong(productId), product);
            return ResponseEntity.of(Optional.of(new ApiResponse<>(productResult)));
        } catch (ProductUpdateException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse<Product>().addError(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // List
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts() {
        try {
            Page<Product> productsPage = productService.get();
            return ResponseEntity.of(Optional.of(new ApiResponse<>(productsPage)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
