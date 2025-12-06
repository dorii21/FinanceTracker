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
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
        VBox loginBox = new VBox();
        Text loginLabel = new Text("Login");
        loginLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
        Text email = new Text("Email:");
        Text password = new Text("Password:");
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        loginBox.getChildren().addAll(email, emailField, password, passwordField, loginButton);

        VBox registerBox = new VBox();
        Text registerLabel = new Text("Register");
        registerLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
        Text firstName = new Text("First Name:");
        Text lastName = new Text("Last Name:");
        Text regEmail = new Text("Email:");
        Text regPassword = new Text("Password:");
        TextField lastNameField = new TextField();
        TextField firstNameField = new TextField();
        TextField registerEmail = new TextField();
        PasswordField registerPassword = new PasswordField();
        Button registerButton = new Button("Register");
        registerBox.getChildren().addAll(firstName, firstNameField, lastName, lastNameField, regEmail, registerEmail, regPassword, registerPassword, registerButton);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(loginLabel, 0, 0);
        gridPane.add(registerLabel, 1, 0);
        gridPane.add(loginBox, 0, 1);
        gridPane.add(registerBox, 1, 1);

        Label messageLabel = new Label();

        LoginController loginController = new LoginController(userService, messageLabel);
        loginButton.setOnAction(e -> {
            boolean loggedIn = loginController.handleLoginButton(emailField.getText(), passwordField.getText());
            if (loggedIn) {
                transactionView();
            }
        });
        registerButton.setOnAction(e -> {
            loginController.handleRegisterButton(registerEmail.getText(), registerPassword.getText(), firstNameField.getText(), lastNameField.getText());
        });
        Scene scene = new Scene(gridPane, 350, 300);
        primaryStage.setScene(scene);
    }

    private void transactionView() {
        //Buttons
        Button add = new Button("+");
        Button ok = new Button("OK");
        Button export = new Button("Export");

        //Texts
        Text title = new Text("TRANSACTIONS");
        title.setStyle("-fx-font-weight: bold;-fx-font-size: 18px;-fx-font-family:'Montserrat';");
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
        categoryFilter.setPrefWidth(200);
        HBox amountFilter = new HBox(5);
        amountFilter.getChildren().addAll(new Label("min:"), minAmount, new Label("max:"), maxAmount);
        amountFilter.setAlignment(Pos.CENTER_LEFT);
        HBox dateFilter = new HBox();
        dateFilter.getChildren().addAll(new Label("min:"), minDate, new Label("max:"), maxDate);
        dateFilter.setAlignment(Pos.CENTER_LEFT);
        VBox filter = new VBox(10);
        filter.setStyle("-fx-background-color: #D1E5F4;-fx-padding: 10;-fx-border-radius: 5;-fx-background-radius: 5;-fx-border-color: #9ABDDC;-fx-padding: 10;");
        filter.setMaxHeight(Region.USE_PREF_SIZE);

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
        listView.setStyle("-fx-focus-color: transparent;-fx-faint-focus-color: transparent;");

        HelloController controller = new HelloController(amountFilter, dateFilter, categoryFilter, ok, transactionService, transactions);
        transactions.setAll(transactionService.listTransactions());

        listView.setCellFactory(l -> new CustomCell());
        listView.setOnMouseClicked(event -> {
            TransactionDTO selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && event.getClickCount() == 2) {
                controller.editTransaction(selected);
            }
        });

        add.setOnAction(e -> {
            controller.createTransaction();
        });

        export.setOnAction(e -> {
            if (transactionService.csvExport(transactions)) {
                controller.successfulExport();
            }
        });

        //Filter by choicebox
        ChoiceBox<String> filters = new ChoiceBox<String>();
        filters.setPrefWidth(200);
        filters.getItems().addAll("Category", "Amount", "Date", "Expense only", "Income only", "Show all");

        filter.getChildren().addAll(filterBy, filters, categoryFilter, amountFilter, dateFilter, ok);

        filters.setOnAction(e -> {
            controller.choiceBoxSelection(filters.getValue());
        });

        ok.setOnAction(e -> {
            if (filters.getValue().equals("Category")) {
                transactions.setAll(transactionService.filterByCategory(categoryFilter.getValue()));
            } else if (filters.getValue().equals("Amount")) {
                transactions.setAll(transactionService.filterByAmount(Long.valueOf(minAmount.getText()), Long.valueOf(maxAmount.getText())));
            } else if (filters.getValue().equals("Date")) {
                transactions.setAll(transactionService.filterByDate(minDate.getValue(), maxDate.getValue()));
            } else if (filters.getValue().equals("Expense only")) {
                transactions.setAll(transactionService.filterByExpense());
            } else if (filters.getValue().equals("Income only")) {
                transactions.setAll(transactionService.filterByIncome());
            } else {
                transactions.setAll(transactionService.listTransactions());
            }
        });

        //Left side
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(export, add);

        //Alignment
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(700, 500);
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(10);
        gridPane.setHgap(20);
        gridPane.setAlignment(Pos.TOP_CENTER);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.NEVER);
        column1.setPercentWidth(35);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.NEVER);
        column2.setPercentWidth(65);
        gridPane.getColumnConstraints().addAll(column1, column2);

        gridPane.add(title, 0, 0);
        gridPane.add(filter, 1, 1);//column row
        gridPane.add(listView, 0, 1);
        GridPane.setValignment(filter, VPos.TOP);
        gridPane.add(hbox, 1, 0);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        gridPane.setStyle("-fx-background-color: #E5F3FD;");

        Scene scene = new Scene(gridPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

