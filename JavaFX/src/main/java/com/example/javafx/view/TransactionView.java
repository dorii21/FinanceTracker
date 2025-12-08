package com.example.javafx.view;

import com.example.javafx.models.Category;
import com.example.javafx.models.TransactionDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class TransactionView {
    private final GridPane gridPane;
    private final ObservableList<TransactionDTO> transactionList = FXCollections.observableArrayList();
    private final Button add = new Button("+");
    private final Button ok = new Button("OK");
    private final Button export = new Button("Export");
    private final TextField minAmount = new TextField();
    private final TextField maxAmount = new TextField();
    private final DatePicker minDate = new DatePicker();
    private final DatePicker maxDate = new DatePicker();
    private final ChoiceBox<Category> categoryFilter = new ChoiceBox<>();
    private final ChoiceBox<String> filters = new ChoiceBox<>();
    private final HBox amountFilter;
    private final HBox dateFilter;
    private final VBox filter;
    private final ListView<TransactionDTO> listView;

    public TransactionView() {
        Text title = new Text("TRANSACTIONS");
        title.setStyle("-fx-font-weight: bold;-fx-font-size: 18px;-fx-font-family:'Montserrat';");

        Text filterBy = new Text("Filter by");

        //filter criteria choicebox
        filters.setPrefWidth(200);
        filters.getItems().addAll("Category", "Amount", "Date", "Expense only", "Income only", "Show all");

        //input field if category filter is selected
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

        //input fields if amount filter is selected
        amountFilter = new HBox(5);
        amountFilter.getChildren().addAll(new Label("min:"), minAmount, new Label("max:"), maxAmount);
        amountFilter.setAlignment(Pos.CENTER_LEFT);

        //input fields if date filter is selected
        dateFilter = new HBox();
        dateFilter.getChildren().addAll(new Label("min:"), minDate, new Label("max:"), maxDate);
        dateFilter.setAlignment(Pos.CENTER_LEFT);

        //hide input fields by default
        amountFilter.setVisible(false);
        amountFilter.setManaged(false);
        dateFilter.setVisible(false);
        dateFilter.setManaged(false);
        categoryFilter.setVisible(false);
        categoryFilter.setManaged(false);
        ok.setVisible(false);
        ok.setManaged(false);

        //display transactions in a listview and customize the cells
        this.listView = new ListView<>(transactionList);
        listView.setStyle("-fx-focus-color: transparent;-fx-faint-focus-color: transparent;");
        listView.setCellFactory(l -> new CustomCell());

        //vbox for all filter elements
        filter = new VBox(10);
        filter.setStyle("-fx-background-color: #D1E5F4;-fx-padding: 10;-fx-border-radius: 5;-fx-background-radius: 5;-fx-border-color: #9ABDDC;");
        filter.setMaxHeight(Region.USE_PREF_SIZE);
        filter.getChildren().addAll(filterBy, filters, categoryFilter, amountFilter, dateFilter, ok);

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(export, add);

        this.gridPane = new GridPane();
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
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public Button getOk() {
        return ok;
    }

    public Button getAdd() {
        return add;
    }

    public Button getExport() {
        return export;
    }

    public ChoiceBox<String> getFilters() {
        return filters;
    }

    public ListView<TransactionDTO> getListView() {
        return listView;
    }

    public ChoiceBox<Category> getCategoryFilter() {
        return categoryFilter;
    }

    public HBox getAmountFilter() {
        return amountFilter;
    }

    public HBox getDateFilter() {
        return dateFilter;
    }

    public ObservableList<TransactionDTO> getTransactionList() {
        return transactionList;
    }
}
