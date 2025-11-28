package com.example.FinanceTracker.repositories;

import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByCategory(Category category);
    List<TransactionEntity> findByAmountBetween(int min, int max);
    List<TransactionEntity> findByDateBetween(LocalDate min, LocalDate max);
    List<TransactionEntity> findByType(TransactionType type);
}
