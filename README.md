# Bank Account Transfer Application

This is a GUI application for a bank to manage fund transfers between accounts. It allows users to transfer funds from one account to another, provided certain conditions are met.


!![png](https://cdn-icons-png.flaticon.com/512/14172/14172336.png)
## Features

- Creates an `Accounts` table in the database and populates it with data from `Accounts.txt` file at startup.
- Accepts user input through three fields:
- Account number to transfer funds from
- Amount to be transferred
- Account number to transfer funds to
- Initiates the transfer when the 'Submit' button is pressed, but only if:
- The source account has sufficient funds
- Both source and destination accounts are not locked
- Displays a message if the transfer cannot be completed due to insufficient funds or locked accounts.
- Shows the contents of the `Accounts` table in a text area when the 'Show Accounts' button is pressed.
- Uses prepared statements and transactions to ensure data integrity and security.
- Handles exceptions properly throughout the application.