package com.example.javafx.controllers;

import com.example.javafx.models.Transaction;
import com.example.javafx.services.TransactionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelloController {
    private HBox amountFilter;
    private HBox dateFilter;
    private TextField categoryFilter;
    private Button ok;
    private TransactionService transactionService;

    public HelloController(HBox amountFilter, HBox dateFilter, TextField categoryFilter, Button ok, TransactionService transactionService) {
        this.amountFilter = amountFilter;
        this.dateFilter = dateFilter;
        this.categoryFilter = categoryFilter;
        this.ok = ok;
        this.transactionService = transactionService;
    }

    public void choiceBoxSelection(String choice) {
        switch (choice) {
            case "Category":
                amountFilter.setVisible(false);
                amountFilter.setManaged(false);
                dateFilter.setVisible(false);
                dateFilter.setManaged(false);
                categoryFilter.setVisible(true);
                categoryFilter.setManaged(true);
                ok.setVisible(true);
                ok.setManaged(true);
                break;
            case "Date":
                amountFilter.setVisible(false);
                amountFilter.setManaged(false);
                dateFilter.setVisible(true);
                dateFilter.setManaged(true);
                categoryFilter.setVisible(false);
                categoryFilter.setManaged(false);
                ok.setVisible(true);
                ok.setManaged(true);
                break;
            case "Amount":
                amountFilter.setVisible(true);
                amountFilter.setManaged(true);
                dateFilter.setVisible(false);
                dateFilter.setManaged(false);
                categoryFilter.setVisible(false);
                categoryFilter.setManaged(false);
                ok.setVisible(true);
                ok.setManaged(true);
                break;
            default:
                amountFilter.setVisible(false);
                amountFilter.setManaged(false);
                dateFilter.setVisible(false);
                dateFilter.setManaged(false);
                categoryFilter.setVisible(false);
                categoryFilter.setManaged(false);
                ok.setVisible(true);
                ok.setManaged(true);
        }
    }

    public void editTransaction(Long id) {
        Transaction transaction = transactionService.findById(id);
        Stage newStage = new Stage();
        Text amount = new Text("Amount:");
        Text date = new Text("Date:");
        Text category = new Text("Category:");
        Text comment = new Text("Comment:");
        Button save = new Button("Save");
        Button delete = new Button("Delete");
        TextField amountField;
        TextField dateField;
        TextField categoryField;
        TextField commentField;
        if (transaction.getAmount() != null) {
            amountField = new TextField(transaction.getAmount().toString());
        } else {
            amountField = new TextField();
        }
        if (transaction.getDate() != null) {
            dateField = new TextField(transaction.getDate().toString());
        }else
            dateField = new TextField();
        if (transaction.getCategory() != null) {
            categoryField = new TextField(transaction.getCategory().toString());
        }else
            categoryField = new TextField();
        if (transaction.getComment() != null) {
            commentField = new TextField(transaction.getComment());
        }else
            commentField = new TextField();

        GridPane gridPane = new GridPane();
        gridPane.add(amount, 0, 0);
        gridPane.add(amountField, 1, 0);
        gridPane.add(date, 0, 1);
        gridPane.add(dateField, 1, 1);
        gridPane.add(category, 0, 2);
        gridPane.add(categoryField, 1, 2);
        gridPane.add(comment, 0, 3);
        gridPane.add(commentField, 1, 3);
        gridPane.add(delete, 0, 4);
        gridPane.add(save, 1, 4);
        Scene scene = new Scene(gridPane, 200, 200);
        newStage.setTitle(transaction.getType().toString());
        newStage.setScene(scene);
        newStage.show();
    }
}
