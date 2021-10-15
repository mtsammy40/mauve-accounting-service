package com.qloudd.payments.repository;

import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findFirstByNameAndStatusOrderByIdDesc(String name, Status status);
}
