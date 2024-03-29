module org.example.bankfundtransfer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.oracle.database.jdbc;


    opens org.example.bankfundtransfer to javafx.fxml;
    exports org.example.bankfundtransfer;
}