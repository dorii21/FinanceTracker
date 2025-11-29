package com.example.FinanceTracker.services;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.TransactionType;
import com.example.FinanceTracker.exceptions.TransactionNotFoundException;
import com.example.FinanceTracker.mappers.TransactionMapper;
import com.example.FinanceTracker.repositories.TransactionRepository;
import com.example.FinanceTracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

    public List<TransactionDTO> listTransactions() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long id = userRepository.findByEmail(email).get().getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByUserId(id);
        return transactionMapper.toDTOs(transactionEntities);
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(transactionDTO);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        transactionEntity.setUser(userRepository.findByEmail(email).get());
        if (transactionEntity.getType().equals(TransactionType.EXPENSE)) transactionEntity.setCategory(null);
        TransactionEntity newTransactionEntity = transactionRepository.save(transactionEntity);
        return transactionMapper.toDTO(newTransactionEntity);
    }

    public TransactionDTO modifyTransaction(Long id, TransactionDTO transactionDTO) {
        transactionDTO.setId(id);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email).get().getId();
        Optional<TransactionEntity> optional = transactionRepository.findByIdAndUserId(transactionDTO.getId(), userId);
        if (optional.isPresent()) {
            TransactionEntity transactionEntity = optional.get();
            transactionMapper.updateTransactionFromDTO(transactionDTO, transactionEntity);
            transactionRepository.save(transactionEntity);
            return transactionMapper.toDTO(transactionEntity);
        } else throw new TransactionNotFoundException("Transaction Not Found");
    }

    public void deleteTransaction(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email).get().getId();
        Optional<TransactionEntity> optional = transactionRepository.findByIdAndUserId(id, userId);
        if (optional.isPresent()) {
            transactionRepository.deleteById(id);
        } else throw new TransactionNotFoundException("Transaction not found");
    }

    public List<TransactionDTO> filterByCategory(Category category) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email).get().getId();
        List<TransactionEntity> transactions = transactionRepository.findByCategoryAndUserId(category, userId);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> filterByAmountBetween(int min, int max) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email).get().getId();
        List<TransactionEntity> transactions = transactionRepository.findByAmountBetweenAndUserId(min, max, userId);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> filterByDateBetween(LocalDate min, LocalDate max) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email).get().getId();
        List<TransactionEntity> transactions = transactionRepository.findByDateBetweenAndUserId(min, max, userId);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> getExpenses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email).get().getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByTypeAndUserId(TransactionType.EXPENSE, userId);
        return transactionMapper.toDTOs(transactionEntities);
    }

    public List<TransactionDTO> getIncome() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email).get().getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByTypeAndUserId(TransactionType.INCOME, userId);
        return transactionMapper.toDTOs(transactionEntities);
    }
}
