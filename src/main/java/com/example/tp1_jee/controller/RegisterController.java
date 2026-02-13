package com.example.tp1_jee.controller;

import com.example.tp1_jee.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private UserService userService = new UserService();

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Tous les champs sont obligatoires");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (username.length() < 3) {
            messageLabel.setText("Le nom d'utilisateur doit contenir au moins 3 caractères");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!email.contains("@")) {
            messageLabel.setText("Email invalide");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (password.length() < 4) {
            messageLabel.setText("Le mot de passe doit contenir au moins 4 caractères");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Les mots de passe ne correspondent pas");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Enregistrement
        boolean success = userService.register(username, password, email);

        if (success) {
            messageLabel.setText("Inscription réussie ! Redirection...");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Rediriger vers la page de connexion après 1.5 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            handleBack(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            messageLabel.setText("Ce nom d'utilisateur existe déjà");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/tp1_jee/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 350));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}