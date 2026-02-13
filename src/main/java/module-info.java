module com.example.tp1_jee {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.tp1_jee to javafx.fxml;
    opens com.example.tp1_jee.controller to javafx.fxml;

    exports com.example.tp1_jee;
    exports com.example.tp1_jee.controller;
    exports com.example.tp1_jee.model;
    exports com.example.tp1_jee.service;
}