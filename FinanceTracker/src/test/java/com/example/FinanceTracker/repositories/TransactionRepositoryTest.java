package com.example.FinanceTracker.repositories;

import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.TransactionType;
import com.example.FinanceTracker.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TestEntityManager testEntityManager;
    private UserEntity user1;
    private UserEntity user2;
    private TransactionEntity transaction1;
    private TransactionEntity transaction2;
    private TransactionEntity transaction3;
    private TransactionEntity transaction4;

    @BeforeEach
    void setup() {
        user1 = testEntityManager.persist(new UserEntity(null, "full", "name", "xy@gmail.com", "pass", null));
        user2 = testEntityManager.persist(new UserEntity(null, "other", "user", "yz@gmail.com", "pass2", null));
        transaction1 = new TransactionEntity(null, TransactionType.EXPENSE, 3000L, LocalDate.of(2025, 11, 20), Category.RESTAURANT, "ebed", user1);
        transaction2 = new TransactionEntity(null, TransactionType.INCOME, 500000L, LocalDate.of(2025, 12, 1), null, "fizetes", user1);
        transaction3 = new TransactionEntity(null, TransactionType.EXPENSE, 4000L, LocalDate.of(2025, 11, 20), Category.CLOTHING, "ebed", user2);
        transaction4 = new TransactionEntity(null, TransactionType.INCOME, 800000L, LocalDate.of(2025, 12, 1), null, "fizetes", user2);
        testEntityManager.persist(transaction1);
        testEntityManager.persist(transaction2);
        testEntityManager.persist(transaction3);
        testEntityManager.persist(transaction4);
        testEntityManager.flush();
    }

    @Test
    void findByIdAndUserIdTest_Success() {
        Optional<TransactionEntity> transactionEntities = transactionRepository.findByIdAndUserId(transaction1.getId(), user1.getId());
        assertTrue(transactionEntities.isPresent());
        assertEquals(transactionEntities.get().getUser().getId(), user1.getId());
        assertEquals(transactionEntities.get().getId(), transaction1.getId());
    }

    @Test
    void findByIdAndUserIdTest_Failure() {
        Optional<TransactionEntity> transactionEntities = transactionRepository.findByIdAndUserId(transaction2.getId(), user2.getId());
        assertTrue(transactionEntities.isEmpty());
    }

    @Test
    void findByCategoryAndUserIdTest_Success() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByCategoryAndUserId(transaction1.getCategory(), user1.getId());
        assertEquals(1, transactionEntities.size());
        assertEquals(transactionEntities.get(0).getUser().getId(), user1.getId());
        assertEquals(transactionEntities.get(0).getCategory(), transaction1.getCategory());
    }

    @Test
    void findByCategoryAndUserIdTest_EmptyList() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByCategoryAndUserId(transaction1.getCategory(), user2.getId());
        assertTrue(transactionEntities.isEmpty());
    }

    @Test
    void findByCategoryAndUserIdTest_EmptyList2() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByCategoryAndUserId(Category.EDUCATION, user2.getId());
        assertTrue(transactionEntities.isEmpty());
    }

    @Test
    void findByAmountBetweenAndUserIdTest_Success() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByAmountBetweenAndUserId(transaction1.getAmount() - 1L, transaction1.getAmount() + 1L, user1.getId());
        assertEquals(1, transactionEntities.size());
        assertEquals(transactionEntities.get(0).getUser().getId(), user1.getId());
        assertEquals(transactionEntities.get(0).getAmount(), transaction1.getAmount());
    }

    @Test
    void findByAmountBetweenAndUserIdTest_EmptyList() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByAmountBetweenAndUserId(transaction1.getAmount() + 1L, transaction1.getAmount() + 2L, user1.getId());
        assertTrue(transactionEntities.isEmpty());
    }

    @Test
    void findByAmountBetweenAndUserIdTest_Bounds() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByAmountBetweenAndUserId(transaction1.getAmount(), transaction1.getAmount() + 1L, user1.getId());
        assertEquals(1, transactionEntities.size());
        assertEquals(transactionEntities.get(0).getUser().getId(), user1.getId());
        assertEquals(transactionEntities.get(0).getAmount(), transaction1.getAmount());
    }

    @Test
    void findByDateBetweenAndUserIdTest_Success() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByDateBetweenAndUserId(transaction1.getDate().minusDays(1), transaction1.getDate().plusDays(1), user1.getId());
        assertEquals(1, transactionEntities.size());
        assertEquals(transactionEntities.get(0).getUser().getId(), user1.getId());
        assertEquals(transactionEntities.get(0).getDate(), transaction1.getDate());
    }

    @Test
    void findByDateBetweenAndUserIdTest_EmptyList() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByDateBetweenAndUserId(transaction1.getDate().minusDays(2), transaction1.getDate().minusDays(1), user1.getId());
        assertTrue(transactionEntities.isEmpty());
    }

    @Test
    void findByDateBetweenAndUserIdTest_Bounds() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByDateBetweenAndUserId(transaction1.getDate(), transaction1.getDate().plusDays(1), user1.getId());
        assertEquals(1, transactionEntities.size());
        assertEquals(transactionEntities.get(0).getUser().getId(), user1.getId());
        assertEquals(transactionEntities.get(0).getDate(), transaction1.getDate());
    }

    @Test
    void findByTypeAndUserIdTest_Success() {
        List<TransactionEntity> transactions = transactionRepository.findByTypeAndUserId(transaction1.getType(), user1.getId());
        assertEquals(1, transactions.size());
        assertEquals(transactions.get(0).getUser().getId(), user1.getId());
        assertEquals(transactions.get(0).getType(), transaction1.getType());
    }

    @Test
    void findByUserIdTest_Success() {
        List<TransactionEntity> transactions = transactionRepository.findByUserId(user1.getId());
        assertEquals(2, transactions.size());
        assertEquals(transactions.get(0).getUser().getId(), user1.getId());
        assertEquals(transactions.get(1).getUser().getId(), user1.getId());
    }

    @Test
    void findByUserIdTest_EmptyList() {
        List<TransactionEntity> transactions = transactionRepository.findByUserId(0L);
        assertTrue(transactions.isEmpty());
    }
}
