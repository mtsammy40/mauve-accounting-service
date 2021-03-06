package com.qloudd.payments.controller;

import com.qloudd.payments.entity.*;
import com.qloudd.payments.exceptions.*;
import com.qloudd.payments.exceptions.accountType.AccountTypeCreationException;
import com.qloudd.payments.exceptions.accounts.AccountCreationException;
import com.qloudd.payments.exceptions.accounts.AccountNotFoundException;
import com.qloudd.payments.exceptions.accounts.AccountTrashException;
import com.qloudd.payments.exceptions.accounts.AccountUpdateException;
import com.qloudd.payments.exceptions.product.ProductCreationException;
import com.qloudd.payments.exceptions.product.ProductUpdateException;
import com.qloudd.payments.model.api.ApiResponse;
import com.qloudd.payments.model.api.TransactionDto;
import com.qloudd.payments.repository.ConfigurationRepository;
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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController()
@CrossOrigin()
@RequestMapping("/mpg")
public class ApiController {
    private static Logger LOG = LoggerFactory.getLogger(ApiController.class);

    private final AccountService accountService;
    private final ProductService productService;
    private final TransactionService transactionService;
    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ApiController(AccountService accountService, ProductService productService, TransactionService transactionService, ConfigurationRepository configurationRepository) {
        this.accountService = accountService;
        this.productService = productService;
        this.transactionService = transactionService;
        this.configurationRepository = configurationRepository;
    }

    @GetMapping("/newConfig")
    public ResponseEntity<ApiResponse<Configuration>> config() {
        var config = Configuration.builder()
                .id(UUID.randomUUID())
                .name("Sam")
                .createdAt(Instant.now())
                .userId(UUID.randomUUID())
                .type("TEST")
                .config(
                        Configuration.ConfigData.builder()
                                .mpesa(
                                        Configuration.MpesaConfiguration.builder()
                                                .consumerKey("test")
                                                .consumerSecret("test")
                                                .initiatorName("test")
                                                .lnmShortCode("test")
                                                .passkey("test")
                                                .securityCredential("test")
                                                .build()
                                ).build()
                ).build();
        return ResponseEntity.of(Optional.of(new ApiResponse<>(configurationRepository.save(config))));
    }

    @GetMapping("/configs")
    public ResponseEntity<ApiResponse<List<Configuration>>> list() {
        return ResponseEntity.ok(new ApiResponse<>(configurationRepository.findAll()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Transaction>> transfer(@RequestBody() TransactionDto transactionDto) {
        Transaction transaction = null;
        try {
            transaction = transactionService.transfer(transactionDto);
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

    // -- Accounts
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        try {
            List<Account> account = accountService.getAccounts();
            return ResponseEntity.of(Optional.of(account));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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
    public ResponseEntity<ApiResponse<Account>> createAccount(@RequestBody() Account account) {
        try {
            account = accountService.create(account);
            return ResponseEntity.accepted().body((new ApiResponse<Account>(account)));
        } catch (AccountCreationException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse<Account>(account).addErrors(e.getDetails()));
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

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Account> trashAccount(@PathVariable Long id) {
        try {
            Account account = accountService.trash(id);
            return ResponseEntity.of(Optional.of(account));
        } catch (AccountTrashException e) {
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
            return ResponseEntity.of(Optional.of(new ApiResponse<>(accountTypeResult)));
        } catch (AccountTypeCreationException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse<AccountType>(accountType).addErrors(e.getDetails()));
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
                    .body(new ApiResponse<Product>(product).addErrors(e.getDetails()));
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

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ApiResponse<?>> handleException(ConstraintViolationException e) {
        List<String> violations = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>().addErrors(violations));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Unexpected error occurred"));
    }
}
