package com.qloudd.payments.repository;

import com.qloudd.payments.entity.AccountingEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingEntryRepository extends JpaRepository<AccountingEntry, Long> {
    
}
