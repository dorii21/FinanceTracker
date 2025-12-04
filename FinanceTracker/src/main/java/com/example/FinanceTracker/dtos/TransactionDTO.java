package com.example.FinanceTracker.dtos;

import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDTO {
    private Long id;
    private TransactionType type;
    private Long amount;
    private LocalDate date;
    private Category category;
    private String comment;
}
