package com.example.javafx;

import com.example.javafx.controllers.TransactionController;
import com.example.javafx.controllers.LoginController;
import com.example.javafx.services.TransactionService;
import com.example.javafx.services.UserService;
import com.example.javafx.view.LoginView;
import com.example.javafx.view.TransactionView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private UserService userService;
    private TransactionService transactionService;
    private Stage primaryStage;
    private LoginView loginView;
    private TransactionView transactionView;
    private Label message;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Finance Tracker");
        this.userService = new UserService();
        this.transactionService = new TransactionService(userService);
        this.loginView = new LoginView();
        this.transactionView = new TransactionView();
        this.message = new Label();
        LoginController loginController = new LoginController(userService, message, loginView, this::switchView);
        GridPane gridPane = loginView.getGridPane();
        Scene scene = new Scene(gridPane, 350, 300);
        stage.setScene(scene);
        stage.show();
    }

    public void switchView() {
        Scene transactionScene = new Scene(transactionView.getGridPane(), 900, 600);
        TransactionController transactionController = new TransactionController(transactionService, transactionView);
        primaryStage.setScene(transactionScene);
        primaryStage.show();
    }
}

