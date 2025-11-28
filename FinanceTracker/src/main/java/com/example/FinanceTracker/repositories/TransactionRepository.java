package com.example.FinanceTracker.repositories;

import com.example.FinanceTracker.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
