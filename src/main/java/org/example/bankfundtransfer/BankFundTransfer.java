package org.example.bankfundtransfer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BankFundTransfer extends Application {
    @Override
    public void start(Stage stage) throws IOException {


        Scene scene = new Scene(null, 320, 240);
        stage.setTitle("Bank Fund Transfer Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}