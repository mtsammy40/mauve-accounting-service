package com.qloudd.payments.service;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.exceptions.ProductCreationException;
import com.qloudd.payments.exceptions.ProductNotFoundException;
import com.qloudd.payments.exceptions.ProductUpdateException;
import com.qloudd.payments.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ProductService {
    Product create(Product product) throws ProductCreationException;
    Product update(Long productId, Product product) throws ProductUpdateException;
    Product getOne(Long productId) throws ProductNotFoundException;
    Page<Product> get();
    ProductRepository getRepository();
}
