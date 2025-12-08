package com.example.javafx.controllers;

import com.example.javafx.models.Category;
import com.example.javafx.models.TransactionDTO;
import com.example.javafx.models.TransactionType;
import com.example.javafx.services.TransactionService;
import com.example.javafx.view.TransactionView;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TransactionController {
    private final ObservableList<TransactionDTO> transactions;
    private final TransactionService transactionService;
    private final TextField minAmountField;
    private final TextField maxAmountField;
    private final DatePicker minDateField;
    private final DatePicker maxDateField;
    private final ChoiceBox<Category> categoryFilter;
    private final Button ok;
    private final Button add;
    private final Button export;
    private final ListView<TransactionDTO> listView;
    private final ChoiceBox<String> filters;
    private final HBox amountFilter;
    private final HBox dateFilter;

    public TransactionController(TransactionService transactionService, TransactionView transactionView) {
        this.transactionService = transactionService;
        this.transactions = transactionView.getTransactionList();
        this.categoryFilter = transactionView.getCategoryFilter();
        this.ok = transactionView.getOk();
        this.add = transactionView.getAdd();
        this.export = transactionView.getExport();
        this.listView = transactionView.getListView();
        this.filters = transactionView.getFilters();
        this.amountFilter = transactionView.getAmountFilter();
        this.dateFilter = transactionView.getDateFilter();
        this.minAmountField = (TextField) amountFilter.getChildren().get(1);
        this.maxAmountField = (TextField) amountFilter.getChildren().get(3);
        this.minDateField = (DatePicker) dateFilter.getChildren().get(1);
        this.maxDateField = (DatePicker) dateFilter.getChildren().get(3);
        transactions.setAll(transactionService.listTransactions());

        //the input fields are dynamically displayed based on the selection of the filter choicebox
        filters.setOnAction(event -> {
            choiceBoxSelection(filters.getValue());
        });

        //apply filter
        ok.setOnAction(event -> {
            if (filters.getValue().equals("Category")) {
                transactions.setAll(transactionService.filterByCategory(categoryFilter.getValue()));
            } else if (filters.getValue().equals("Amount")) {
                //check if the value of the amount fields are non-negative integers
                if (!minAmountField.getText().matches("\\d+") || !maxAmountField.getText().matches("\\d+")) {
                    showAlert(Alert.AlertType.ERROR, "Please enter a valid amount");
                } else {
                    transactions.setAll(transactionService.filterByAmount(Long.valueOf(minAmountField.getText()), Long.valueOf(maxAmountField.getText())));
                }
            } else if (filters.getValue().equals("Date")) {
                transactions.setAll(transactionService.filterByDate(minDateField.getValue(), maxDateField.getValue()));
            } else if (filters.getValue().equals("Expense only")) {
                transactions.setAll(transactionService.filterByExpense());
            } else if (filters.getValue().equals("Income only")) {
                transactions.setAll(transactionService.filterByIncome());
            } else {
                transactions.setAll(transactionService.listTransactions());
            }
        });

        //create new transaction
        add.setOnAction(event -> {
            createTransaction();
        });

        //export the displayed transactions to csv
        export.setOnAction(event -> {
            if (transactionService.csvExport(transactions)) {
                successfulExport();
            }
        });

        //edit the selected transaction (via double-click)
        listView.setOnMouseClicked(event -> {
            TransactionDTO selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && event.getClickCount() == 2) {
                editTransaction(selected);
            }
        });
    }

    public void choiceBoxSelection(String choice) {
        switch (choice) {
            //display a choicebox for the selection of category to filter by
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
            //display 2 datepickers to set the starting and end date
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
            //display 2 textfields to set the minimum and maximum amount
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
            //hide the conditional input fields in case nothing or "show all" is selected
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

    public void editTransaction(TransactionDTO transaction) {
        //open an edit window and display input fields to update the transaction
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

        if (transaction.getType().equals(TransactionType.INCOME)) categoryField.setDisable(true);

        //update the transaction based on the given values
        save.setOnAction(event -> {
            if (!amountField.getText().isBlank() && amountField.getText().matches("\\d+")) {
                transaction.setAmount(Long.valueOf(amountField.getText()));
                transaction.setCategory(categoryField.getValue());
                transaction.setDate(dateField.getValue());
                transaction.setComment(commentField.getText());
                transaction.setType(transaction.getType());
                transactionService.updateTransaction(transaction);
                transactions.setAll(transactionService.listTransactions());
                newStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid amount");
            }
        });

        //delete the transaction
        delete.setOnAction(event -> {
            transactionService.deleteTransaction(transaction);
            transactions.setAll(transactionService.listTransactions());
            newStage.close();
        });

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));
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
        Scene scene = new Scene(gridPane, 300, 200);
        newStage.setTitle(transaction.getType().toString());
        newStage.setScene(scene);
        newStage.show();
    }

    //similar to editTransaction
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
        categoryField.setDisable(true);
        categoryField.setValue(null);
        TextField commentField = new TextField();
        ChoiceBox<TransactionType> typeField = new ChoiceBox<>();
        typeField.getItems().addAll(TransactionType.EXPENSE, TransactionType.INCOME);
        typeField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(TransactionType.EXPENSE)) {
                categoryField.setDisable(false);
            } else if (newValue.equals(TransactionType.INCOME)) {
                categoryField.setDisable(true);
                categoryFilter.setValue(null);
            }
        });
        Button createButton = new Button("Create");

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));
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
        Scene scene = new Scene(gridPane, 300, 250);
        Stage stage = new Stage();
        stage.setTitle("Add transaction");
        stage.setScene(scene);

        createButton.setOnAction(event -> {
            if (!amountField.getText().matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Invalid amount");
            } else if (typeField.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Please select a type");
            } else {
                TransactionDTO transactionDTO = new TransactionDTO(typeField.getValue(), Long.valueOf(amountField.getText()), datePicker.getValue(), categoryField.getValue(), commentField.getText());
                transactionService.createTransaction(transactionDTO);
                transactions.setAll(transactionService.listTransactions());
                stage.close();
            }
        });

        stage.show();
    }

    //confirm successful export
    public void successfulExport() {
        Text text = new Text("Transactions successfully exported");
        VBox vBox = new VBox(text);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 200, 50);
        Stage stage = new Stage();
        stage.setTitle("Export successful");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
