package com.qloudd.payments.adapters;

import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.entity.Transaction;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.exceptions.product.ProductNotFoundException;
import com.qloudd.payments.model.api.TransactionDto;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.ProductRepository;
import com.qloudd.payments.repository.TransactionRepository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TransactionDtoValidator extends BasicValidator<TransactionDto> {
    ProductRepository productRepository;
    AccountRepository accountRepository;
    TransactionRepository transactionRepository;

    private Account srcAccount;
    private Product product;

    public TransactionDtoValidator(ProductRepository productRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        super(productRepository, accountRepository, null);
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Validator<TransactionDto> validate(TransactionDto transaction, Function function) throws ValidationException {
        product = requireValidProductId(transaction.getProductId());
        srcAccount = requireValidAccount(transaction.getSourceAccount());
        requireValidThirdPartyReference(transaction.getThirdPartyReference());
        return this;
    }

    protected Product requireValidProductId(Long productId) throws ValidationException {
        List<String> errors = new ArrayList<>();
        Product product = null;
        if (productId == null || productId < 0L) {
            errors.add("Invalid product Id");
        } else {
            var productRecord = productRepository.findById(productId);
            if (productRecord.isEmpty()) {
                errors.add("Product ID [" + productId + "] does not exist or is inactive");
            } else {
                product = productRecord.get();
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return product;
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
}
