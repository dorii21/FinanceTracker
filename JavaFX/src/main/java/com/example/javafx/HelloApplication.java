package com.example.javafx;

import com.example.javafx.controllers.HelloController;
import com.example.javafx.controllers.LoginController;
import com.example.javafx.models.Category;
import com.example.javafx.models.TransactionDTO;
import com.example.javafx.services.TransactionService;
import com.example.javafx.services.UserService;
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

public class HelloApplication extends Application {
    private UserService userService;
    private TransactionService transactionService;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        stage.setTitle("Finance Tracker");
        this.userService = new UserService();
        this.transactionService = new TransactionService(userService);
        loginView();
        stage.show();
    }

    private void loginView() {
        VBox vBox = new VBox();
        Text loginLabel = new Text("Login");
        Text email = new Text("Email:");
        Text password = new Text("Password:");
        TextField emailField = new TextField();
        TextField passwordField = new TextField();
        Button loginButton = new Button("Login");
        vBox.getChildren().addAll(loginLabel, email, emailField, password, passwordField, loginButton);

        Label messageLabel = new Label();

        LoginController loginController = new LoginController(userService, this::transactionView, messageLabel);
        loginButton.setOnAction(e -> {
            loginController.handleLoginButton(emailField.getText(), passwordField.getText());
        });
        Scene scene = new Scene(vBox, 300, 300);
        primaryStage.setScene(scene);
    }

    private void transactionView() {
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
        ChoiceBox<Category> categoryFilter = new ChoiceBox<>();
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

        ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();
        ListView<TransactionDTO> listView = new ListView<>(transactions);
        Runnable refreshList = () -> {
            transactions.setAll(transactionService.listTransactions());
        };

        HelloController controller = new HelloController(amountFilter, dateFilter, categoryFilter, ok, transactionService, refreshList);
        refreshList.run();

        listView.setCellFactory(l -> new CustomCell());
        listView.setOnMouseClicked(event -> {
            TransactionDTO selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && event.getClickCount() == 2) {
                controller.editTransaction(selected.getId());
            }
        });

        add.setOnAction(e -> {
            controller.createTransaction();
        });

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
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

