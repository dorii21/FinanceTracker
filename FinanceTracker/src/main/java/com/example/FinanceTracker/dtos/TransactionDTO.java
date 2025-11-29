package com.example.FinanceTracker.dtos;

import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionType;
import com.example.FinanceTracker.entities.UserEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDTO {
    private long id;
    private TransactionType type;
    private Long amount;
    private LocalDate date;
    private Category category;
    private String comment;
}
