package com.example.javafx.controllers;

import com.example.javafx.models.Category;
import com.example.javafx.models.TransactionDTO;
import com.example.javafx.models.TransactionType;
import com.example.javafx.services.TransactionService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelloController {
    private HBox amountFilter;
    private HBox dateFilter;
    private ChoiceBox<Category> categoryFilter;
    private Button ok;
    private TransactionService transactionService;
    private final Runnable refreshList;

    public HelloController(HBox amountFilter, HBox dateFilter, ChoiceBox<Category> categoryFilter, Button ok, TransactionService transactionService, Runnable refreshList) {
        this.amountFilter = amountFilter;
        this.dateFilter = dateFilter;
        this.categoryFilter = categoryFilter;
        this.ok = ok;
        this.transactionService = transactionService;
        this.refreshList = refreshList;
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
        TransactionDTO transaction = transactionService.findById(id);
        if (transaction == null) {
            showAlert("Error", "Transaction Not Found");
            return;
        }
        Stage newStage = new Stage();
        Text amount = new Text("Amount:");
        Text date = new Text("Date:");
        Text category = new Text("Category:");
        Text comment = new Text("Comment:");
        Button save = new Button("Save");

        Button delete = new Button("Delete");
        TextField amountField;
        DatePicker dateField;
        ChoiceBox<Category> categoryField = new ChoiceBox<>();
        categoryField.getItems().addAll(Category.GROCERIES,
                Category.HOUSING,
                Category.TRANSPORTATION,
                Category.INSURANCE,
                Category.HEALTH,
                Category.SUBSCRIPTIONS,
                Category.EDUCATION,
                Category.RESTAURANT,
                Category.CLOTHING,
                Category.ENTERTAINMENT,
                Category.TRAVEL,
                Category.HOBBIES,
                Category.GIFT,
                Category.PHONE,
                Category.OTHER);

        TextField commentField;
        if (transaction.getAmount() != null) {
            amountField = new TextField(transaction.getAmount().toString());
        } else {
            amountField = new TextField();
        }
        if (transaction.getDate() != null) {
            dateField = new DatePicker(transaction.getDate());
        } else
            dateField = new DatePicker();
        if (transaction.getCategory() != null) {
            categoryField.setValue(transaction.getCategory());
        }
        if (transaction.getComment() != null) {
            commentField = new TextField(transaction.getComment());
        } else
            commentField = new TextField();


        save.setOnAction(event -> {
            if (!amountField.getText().isBlank()) {
                transaction.setAmount(Long.valueOf(amountField.getText()));
            }
            transaction.setCategory(categoryField.getValue());
            transaction.setDate(dateField.getValue());
            transaction.setComment(commentField.getText());
            transaction.setType(transaction.getType());
            transactionService.updateTransaction(transaction);
            refreshList.run();
            newStage.close();
        });

        delete.setOnAction(event -> {
            transactionService.deleteTransaction(transaction);
            refreshList.run();
            newStage.close();
        });

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void createTransaction() {
        Text amount = new Text("Amount:");
        Text date = new Text("Date:");
        Text category = new Text("Category:");
        Text comment = new Text("Comment:");
        Text type = new Text("Type:");
        TextField amountField = new TextField();
        DatePicker datePicker = new DatePicker();
        ChoiceBox<Category> categoryField = new ChoiceBox<>();
        categoryField.getItems().addAll(Category.GROCERIES,
                Category.HOUSING,
                Category.TRANSPORTATION,
                Category.INSURANCE,
                Category.HEALTH,
                Category.SUBSCRIPTIONS,
                Category.EDUCATION,
                Category.RESTAURANT,
                Category.CLOTHING,
                Category.ENTERTAINMENT,
                Category.TRAVEL,
                Category.HOBBIES,
                Category.GIFT,
                Category.PHONE,
                Category.OTHER);

        TextField commentField = new TextField();
        ChoiceBox<TransactionType> typeField = new ChoiceBox<>();
        typeField.getItems().addAll(TransactionType.EXPENSE, TransactionType.INCOME);
        Button createButton = new Button("Create");

        GridPane gridPane = new GridPane();
        gridPane.add(amount, 0, 0);
        gridPane.add(amountField, 1, 0);
        gridPane.add(date, 0, 1);
        gridPane.add(datePicker, 1, 1);
        gridPane.add(category, 0, 2);
        gridPane.add(categoryField, 1, 2);
        gridPane.add(comment, 0, 3);
        gridPane.add(commentField, 1, 3);
        gridPane.add(type, 0, 4);
        gridPane.add(typeField, 1, 4);
        gridPane.add(createButton, 0, 5);
        Scene scene = new Scene(gridPane, 200, 200);
        Stage stage = new Stage();
        stage.setTitle("Add transaction");
        stage.setScene(scene);

        createButton.setOnAction(event -> {
            TransactionDTO transactionDTO = new TransactionDTO(typeField.getValue(), Long.valueOf(amountField.getText()), datePicker.getValue(), categoryField.getValue(), commentField.getText());
            transactionService.createTransaction(transactionDTO);
            refreshList.run();
            stage.close();
        });

        stage.show();
    }
}
