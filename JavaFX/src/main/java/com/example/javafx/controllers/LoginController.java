package com.example.javafx.controllers;

import com.example.javafx.models.UserDTO;
import com.example.javafx.services.UserService;
import com.example.javafx.view.LoginView;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginController {
    private final Label message;
    private final UserService userService;

    public LoginController(UserService userService, Label message, LoginView loginView, Runnable onSuccess) {
        this.userService = userService;
        this.message = message;

        //load transaction view in case of correct email and password
        loginView.getLoginButton().setOnAction(event -> {
            boolean loggedIn = handleLoginButton(loginView.getEmailField().getText(), loginView.getPasswordField().getText());
            if (loggedIn) {
                onSuccess.run();
            }
        });

        loginView.getRegisterButton().setOnAction(e -> {
            handleRegisterButton(loginView.getRegisterEmail().getText(), loginView.getRegisterPassword().getText(), loginView.getFirstNameField().getText(), loginView.getLastNameField().getText());
        });
    }

    public boolean handleLoginButton(String email, String password) {
        //pop up warning if email and/or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            String msg = "Email and password cannot be blank";
            message.setText(msg);
            showAlert(Alert.AlertType.WARNING, msg);
            return false;
        }

        boolean loggedIn = userService.login(email, password);

        //pop up error message in case of incorrect user details
        if (!loggedIn) {
            message.setText("Incorrect email or password");
            showAlert(Alert.AlertType.ERROR, message.getText());
        }
        return loggedIn;
    }

    public void handleRegisterButton(String email, String password, String firstName, String lastName) {
        //pop up warning if email and/or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
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
            //pop up warning if user with given email already exists in the database
            Alert.AlertType alertType;
            if (e.getMessage().equals("User already exists")) {
                alertType = Alert.AlertType.WARNING;
            } else {
                //error message in case of other exception
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
