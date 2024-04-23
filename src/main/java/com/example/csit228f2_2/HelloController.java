package com.example.csit228f2_2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HelloController {

    @FXML
    public Button loginButton;

    @FXML
    public ProgressBar passwordProgressBar;

    @FXML
    public Label newPassLabel;
    @FXML
    private TextField changePassTF;

    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordPassField;

    @FXML
    private Label resultLabel;

    @FXML
    protected Label registerLabel;

    @FXML
    protected Label loginLabel;


    Timeline timelineResult;

    static int userId;

    static String enteredPassword;


    public static String getEnteredPassword() {
        return enteredPassword;
    }

    public static int getUserId() {
        return userId;
    }


    @FXML
    public void initialize() {
        timelineResult = new Timeline(
                new KeyFrame(Duration.ZERO, e -> resultLabel.setVisible(true)), // Show label
                new KeyFrame(Duration.seconds(2.5), e -> resultLabel.setVisible(false)) // Hide label after 4 seconds
        );
    }

    // Helper function to execute an SQL query
    private ResultSet executeQuery(String query) throws SQLException {
        Connection c = MySQLConnection.getConnection();
        Statement statement = c.createStatement();
        return statement.executeQuery(query);
    }

    // Helper function to insert data into the database
    private void insertData(String query, String username, String password) throws SQLException {

        // Condition if username and password is empty
        if (password.isEmpty()) {
            resultLabel.setTextFill(Color.RED);
            resultLabel.setText("Please input username or password.");
            timelineResult.play();
            return;
        }

        // Condition if username is less than 6 characters
        if (username.length() < 6) {
            resultLabel.setTextFill(Color.RED);
            resultLabel.setText("Username must be 6 or more characters");
            timelineResult.play();
            return;
        }

        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data created successfully!");
                resultLabel.setTextFill(Color.GREEN);
                resultLabel.setText("Data registered successfully!");
                timelineResult.play();
            }
        }
    }

    public void registerButtonOnAction() {
        try {
            String query = "INSERT INTO USERS (name, password) VALUES (?, ?)";
            insertData(query, usernameTextField.getText(), passwordPassField.getText());
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
    }

    public void registerSceneLabel(MouseEvent event) throws IOException {
        registerLabel.setTextFill(Color.GREEN);
        loadAndDisplayScene("register.fxml", (Node) event.getSource(), 1000, 600);
    }

    public void loginSceneLabel(MouseEvent event) throws IOException {
        loadAndDisplayScene("loginview.fxml", (Node) event.getSource(), 1000, 600);
    }

    public static void loadAndDisplayScene(String fxmlPath, Node node, int v1, int v2) throws IOException {
        Stage stage = (Stage) node.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloController.class.getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Stage newStage = new Stage();
        Scene scene = new Scene(root, v1, v2);
        newStage.setScene(scene);
        stage.hide();
        newStage.setResizable(false);
        newStage.show();
    }

    public static void loadAndDisplaySceneNotHidden(String fxmlPath, int v1, int v2) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TableViewController.class.getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Stage newStage = new Stage();
        Scene scene = new Scene(root, v1, v2);
        newStage.setScene(scene);
        newStage.setResizable(false);
        newStage.show();
    }

    @FXML
    public void registerLabelHoverEntered(MouseEvent ignoredEvent) {
        registerLabel.setTextFill(Color.DARKGRAY);
    }

    @FXML
    public void registerLabelHoverExited(MouseEvent ignoredEvent) {
        registerLabel.setTextFill(Color.GRAY);
    }

    @FXML
    public void registerLabelPressed(MouseEvent ignoredEvent) {
        registerLabel.setTextFill(Color.LIGHTGRAY); // Set back to default color when mouse exits
    }

    public void loginButtonOnAction(ActionEvent event) throws IOException {
        String username = usernameTextField.getText();
        enteredPassword = passwordPassField.getText();

        // Condition if username and password is empty
        if (enteredPassword.isEmpty()) {
            resultLabel.setTextFill(Color.RED);
            resultLabel.setText("Please input username or password.");
            timelineResult.play();
            return;
        }

        try {
            ResultSet result = executeQuery("SELECT * FROM users WHERE name = '" + username + "' AND password = '" + enteredPassword + "'");
            if (result.next()) {
                userId = result.getInt("id");
                loadAndDisplayScene("admindashboard.fxml", (Node) event.getSource(), 1000, 600);
            } else {
                resultLabel.setTextFill(Color.RED);
                resultLabel.setText("Incorrect username or password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loginLabelHoverEntered(MouseEvent ignoredEvent) {
        loginLabel.setTextFill(Color.DARKGRAY);
    }

    public void loginLabelHoverExited(MouseEvent ignoredEvent) {
        loginLabel.setTextFill(Color.GRAY);
    }

    public void loginLabelPressed(MouseEvent ignoredEvent) {
        loginLabel.setTextFill(Color.GRAY);
    }


    public void changePassOnAction(ActionEvent event) {
        String newPass = changePassTF.getText();
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(
                     "UPDATE users SET password = ? WHERE id = ?")) {

            preparedStatement.setString(1, newPass);
            preparedStatement.setInt(2, getUserId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
//                TableViewController.resultLabel2.setText("Password changed!");
//                TableViewController.resultLabel2.setTextFill(Color.GREEN);
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } else {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void terminateAccountOnAction(ActionEvent event) throws IOException {
        try (Connection c = MySQLConnection.getConnection()) {
            // First, delete student records associated with the user
            try (PreparedStatement deleteStudentStatement = c.prepareStatement(
                    "DELETE FROM student WHERE user_id = ?")) {
                deleteStudentStatement.setInt(1, getUserId());
                int studentRowsDeleted = deleteStudentStatement.executeUpdate();
                // Log or handle the result if needed
            }

            // Then, delete the user record
            try (PreparedStatement deleteUserStatement = c.prepareStatement(
                    "DELETE FROM users WHERE id = ?")) {
                deleteUserStatement.setInt(1, getUserId());
                int userRowsDeleted = deleteUserStatement.executeUpdate();
                // Log or handle the result if needed
                if (userRowsDeleted > 0) {
                    // User deleted successfully
                } else {
                    // User not found or not deleted
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(0);
        loadAndDisplayScene("loginview.fxml", (Node) event.getSource(), 1000, 600);
    }


}
