package com.example.tp1_jee.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProfileController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField registrationDateField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label passwordMessageLabel;

    @FXML
    private Label productsCreatedLabel;

    @FXML
    private Label lastLoginLabel;

    private String currentUsername = "admin"; // À récupérer depuis la session

    @FXML
    public void initialize() {
        loadUserProfile();
    }

    private void loadUserProfile() {
        // Charger les informations de l'utilisateur
        // Dans une vraie application, ces données viendraient d'une base de données

        usernameLabel.setText(currentUsername);
        roleLabel.setText("Administrateur");

        usernameField.setText(currentUsername);
        emailField.setText(currentUsername + "@stockapp.com");
        roleField.setText("Administrateur");
        registrationDateField.setText("01/01/2024");

        // Statistiques
        productsCreatedLabel.setText("0");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lastLoginLabel.setText(now.format(formatter));
    }

    @FXML
    private void changePassword() {
        passwordMessageLabel.setVisible(false);

        // Validation
        if (oldPasswordField.getText().isEmpty() ||
                newPasswordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()) {
            showPasswordMessage("Veuillez remplir tous les champs", "error");
            return;
        }

        if (newPasswordField.getText().length() < 6) {
            showPasswordMessage("Le mot de passe doit contenir au moins 6 caractères", "error");
            return;
        }

        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            showPasswordMessage("Les mots de passe ne correspondent pas", "error");
            return;
        }

        // Dans une vraie application, vérifier l'ancien mot de passe et mettre à jour
        // Pour l'instant, on simule le succès

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer le changement");
        alert.setHeaderText("Changer le mot de passe");
        alert.setContentText("Voulez-vous vraiment changer votre mot de passe ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showPasswordMessage("Mot de passe changé avec succès !", "success");
                resetPasswordFields();
            }
        });
    }

    @FXML
    private void resetPasswordFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        passwordMessageLabel.setVisible(false);
    }

    private void showPasswordMessage(String message, String type) {
        passwordMessageLabel.setText(message);
        passwordMessageLabel.setVisible(true);

        if (type.equals("success")) {
            passwordMessageLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14; -fx-font-weight: bold;");
        } else {
            passwordMessageLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/tp1_jee/dashboard.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Erreur lors du retour au dashboard");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}