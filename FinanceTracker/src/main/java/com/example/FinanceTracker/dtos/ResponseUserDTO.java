package com.example.FinanceTracker.dtos;

import lombok.Data;

@Data
public class ResponseUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
