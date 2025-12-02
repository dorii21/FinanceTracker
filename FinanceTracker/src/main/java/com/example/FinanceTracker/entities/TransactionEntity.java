package com.example.FinanceTracker.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity(name = "TRANSACTION")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private Long amount;

    @Column
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    private String comment;

    @ManyToOne
    private UserEntity user;
}
