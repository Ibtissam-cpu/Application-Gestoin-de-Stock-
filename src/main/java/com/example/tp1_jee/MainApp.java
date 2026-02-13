package com.example.tp1_jee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/tp1_jee/login.fxml"));

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Stock App");
        stage.show();
    }
}
