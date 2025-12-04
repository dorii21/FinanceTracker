package com.example.FinanceTracker.services;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.TransactionType;
import com.example.FinanceTracker.entities.UserEntity;
import com.example.FinanceTracker.exceptions.TransactionNotFoundException;
import com.example.FinanceTracker.mappers.TransactionMapper;
import com.example.FinanceTracker.repositories.TransactionRepository;
import com.example.FinanceTracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

    public List<TransactionDTO> listTransactions() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long id = user.getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByUserId(id);
        return transactionMapper.toDTOs(transactionEntities);
    }

    public TransactionDTO getTransaction(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        TransactionEntity transactionEntity = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return transactionMapper.toDTO(transactionEntity);
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(transactionDTO);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        transactionEntity.setUser(user);
        if (transactionEntity.getType().equals(TransactionType.INCOME)) transactionEntity.setCategory(null);
        TransactionEntity newTransactionEntity = transactionRepository.save(transactionEntity);
        return transactionMapper.toDTO(newTransactionEntity);
    }

    public TransactionDTO modifyTransaction(Long id, TransactionDTO transactionDTO) {
        transactionDTO.setId(id);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        TransactionEntity transactionEntity = transactionRepository.findByIdAndUserId(transactionDTO.getId(), userId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        transactionMapper.updateTransactionFromDTO(transactionDTO, transactionEntity);
        transactionRepository.save(transactionEntity);
        return transactionMapper.toDTO(transactionEntity);
    }

    public void deleteTransaction(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        TransactionEntity transactionEntity = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        transactionRepository.deleteById(id);
    }

    public List<TransactionDTO> filterByCategory(Category category) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactions = transactionRepository.findByCategoryAndUserId(category, userId);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> filterByAmountBetween(Long min, Long max) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactions = transactionRepository.findByAmountBetweenAndUserId(min, max, userId);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> filterByDateBetween(LocalDate min, LocalDate max) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactions = transactionRepository.findByDateBetweenAndUserId(min, max, userId);
        return transactionMapper.toDTOs(transactions);
    }

    public List<TransactionDTO> getExpenses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByTypeAndUserId(TransactionType.EXPENSE, userId);
        return transactionMapper.toDTOs(transactionEntities);
    }

    public List<TransactionDTO> getIncome() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByTypeAndUserId(TransactionType.INCOME, userId);
        return transactionMapper.toDTOs(transactionEntities);
    }

    public ByteArrayResource CSVcontent(List<TransactionDTO> transactions) {
        StringBuilder content = new StringBuilder();
        content.append("Type,Amount,Date,Category,Comment\n");
        for (TransactionDTO transactionDTO : transactions) {
            String comment = transactionDTO.getComment();
            if (comment == null) {
                comment = "";
            }
            if (comment.contains(",") || comment.contains("\"") || comment.contains("\n") || comment.contains("\r")) {
                comment = "\"" + comment.replace("\"", "\"\"") + "\"";
            }
            String type = transactionDTO.getType() != null ? transactionDTO.getType().toString() : "";
            String amount = String.valueOf(transactionDTO.getAmount());
            String date = transactionDTO.getDate() != null ? transactionDTO.getDate().toString() : "";
            String category = transactionDTO.getCategory() != null ? transactionDTO.getCategory().toString() : "";
            String line = String.join(",", type, amount, date, category, comment);
            content.append(line).append("\n");
        }
        byte[] csv = content.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(csv);
    }
}
