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

/**
 * Service class for managing financial transactions
 * Handles business logic related to fetching, creating, updating and deleting transactions, ensuring that they belong to the currently authenticated user
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

    /**
     * Retrieves all transactions of the currently logged-in user
     *
     * @return A list of TransactionDTOs for the user
     * @throws UsernameNotFoundException if the autheticated user's email does not match an existing user
     */
    public List<TransactionDTO> listTransactions() {
        //get ID of current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long id = user.getId();

        List<TransactionEntity> transactionEntities = transactionRepository.findByUserId(id);
        return transactionMapper.toDTOs(transactionEntities);
    }

    /**
     * Retrieves a TransactionDTO by its ID, ensuring it belongs to the currently logged-in user
     *
     * @param id The ID of the transaction to retrieve
     * @return The corresponding TransactionDTO
     * @throws UsernameNotFoundException    if the authenticated user's email does not match an existing user
     * @throws TransactionNotFoundException if the transaction ID does not exist or does not belong to the current user
     */
    public TransactionDTO getTransaction(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        TransactionEntity transactionEntity = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return transactionMapper.toDTO(transactionEntity);
    }

    /**
     * Creates a new transaction for the currently logged-in user
     * If the transaction type is INCOME, the category is explicitly set to null.
     *
     * @param transactionDTO The TransactionDTO containing data for the new transaction
     * @return The created transactionDTO with generated ID
     * @throws UsernameNotFoundException if the authenticated user's email does not match an existing user
     */
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(transactionDTO);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        transactionEntity.setUser(user);
        if (transactionEntity.getType().equals(TransactionType.INCOME)) transactionEntity.setCategory(null);
        TransactionEntity newTransactionEntity = transactionRepository.save(transactionEntity);
        return transactionMapper.toDTO(newTransactionEntity);
    }

    /**
     * Modifies an existing transaction, ensuring it belongs to the currently logged-in user.
     *
     * @param id             The ID of the transaction to modify
     * @param transactionDTO The TransactionDTO containing the updated data
     * @return The updated TransactionDTO
     * @throws UsernameNotFoundException    if the authenticated user's email does not match an existing user
     * @throws TransactionNotFoundException if the transaction ID does not exist or does not belong to the current user
     *
     */
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

    /**
     * Deletes a transaction by its ID, ensuring it belongs to the currently logged-in user.
     *
     * @param id The ID of the transaction to delete
     * @throws UsernameNotFoundException    if the authenticated user's email does not match an existing user
     * @throws TransactionNotFoundException if the transaction ID does not exist or does not belong to the current user
     */
    public void deleteTransaction(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        transactionRepository.deleteById(id);
    }

    /**
     * Filters transactions by a specific category for the currently logged-in user.
     *
     * @param category The Category enum to filter by
     * @return A list of matching TransactionDTOs
     * @throws UsernameNotFoundException if the authenticated user's email does not match an existing user
     */
    public List<TransactionDTO> filterByCategory(Category category) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactions = transactionRepository.findByCategoryAndUserId(category, userId);
        return transactionMapper.toDTOs(transactions);
    }

    /**
     * Filters transactions based on a range of amount values for the currently logged-in user
     *
     * @param min The minimum amount (inclusive)
     * @param max The maximum amount (inclusive)
     * @return A list of matching TransactionDTOs
     * @throws UsernameNotFoundException if the authenticated user's email does not match an existing user
     */
    public List<TransactionDTO> filterByAmountBetween(Long min, Long max) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactions = transactionRepository.findByAmountBetweenAndUserId(min, max, userId);
        return transactionMapper.toDTOs(transactions);
    }

    /**
     * Filters transactions based on a date range for the currently logged-in user
     *
     * @param min The start date (inclusive)
     * @param max The end date (inclusive)
     * @return A list of matching TransactionDTOs
     * @throws UsernameNotFoundException if the authenticated user's email does not match an existing user
     */
    public List<TransactionDTO> filterByDateBetween(LocalDate min, LocalDate max) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactions = transactionRepository.findByDateBetweenAndUserId(min, max, userId);
        return transactionMapper.toDTOs(transactions);
    }

    /**
     * Retrieves all transactions of type EXPENSE for the currently logged-in user.
     *
     * @return A list of TransactionDTOs representing expenses
     * @throws UsernameNotFoundException if the authenticated user's email does not match an existing user
     */
    public List<TransactionDTO> getExpenses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByTypeAndUserId(TransactionType.EXPENSE, userId);
        return transactionMapper.toDTOs(transactionEntities);
    }

    /**
     * Retrieves all transactions of type INCOME for the currently logged-in user.
     *
     * @return A list of TransactionDTOs representing income
     * @throws UsernameNotFoundException if the authenticated user's email does not match an existing user
     */
    public List<TransactionDTO> getIncome() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long userId = user.getId();
        List<TransactionEntity> transactionEntities = transactionRepository.findByTypeAndUserId(TransactionType.INCOME, userId);
        return transactionMapper.toDTOs(transactionEntities);
    }

    /**
     * Generates CSV content from a list of TransactionDTOs, handling UTF-8 encoding
     * Ensures proper CSV formatting
     *
     * @param transactions The list of TransactionDTOs to convert
     * @return A ByteArrayResource containing the CSV data as bytes
     */
    public ByteArrayResource CSVcontent(List<TransactionDTO> transactions) {
        StringBuilder content = new StringBuilder();
        content.append("Type,Amount,Date,Category,Comment\n");
        for (TransactionDTO transactionDTO : transactions) {
            String comment = transactionDTO.getComment();
            if (comment == null) {
                comment = "";
            }
            //Quote the field if it contains ',', '\n' or '\r'
            //Escape internal double quotes by doubling them
            if (comment.contains(",") || comment.contains("\n") || comment.contains("\r") || comment.contains("\"")) {
                comment = "\"" + comment.replace("\"", "\"\"") + "\"";
            }
            String type = transactionDTO.getType() != null ? transactionDTO.getType().toString() : "";
            String amount = String.valueOf(transactionDTO.getAmount());
            String date = transactionDTO.getDate() != null ? transactionDTO.getDate().toString() : "";
            String category = transactionDTO.getCategory() != null ? transactionDTO.getCategory().toString() : "";
            String line = String.join(",", type, amount, date, category, comment);
            content.append(line).append("\n");
        }
        //convert content to bytes using UTF-8
        byte[] csv = content.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(csv);
    }
}
