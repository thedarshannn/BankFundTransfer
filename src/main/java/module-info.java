module org.example.bankfundtransfer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.bankfundtransfer to javafx.fxml;
    exports org.example.bankfundtransfer;
}