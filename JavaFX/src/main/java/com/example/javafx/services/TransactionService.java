package com.example.javafx.services;

import com.example.javafx.models.Category;
import com.example.javafx.models.Transaction;
import com.example.javafx.models.TransactionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class TransactionService {
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    public TransactionService() {
        transactions.add(
                new Transaction(1L, TransactionType.EXPENSE, 2000L, LocalDate.of(2025, 11, 1), Category.GROCERIES, "Bevásárlás")
        );
        transactions.add(
                new Transaction(2L, TransactionType.INCOME, 50000L, LocalDate.of(2025, 12, 1), null, "Ösztöndíj")
        );
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction findById(Long id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(id)) {
                return transaction;
            }
        }
        return null;
    }
}
