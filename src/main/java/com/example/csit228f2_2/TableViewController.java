package com.example.csit228f2_2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

// TODO remove account
// TODO log-out of account
// TODO update password

public class TableViewController implements Initializable {
    @FXML
    public static Label resultLabel2;
    @FXML
    private TextField fNameTF;

    @FXML
    private  TextField lNameTF;

    @FXML
    private TextField schoolProgramTF;

    @FXML
    private TableView<Student> table;

    @FXML
    private TableColumn<Student, String> fName;

    @FXML
    private TableColumn<Student, String> lName;

    @FXML
    private TableColumn<Student, String> schoolProgram;

    // Functions
    public ObservableList<Student> retrieveData() {
        ObservableList<Student> studentList = FXCollections.observableArrayList();
        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement()) {

            String selectQuery = "SELECT * FROM student where user_id = " + HelloController.getUserId();
            ResultSet result = statement.executeQuery(selectQuery);

            while (result.next()) {
                String fname = result.getString("first_name");
                String lname = result.getString("last_name");
                String sProgram = result.getString("school_program");

                // add student afawdwadawdawd
                studentList.add(new Student(fname, lname, sProgram));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentList;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Initialize table
        fName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        schoolProgram.setCellValueFactory(new PropertyValueFactory<>("schoolProgram"));

        table.setItems(retrieveData());
        editData();

    }

    public void btnInsertData(ActionEvent ignoredEvent) {
        String firstName = fNameTF.getText();
        String lastName = lNameTF.getText();
        String schoolProgram = schoolProgramTF.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || schoolProgram.isEmpty()) {
            return;
        }

        try {
            // Insert with batch processing
            batchInsertStudent(firstName, lastName, schoolProgram);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void editData() {
        schoolProgram.setCellFactory(TextFieldTableCell.forTableColumn());
        schoolProgram.setOnEditCommit(e -> {
            Student student = e.getTableView().getItems().get(e.getTablePosition().getRow());
            student.setSchoolProgram(e.getNewValue());
            updateSchoolProgram(student.getFirstName(), student.getLastName(), e.getNewValue());
        });
    }

    // Helper functions
    private void batchInsertStudent(String firstName, String lastName, String schoolProgram) throws SQLException {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(
                     "INSERT INTO STUDENT (first_name, last_name, school_program, user_id) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, schoolProgram);
            preparedStatement.setInt(4, HelloController.getUserId());

            preparedStatement.addBatch();

            int[] batchResult = preparedStatement.executeBatch();

            for (int i : batchResult) {
                if (i == Statement.EXECUTE_FAILED) {
                    System.out.println("One of the statements in the batch failed.");
                    // TODO result label if fail insert
                }
            }

            // clear input
            fNameTF.clear();
            lNameTF.clear();
            schoolProgramTF.clear();
            table.getItems().clear();
            table.getItems().addAll(retrieveData());

            // TODO result label for batch insert

        }
    }

    private void updateSchoolProgram(String firstName, String lastName, String newSchoolProgram) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(
                     "UPDATE student SET school_program = ? WHERE first_name = ? AND last_name = ?")) {

            preparedStatement.setString(1, newSchoolProgram);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                // TODO result label for if and else
                System.out.println("School program updated successfully for " + firstName + " " + lastName);
            } else {
                System.out.println("Failed to update school program for " + firstName + " " + lastName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(ActionEvent ignoredEvent) {
        TableView.TableViewSelectionModel<Student> selectionModel = table.getSelectionModel();

        if (selectionModel.isEmpty()) {
            System.out.println("No items selected.");
            return;
        }

        ObservableList<Student> selectedItems = selectionModel.getSelectedItems();

        try (Connection c = MySQLConnection.getConnection()) {

            for (Student student : selectedItems) {
                String firstName = student.getFirstName();
                String lastName = student.getLastName();

                try (PreparedStatement preparedStatement = c.prepareStatement(
                        "DELETE FROM student WHERE first_name = ? AND last_name = ?")) {
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);

                    int rowsDeleted = preparedStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Data for " + firstName + " " + lastName + " deleted successfully from the database.");
                        // TODO result label
                    } else {
                        System.out.println("Failed to delete data for " + firstName + " " + lastName + " from the database.");
                        // TODO result label
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        table.getItems().removeAll(selectedItems);
    }

    // TODO are you sure you want to logout
    public void logout(MouseEvent event) throws IOException {
        HelloController.loadAndDisplayScene("loginview.fxml", (Node) event.getSource(), 1000, 600);
    }

    public void terminateAccount(MouseEvent event) throws IOException {
        HelloController.loadAndDisplaySceneNotHidden("terminateaccount.fxml",300, 190);
    }

    public void changePassword(MouseEvent event) throws IOException {
        HelloController.loadAndDisplaySceneNotHidden("editpassword.fxml",300, 190);
    }
}