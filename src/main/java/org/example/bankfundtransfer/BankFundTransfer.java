package org.example.bankfundtransfer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class BankFundTransfer extends Application {

    private TextField sourceAccountField;
    private TextField amountField;
    private TextField targetAccountField;
    private TextArea accountsTextArea;
    private DBConnectivity dbConnectivity = new DBConnectivity();

    @Override
    public void start(Stage stage) throws IOException {

        Label sourceAccountLabel = new Label("Source Account: ");
        sourceAccountField = new TextField();

        Label amountLabel = new Label("Amount: ");
        amountField = new TextField();

        Label targetAccountLabel = new Label("Target Account: ");
        targetAccountField = new TextField();

        Button submitBtn = new Button("Submit");
        Button showAccountsBtn = new Button("Show Accounts");

        accountsTextArea = new TextArea();
        accountsTextArea.setEditable(false);
        accountsTextArea.setPrefHeight(200);


        GridPane grid = new GridPane();
        grid.setMinSize(200, 180);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER);

        grid.add(sourceAccountLabel, 0, 0);
        grid.add(sourceAccountField, 1, 0);
        grid.add(amountLabel, 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(targetAccountLabel, 0, 2);
        grid.add(targetAccountField, 1, 2);

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(submitBtn, showAccountsBtn);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 3, 2, 1);
        grid.add(accountsTextArea, 0, 4, 2, 1);

        // Create a new DB connection and create a table, and insert data from a file
        try {
            dbConnectivity.DBConnection();
            dbConnectivity.createTable();
            dbConnectivity.insertDataThroughFile("accounts.txt");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Transfer funds from source account to target account
        submitBtn.setOnAction(e -> {
            try {
                int sourceAccount = Integer.parseInt(sourceAccountField.getText());
                double amount = Double.parseDouble(amountField.getText());
                int targetAccount = Integer.parseInt(targetAccountField.getText());


                if (sourceAccount <= 0 || targetAccount <= 0 || amount <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input. Please enter positive numbers.");
                    alert.show();
                } else {
                    dbConnectivity.transferFunds(sourceAccount, amount, targetAccount);
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input.");
                alert.show();
            }
        });

        // Show all accounts
        showAccountsBtn.setOnAction(e -> {
            dbConnectivity.showAccounts(accountsTextArea);
        });

        //close the connection
        stage.setOnCloseRequest(e -> {
            try {
                dbConnectivity.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        Scene scene = new Scene(grid, 400, 400);
        stage.setTitle("Bank Fund Transfer Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}