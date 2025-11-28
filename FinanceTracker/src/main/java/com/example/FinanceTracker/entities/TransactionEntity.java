package com.example.FinanceTracker.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity(name="TRANSACTION")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column
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
