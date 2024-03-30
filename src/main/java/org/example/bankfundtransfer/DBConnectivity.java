package org.example.bankfundtransfer;

import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
public class DBConnectivity implements AutoCloseable{

    private Connection con;

    //Define the URL string
    protected String url = "jdbc:oracle:thin:@calvin.humber.ca:1521:grok";
    protected String username = "n01584247";
    protected String pass = "oracle";


    public void DBConnection() throws SQLException {

        // Load the JDBC driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Oracle JDBC driver not found.");
            alert.show();
            return;
        }

        // Establish the connection
        try{
            con = DriverManager.getConnection(url, username, pass);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Connection established.");
            alert.show();

        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error establishing connection.");
            alert.show();
        }

    }

    public void createTable() throws SQLException {
        try {
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "ACCOUNTS", null);
            if (!tables.next()) {
                // Table does not exist
                try (Statement stmt = con.createStatement()) {
                    String sqlCreateQuery = "CREATE TABLE Accounts (" +
                            "AccountNumber NUMBER PRIMARY KEY," +
                            "Name VARCHAR2(50)," +
                            "LastName VARCHAR2(50)," +
                            "Balance NUMBER(10, 2)," +
                            "IsLocked VARCHAR2(3)" +
                            ")";
                    stmt.execute(sqlCreateQuery);
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error creating table.");
                    alert.show();
                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error checking if table exists.");
            alert.show();
        }
    }

    public void insertDataThroughFile(String fileName) throws SQLException, IOException{
        String queryString = "INSERT INTO Accounts (AccountNumber, Name, LastName, Balance, IsLocked) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement prepStmt = con.prepareStatement(queryString)) {
            try(BufferedReader in = new BufferedReader(new FileReader(fileName))){
                String line = "";
                while((line = in.readLine()) != null){
                    String[] data = line.split("\\s+");
                    prepStmt.setInt(1, Integer.parseInt(data[0]));
                    prepStmt.setString(2, data[1]);
                    prepStmt.setString(3, data[2]);
                    prepStmt.setDouble(4, Double.parseDouble(data[3]));
                    prepStmt.setString(5, data[4]);
                    prepStmt.executeUpdate();
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        con.close();
    }
}
