package com.example.csit228f2_2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HelloController {
    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordPassField;

    @FXML
    private Label result;
    public void registerButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(
                     "INSERT INTO USERS (name, password) VALUES (?, ?)"
             )) {

            String username = usernameTextField.getText();
            String password = passwordPassField.getText();

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Data created successfully!");
                result.setTextFill(Color.GREEN);
                result.setText("Data registered successfully!");

            }

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
    }
}