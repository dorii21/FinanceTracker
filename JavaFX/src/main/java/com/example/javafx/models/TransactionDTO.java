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

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
