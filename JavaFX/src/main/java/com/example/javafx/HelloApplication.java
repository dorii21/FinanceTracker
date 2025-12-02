package com.example.javafx;

import com.example.javafx.controllers.HelloController;
import com.example.javafx.models.Category;
import com.example.javafx.models.Transaction;
import com.example.javafx.services.TransactionService;
import com.example.javafx.view.CustomCell;
import javafx.application.Application;
import javafx.collections.FXCollections;
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

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {


        //Buttons
        Button add = new Button("+");
        Button ok = new Button("OK");

        //Texts
        Text title = new Text("TRANSACTIONS");
        Text filterBy = new Text("Filter by");

        //Filter by textfield/datepicker
        TextField minAmount = new TextField();
        TextField maxAmount = new TextField();
        DatePicker minDate = new DatePicker();
        DatePicker maxDate = new DatePicker();

        //Box for filters
        ChoiceBox<Category> categoryFilter=new ChoiceBox<>();
        categoryFilter.getItems().addAll(Category.GROCERIES,
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
        HBox amountFilter = new HBox();
        amountFilter.getChildren().addAll(new Label("min:"), minAmount, new Label("max:"), maxAmount);
        HBox dateFilter = new HBox();
        dateFilter.getChildren().addAll(new Label("min:"), minDate, new Label("max:"), maxDate);
        VBox filter = new VBox(10);
        filter.setStyle("-fx-background-color: #D3D3D3;");
        amountFilter.setVisible(false);
        amountFilter.setManaged(false);
        dateFilter.setVisible(false);
        dateFilter.setManaged(false);
        categoryFilter.setVisible(false);
        categoryFilter.setManaged(false);
        ok.setVisible(false);
        ok.setManaged(false);

        //Transactions
        TransactionService transactionService = new TransactionService();
        HelloController controller=new HelloController(amountFilter,dateFilter,categoryFilter,ok,transactionService);
        ListView<Transaction> listView = new ListView<>();
        listView.setItems(transactionService.getTransactions());
        listView.setCellFactory(l -> new CustomCell());
        listView.setOnMouseClicked(event -> {
            controller.editTransaction(listView.getSelectionModel().getSelectedItem().getId());
        });

        add.setOnAction(e -> {controller.createTransaction();});

        //Filter by choicebox
        ChoiceBox<String> filters = new ChoiceBox<String>();
        filters.setPrefWidth(200);
        filters.getItems().addAll("Category", "Amount", "Date", "Expense only", "Income only");

        filter.getChildren().addAll(filterBy, filters, categoryFilter, amountFilter, dateFilter, ok);

        filters.setOnAction(e -> {
            controller.choiceBoxSelection(filters.getValue());
        });

        //Left side
        HBox header = new HBox(140);
        header.getChildren().addAll(title, add);
        VBox box = new VBox();
        box.getChildren().addAll(header, listView);

        //Alignment
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(500, 500);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(20);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(box, 0, 0);
        gridPane.add(filter, 1, 0);

        Scene scene = new Scene(gridPane, 700, 500);
        stage.setScene(scene);
        stage.setTitle("Finance Tracker");
        stage.show();
    }


}
