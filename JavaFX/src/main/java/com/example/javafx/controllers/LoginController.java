package com.example.javafx.controllers;

import com.example.javafx.models.UserDTO;
import com.example.javafx.services.UserService;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;

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
            message.setText("Incorrect email or password");
            showAlert(Alert.AlertType.ERROR, message.getText());
        }
    }

    public void handleRegisterButton(String email, String password, String firstName, String lastName) {
        UserDTO userDTO = new UserDTO(email, password, firstName, lastName);
        try{
            userService.register(userDTO);
            message.setText("User registered successfully");
            onSuccess.run();
        }catch(IOException e){
            Alert.AlertType alertType;
            if(e.getMessage().equals("Username already exists")){
                alertType = Alert.AlertType.WARNING;
            }else{
                alertType = Alert.AlertType.ERROR;
            }
            message.setText(e.getMessage());
            showAlert(alertType,message.getText());
        }catch(InterruptedException e){
            message.setText(e.getMessage());
            showAlert(Alert.AlertType.ERROR,message.getText());
        }
    }

    private void showAlert(Alert.AlertType alertType,String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
