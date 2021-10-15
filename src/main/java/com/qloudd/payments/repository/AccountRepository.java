package com.qloudd.payments.repository;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByAccountNumberAndStatus(String accountNumber, Status status);
    Optional<Account> findByIdAndStatus(Long id, Status status);

    Optional<Account> findByIdOrAccountNumber(Long accountId, String accountNumber);

    Optional<Account> findTopByIdNotNullOrderByIdDesc();
}
