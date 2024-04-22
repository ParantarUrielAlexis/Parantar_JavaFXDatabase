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

import java.io.IOException;

import java.sql.*;


public class HelloController {

    @FXML
    public Button loginButton;

    @FXML
    public ProgressBar passwordProgressBar;

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

    public void loadAndDisplayScene(String fxmlPath, Node node, int v1, int v2) throws IOException {
        Stage stage = (Stage) node.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Stage newStage = new Stage();
        Scene scene = new Scene(root, v1, v2);
        newStage.setScene(scene);
        stage.hide();
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
        String enteredPassword = passwordPassField.getText();

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

}
