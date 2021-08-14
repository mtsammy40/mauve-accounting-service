package com.qloudd.payments.repository;

import com.qloudd.payments.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
    Optional<AccountType> findByIdAndStatus(Long id, AccountType.Status status);
}
