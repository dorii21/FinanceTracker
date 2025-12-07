package com.example.javafx.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginView {
    private final GridPane gridPane;
    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Register");
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField registerEmail = new TextField();
    private final PasswordField registerPassword = new PasswordField();

    public LoginView() {
        VBox loginBox = new VBox(5);
        Text loginLabel = new Text("Login");
        loginLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
        loginBox.getChildren().addAll(new Text("Email:"), emailField, new Text("Password:"), passwordField, loginButton);

        VBox registerBox = new VBox(5);
        Text registerLabel = new Text("Register");
        registerLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
        registerBox.getChildren().addAll(new Text("First Name:"), firstNameField, new Text("Last Name:"), lastNameField, new Text("Email:"), registerEmail, new Text("Password:"), registerPassword, registerButton);

        this.gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(loginLabel, 0, 0);
        gridPane.add(registerLabel, 1, 0);
        gridPane.add(loginBox, 0, 1);
        gridPane.add(registerBox, 1, 1);

    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Button getRegisterButton() {
        return registerButton;
    }

    public TextField getFirstNameField() {
        return firstNameField;
    }

    public TextField getLastNameField() {
        return lastNameField;
    }

    public TextField getRegisterEmail() {
        return registerEmail;
    }

    public PasswordField getRegisterPassword() {
        return registerPassword;
    }

    public TextField getEmailField() {
        return emailField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }
}
