package com.example.FinanceTracker.repositories;

import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByIdAndUserId(Long id, Long userId);
    List<TransactionEntity> findByCategoryAndUserId(Category category,Long userId);
    List<TransactionEntity> findByAmountBetweenAndUserId(int min, int max, Long userId);

    List<TransactionEntity> findByDateBetweenAndUserId(LocalDate dateAfter, LocalDate dateBefore, Long userId);
    List<TransactionEntity> findByTypeAndUserId(TransactionType type,Long userId);
    List<TransactionEntity> findByUserId(Long userId);
}
