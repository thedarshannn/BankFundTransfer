package org.example.bankfundtransfer;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import oracle.jdbc.proxy.annotation.Pre;

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
        String queryCheck = "SELECT COUNT(*) FROM Accounts WHERE AccountNumber = ?";

        try(PreparedStatement prepStmt = con.prepareStatement(queryString);
            PreparedStatement checkStmt = con.prepareStatement(queryCheck)) {
            try(BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                String line = "";
                while((line = in.readLine()) != null){
                    String[] data = line.split("\\s+");
                    int accountNumber = Integer.parseInt(data[0]);
                    checkStmt.setInt(1, accountNumber);
                    ResultSet checkResult = checkStmt.executeQuery();
                    checkResult.next();
                    if(checkResult.getInt(1) == 0) {
                        prepStmt.setInt(1, accountNumber);
                        prepStmt.setString(2, data[1]);
                        prepStmt.setString(3, data[2]);
                        prepStmt.setDouble(4, Double.parseDouble(data[3]));
                        prepStmt.setString(5, data[4]);
                        prepStmt.executeUpdate();
                    }
                }
            }
        }
    }

    // Transfer funds from source account to target account
    public void transferFunds(int sourceAcc, double amount, int targetAcc){

        try{

            // Start a transaction
            con.setAutoCommit(false);

            // Check if source accounts are not locked
            PreparedStatement lockCheckStmt  = con.prepareStatement("SELECT IsLocked FROM Accounts WHERE AccountNumber = ?");
            lockCheckStmt.setInt(1, sourceAcc);
            ResultSet lockCheckResult = lockCheckStmt.executeQuery();
            lockCheckResult.next();
            String sourceLockStatus = lockCheckResult.getString("IsLocked");

            // Check if target accounts are not locked
            lockCheckStmt.setInt(1, targetAcc);
            lockCheckResult = lockCheckStmt.executeQuery();
            lockCheckResult.next();
            String targetLockStatus = lockCheckResult.getString("IsLocked");

            // If either account is locked, rollback the transaction
            if ("yes".equalsIgnoreCase(sourceLockStatus) || "yes".equalsIgnoreCase(targetLockStatus)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "One or both accounts are locked.");
                alert.show();
                con.rollback();
            }else{
                // Check if there are sufficient funds in the source account
                PreparedStatement balanceCheckStmt = con.prepareStatement("SELECT Balance FROM Accounts WHERE AccountNumber = ?");
                balanceCheckStmt.setInt(1, sourceAcc);
                ResultSet balanceCheckResult = balanceCheckStmt.executeQuery();
                balanceCheckResult.next();
                double sourceBalance = balanceCheckResult.getDouble("Balance");

                if(sourceBalance < amount){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Insufficient funds in the source account.");
                        alert.show();
                        con.rollback();
                }else {
                        // Update balances in both accounts
                        PreparedStatement updateSourceStmt = con.prepareStatement("UPDATE Accounts SET Balance = Balance - ? WHERE AccountNumber = ?");
                        updateSourceStmt.setDouble(1, amount);
                        updateSourceStmt.setInt(2, sourceAcc);
                        updateSourceStmt.executeUpdate();

                        PreparedStatement updateTargetStmt = con.prepareStatement("UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = ?");
                        updateTargetStmt.setDouble(1, amount);
                        updateTargetStmt.setInt(2, targetAcc);
                        updateTargetStmt.executeUpdate();

                        con.commit();
                        con.setAutoCommit(true);

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Transfer completed successfully.");
                        alert.show();
                }
            }
        }catch (NumberFormatException | SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input or database error occurred.");
            alert.show();
        }
    }

    public void showAccounts(TextArea accountsTextArea){
        try {
            // Create a StringBuilder to store the accounts information
            StringBuilder accountsInfo = new StringBuilder();

            // Prepare and execute the SQL query to select all rows from the Accounts table
            PreparedStatement statement = con.prepareStatement("SELECT * FROM Accounts");
            ResultSet resultSet = statement.executeQuery();

            // Iterate through the result set and append each row to the StringBuilder
            while (resultSet.next()) {
                int accountNumber = resultSet.getInt("AccountNumber");
                String name = resultSet.getString("Name");
                String lastName = resultSet.getString("LastName");
                double balance = resultSet.getDouble("Balance");
                String isLocked = resultSet.getString("IsLocked");

                accountsInfo.append("Account Number: ").append(accountNumber).append("\n")
                        .append("Name: ").append(name).append("\n")
                        .append("Last Name: ").append(lastName).append("\n")
                        .append("Balance: ").append(balance).append("\n")
                        .append("Is Locked: ").append(isLocked).append("\n\n");
            }

            // Set the accounts information to the TextArea
            accountsTextArea.setText(accountsInfo.toString());
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error retrieving accounts information.");
            alert.show();
        }
    }

    @Override
    public void close() throws Exception {
        con.close();
    }
}
