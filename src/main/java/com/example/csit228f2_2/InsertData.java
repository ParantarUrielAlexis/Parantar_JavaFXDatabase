package com.example.csit228f2_2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class InsertData {
    public static void main(String[] args) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(
                     "INSERT INTO USERS (name, email) VALUES (?, ?)"
             )) {
            String name = "Lance DB Antor";
            String email = "lancesiuu@gmail.com";

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Data created successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
