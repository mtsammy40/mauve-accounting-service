package com.qloudd.payments.repository;

import com.qloudd.payments.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {
    Optional<Configuration> findTopByUserId(UUID uuid);
}
