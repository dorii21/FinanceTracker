package com.example.javafx.models;

import java.time.LocalDate;

public class TransactionDTO {
    private Long id;
    private TransactionType type;
    private Long amount;
    private LocalDate date;
    private Category category;
    private String comment;

    TransactionDTO(Long id, TransactionType type, Long amount, LocalDate date, Category category, String comment) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.comment = comment;
    }

    public TransactionDTO() {
    }

    public TransactionType getType() {
        return type;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public String getComment() {
        return comment;
    }
}
