package com.example.FinanceTracker.services;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.TransactionType;
import com.example.FinanceTracker.exceptions.TransactionNotFoundException;
import com.example.FinanceTracker.mappers.TransactionMapper;
import com.example.FinanceTracker.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public List<TransactionDTO> listTransactions() {
        List<TransactionEntity> transactionEntities = transactionRepository.findAll();
        return transactionMapper.toDTOs(transactionEntities);
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(transactionDTO);
        TransactionEntity newTransactionEntity = transactionRepository.save(transactionEntity);
        return transactionMapper.toDTO(newTransactionEntity);
    }

    public TransactionDTO modifyTransaction(Long id,TransactionDTO transactionDTO) {
        transactionDTO.setId(id);
        Optional<TransactionEntity> optional = transactionRepository.findById(transactionDTO.getId());
        if(optional.isPresent()) {
            TransactionEntity transactionEntity = optional.get();
            transactionMapper.updateTransactionFromDTO(transactionDTO,transactionEntity);
            transactionRepository.save(transactionEntity);
            return transactionMapper.toDTO(transactionEntity);
        }else throw new TransactionNotFoundException("Transaction Not Found");
    }

    public void deleteTransaction(Long id){
        Optional<TransactionEntity> optional = transactionRepository.findById(id);
        if (optional.isPresent()) {
            transactionRepository.deleteById(id);
        } else throw new TransactionNotFoundException("Transaction not found");
    }

    public List<TransactionDTO> filterByCategory(Category category) {
        List<TransactionEntity> transactions=transactionRepository.findByCategory(category);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> filterByAmountBetween(int min, int max) {
        List<TransactionEntity> transactions=transactionRepository.findByAmountBetween(min, max);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> filterByDateBetween(LocalDate min, LocalDate max) {
        List<TransactionEntity> transactions=transactionRepository.findByDateBetween(min, max);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> getExpenses() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByType(TransactionType.EXPENSE);
        return transactionMapper.toDTOs(transactionEntities);
    }

    public List<TransactionDTO> getIncome() {
        List<TransactionEntity> transactionEntities = transactionRepository.findByType(TransactionType.INCOME);
        return transactionMapper.toDTOs(transactionEntities);
    }
}
