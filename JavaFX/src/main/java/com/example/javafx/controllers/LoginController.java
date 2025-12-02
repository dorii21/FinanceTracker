package com.example.javafx.controllers;

import com.example.javafx.services.UserService;
import javafx.scene.control.Label;

public class LoginController {
    private final Label message;
    private final UserService userService;
    private final Runnable onSuccess;

    public LoginController(UserService userService, Runnable onSuccess, Label message) {
        this.userService = userService;
        this.onSuccess = onSuccess;
        this.message = message;
    }

    public void handleLoginButton(String email, String password) {
        boolean loggedIn = userService.login(email, password);
        if (loggedIn) {
            message.setText("Logged in successfully");
            onSuccess.run();
        } else {
            message.setText("Incorrect username or password");
        }
    }
}
