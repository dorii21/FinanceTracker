package com.example.FinanceTracker.controllers;

import com.example.FinanceTracker.dtos.UserDTO;
import com.example.FinanceTracker.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/register")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    private ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.register(userDTO));
    }
}
