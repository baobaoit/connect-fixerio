package com.example.demo_fixerio.repository;

import com.example.demo_fixerio.domain.ExchangeRatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRatesRepository extends JpaRepository<ExchangeRatesEntity, String> {
    Optional<ExchangeRatesEntity> findFirst1ByOrderByDateDesc();
}
