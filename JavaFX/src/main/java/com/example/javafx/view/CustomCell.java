package com.example.javafx.view;

import com.example.javafx.models.TransactionDTO;
import com.example.javafx.models.TransactionType;
import javafx.scene.control.ListCell;

public class CustomCell extends ListCell<TransactionDTO> {
    @Override
    protected void updateItem(TransactionDTO item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            if (item.getType().equals(TransactionType.EXPENSE)) {
                setText("-" + item.getAmount().toString() +" Ft");
                setStyle("-fx-text-fill: red;-fx-font-weight: bold;-fx-font-size: 12px;-fx-padding: 10px;-fx-background-color: transparent;-fx-focus-color:transparent");
            } else {
                setText("+" + item.getAmount().toString()+" Ft");
                setStyle("-fx-text-fill: green;-fx-font-weight: bold;-fx-font-size: 12px;-fx-padding: 10px;-fx-background-color: transparent;-fx-focus-color:transparent");
            }
            setPrefHeight(40);
        }
    }
}
