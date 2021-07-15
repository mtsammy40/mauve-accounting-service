package com.qloudd.payments.repository;

import com.qloudd.payments.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
