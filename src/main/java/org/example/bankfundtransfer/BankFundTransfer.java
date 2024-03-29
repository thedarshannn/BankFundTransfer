package org.example.bankfundtransfer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class BankFundTransfer extends Application {

    private TextField sourceAccountField;
    private TextField amountField;
    private TextField targetAccountField;
    private TextArea accountsTextArea;

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

        Scene scene = new Scene(null, 320, 240);
        stage.setTitle("Bank Fund Transfer Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}