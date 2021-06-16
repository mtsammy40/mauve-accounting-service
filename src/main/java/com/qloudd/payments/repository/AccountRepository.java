package com.qloudd.payments.repository;

import com.qloudd.payments.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    @Query(value = "SELECT * FROM account order by id desc limit 1", nativeQuery = true )
    Optional<Account> getTopOrderByIdDesc();
}
