package com.qloudd.payments.adapters;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.enums.CommandCode;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.exceptions.accounts.AccountNotFoundException;
import com.qloudd.payments.exceptions.product.ProductNotFoundException;
import com.qloudd.payments.model.api.TransactionDto;
import com.qloudd.payments.model.command.Command;
import com.qloudd.payments.service.AccountService;
import com.qloudd.payments.service.ProductService;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

public class BasicCommandValidator implements CommandValidator {
    private AccountService accountService;
    private ProductService productService;

    private Account sourceAccount;
    private Account destinationAccount;
    private CommandCode commandCode;

    @Override
    public BasicCommandValidator using(AccountService accountService, ProductService productService) {
        this.accountService = accountService;
        this.productService = productService;
        return this;
    }

    @Override
    public void validate(TransactionDto transactionDto) throws ValidationException {
        sourceAccount = requireValidAccount(transactionDto.getSourceAccount());
        requireValidThirdPartyReference(transactionDto.getThirdPartyReference());
        requireValidAmount(transactionDto.getAmount());
    }

    @Override
    public Transaction buildTransaction(TransactionDto transactionDto) throws ProductNotFoundException {
        Product product = productService.getOne(commandCode);
        return Transaction.builder()
                .amount(transactionDto.getAmount())
                .sourceAccount(transactionDto.getDestAccount())
                .destAccount(transactionDto.getDestAccount())
                .thirdPartyReference(transactionDto.getThirdPartyReference())
                .product(product)
                .build();
    }

    protected Account requireValidAccount(Account account) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (account == null) {
            errors.add("Invalid account");
        } else {
            if (account.getId() != null) {
                try {
                    account = accountService.getAccount(account.getId());
                } catch (AccountNotFoundException e) {
                    errors.add("Invalid account - Account Id does not exist");
                }
            } else if (StringUtils.hasText(account.getAccountNumber())) {
                try {
                    account = accountService.getAccount(account.getAccountNumber());
                } catch (AccountNotFoundException e) {
                    errors.add("Invalid account - Account number does not exist");
                }
            } else {
                errors.add("Invalid  account - Dest account details are required");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        } else {
            return account;
        }
    }

    protected void requireValidAmount(BigDecimal amount) throws ValidationException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(Collections.singletonList("Invalid amount"));
        }
    }

    protected void requireValidThirdPartyReference(String thirdPartyReference) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (!StringUtils.hasText(thirdPartyReference)) {
            errors.add("Invalid thirdPartyReference");
        } else {
            if (thirdPartyReference.length() < 18) {
                errors.add("ThirdPartyReference has to have more than 18 characters");
            }
            if (thirdPartyReference.length() > 32) {
                errors.add("ThirdPartyReference has to have less than 32 characters");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    protected void requireValidProductId(Long productId) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (productId == null || productId < 0L) {
            errors.add("Invalid product Id");
        } else {
            try {
                var product = productService.getOne(productId);
            } catch (ProductNotFoundException e) {
                errors.add(e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    protected CommandCode requireValidCommand(String command) throws ValidationException {
        List<String> errors = new ArrayList<>();
        CommandCode commandCode = null;
        if (!StringUtils.hasText(command)) {
            errors.add("Invalid errors");
        } else {
            try {
                commandCode = CommandCode.resolve(command);
            } catch (NoSuchElementException e) {
                errors.add("Invalid command");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        } else {
            return commandCode;
        }

    }


}
