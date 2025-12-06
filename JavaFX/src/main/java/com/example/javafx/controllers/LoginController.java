package com.example.javafx.controllers;

import com.example.javafx.models.UserDTO;
import com.example.javafx.services.UserService;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginController {
    private final Label message;
    private final UserService userService;

    public LoginController(UserService userService, Label message) {
        this.userService = userService;
        this.message = message;
    }

    public boolean handleLoginButton(String email, String password) {
        boolean loggedIn = userService.login(email, password);
        if (email.isEmpty() || password.isBlank()) {
            String msg = "Email and password cannot be blank";
            message.setText(msg);
            showAlert(Alert.AlertType.WARNING, msg);
            return false;
        }
        userService.login(email, password);
        if (loggedIn) {
            message.setText("Logged in successfully");
        } else {
            message.setText("Incorrect email or password");
            showAlert(Alert.AlertType.ERROR, message.getText());
        }
        return loggedIn;
    }

    public void handleRegisterButton(String email, String password, String firstName, String lastName) {
        if (email.isEmpty() || password.isBlank()) {
            String msg = "Email and password cannot be blank";
            message.setText(msg);
            showAlert(Alert.AlertType.WARNING, msg);
            return;
        }
        UserDTO userDTO = new UserDTO(email, firstName, lastName, password);
        try {
            userService.register(userDTO);
            message.setText("User registered successfully");
            showAlert(Alert.AlertType.INFORMATION, message.getText());
        } catch (IOException e) {
            Alert.AlertType alertType;
            if (e.getMessage().equals("User already exists")) {
                alertType = Alert.AlertType.WARNING;
            } else {
                alertType = Alert.AlertType.ERROR;
            }
            message.setText(e.getMessage());
            showAlert(alertType, message.getText());
        } catch (InterruptedException e) {
            message.setText(e.getMessage());
            showAlert(Alert.AlertType.ERROR, message.getText());
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
