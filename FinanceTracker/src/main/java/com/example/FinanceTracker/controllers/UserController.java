package com.example.FinanceTracker.controllers;

import com.example.FinanceTracker.dtos.ResponseUserDTO;
import com.example.FinanceTracker.dtos.UserDTO;
import com.example.FinanceTracker.exceptions.UserAlreadyExistsException;
import com.example.FinanceTracker.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/register")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    private ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        try {
            ResponseUserDTO registeredUser = userService.register(userDTO);
            return ResponseEntity.ok(registeredUser);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
