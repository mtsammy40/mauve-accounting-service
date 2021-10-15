package com.qloudd.payments.service.impl;

import com.qloudd.payments.adapters.ProductValidator;
import com.qloudd.payments.commons.CustomLogger;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.CommandCode;
import com.qloudd.payments.enums.ErrorCode;
import com.qloudd.payments.enums.Status;
import com.qloudd.payments.exceptions.product.ProductCreationException;
import com.qloudd.payments.exceptions.product.ProductNotFoundException;
import com.qloudd.payments.exceptions.product.ProductUpdateException;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.ProductRepository;
import com.qloudd.payments.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    private final CustomLogger LOG = new CustomLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, AccountRepository accountRepository) {
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Product create(Product product) throws ProductCreationException {
        LOG.update(Function.PRODUCT_CREATION, product.getName());
        // Validate input
        try {
            LOG.info("Commencing product creation - validating input...");
            new ProductValidator()
                    .using(productRepository, accountRepository)
                    .validate(product, Function.PRODUCT_CREATION);
        } catch (ValidationException e) {
            LOG.warn("Product Creation Failed | Validation | {}", e.getErrorList());
            throw new ProductCreationException(ErrorCode.VALIDATION_FAILED, e.getErrorList(), product);
        } catch (Exception e) {
            LOG.error("Product Creation Failed - Unexpected Error | Validation | {}", e.getMessage());
            e.printStackTrace();
            throw new ProductCreationException(product, ErrorCode.UNEXPECTED_ERROR);
        }
        // Add product configurations

        // persist
        try {
            LOG.info("Persisting product...");
            productRepository.save(product);
        } catch (Exception e) {
            LOG.error("Product Creation Failed - Unexpected Error | Persistence | {}", e.getMessage());
            e.printStackTrace();
            throw new ProductCreationException(product, ErrorCode.UNEXPECTED_ERROR);
        }
        return product;
    }

    @Override
    public Product update(Long productId, Product product) throws ProductUpdateException {
        LOG.update(Function.PRODUCT_UPDATE, product.getName());
        // validate input
        try {
            LOG.info("Validating product creation ...", product);
            new ProductValidator()
                    .using(productRepository, accountRepository)
                    .validate(product, Function.PRODUCT_CREATION);
        } catch (ValidationException e) {
            LOG.warn("Product Update Failed | Validation | {}", e.getMessage());
            throw new ProductUpdateException(ErrorCode.VALIDATION_FAILED, e.getErrorList(), product);
        } catch (Exception e) {
            LOG.error("Product Update Failed - Unexpected Error | Validation | {}", e.getMessage());
            e.printStackTrace();
            throw new ProductUpdateException(ErrorCode.UNEXPECTED_ERROR, product);
        }
        // persist
        try {
            LOG.info("Persisting updated product...");
            productRepository.save(product);
        } catch (Exception e) {
            LOG.warn("Product Update Failed - Unexpected Error | Persistence | {}", e.getMessage());
            e.printStackTrace();
            throw new ProductUpdateException(ErrorCode.UNEXPECTED_ERROR, product);
        }
        return product;
    }

    @Override
    public Product getOne(Long productId) throws ProductNotFoundException {
        LOG.update(Function.PRODUCT_GET_ONE, String.valueOf(productId));
        try {
            LOG.info("Fetching product | id : [{}]", productId);
            Optional<Product> productResult = productRepository.findById(productId);
            return productResult.orElseThrow(() -> new ProductNotFoundException(productId));
        } catch (ProductNotFoundException e) {
            LOG.warn("Product Get Failed - Not Found | {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.error("Product Get Failed - Unexpected Error | {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Product getOne(CommandCode commandCode) throws ProductNotFoundException {
        LOG.update(Function.PRODUCT_GET_ONE, commandCode.getCode());
        try {
            LOG.info("Fetching product | command : [{}]", commandCode);
            Optional<Product> productResult = productRepository.findFirstByNameAndStatusOrderByIdDesc(commandCode.getCode(), Status.ACTIVE);
            return productResult.orElseThrow(() -> new ProductNotFoundException(commandCode.getCode()));
        } catch (ProductNotFoundException e) {
            LOG.warn("Product Get Failed - Not Found | {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.error("Product Get Failed - Unexpected Error | {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Page<Product> get() {
        LOG.update(Function.ACCOUNT_CREATION, "List Products");
        try {
            LOG.info("Listing products | params [ ] ...");
            Pageable pageable = Pageable.unpaged();
            return productRepository.findAll(pageable);
        } catch (Exception e) {
            LOG.error("Product List Failed - Unexpected Error | {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ProductRepository getRepository() {
        return productRepository;
    }
}
