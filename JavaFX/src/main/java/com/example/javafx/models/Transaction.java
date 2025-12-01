package com.example.javafx.models;

import java.time.LocalDate;

public class Transaction {
    private long id;
    private TransactionType type;
    private Long amount;
    private LocalDate date;
    private Category category;
    private String comment;

    public Transaction(long id, TransactionType type, Long amount, LocalDate date, Category category, String comment) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Long  getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public String getAmountsToString() {
        if (type == TransactionType.EXPENSE) {
            return "-" + amount.toString();
        }
        return "+" + amount.toString();
    }

    public TransactionType getType() {
        return type;
    }
}
