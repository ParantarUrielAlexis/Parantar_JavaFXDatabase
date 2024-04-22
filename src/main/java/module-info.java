module com.example.csit228f2_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.example.csit228f2_2 to javafx.fxml;
    exports com.example.csit228f2_2;
}