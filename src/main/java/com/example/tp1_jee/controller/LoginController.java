package com.example.tp1_jee.controller;

import com.example.tp1_jee.model.Session;
import com.example.tp1_jee.model.User;
import com.example.tp1_jee.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", "red");
            return;
        }

        if (userService.authenticate(username, password)) {
            try {
                // Récupérer l'utilisateur et sauvegarder dans la session
                User user = userService.findUserByUsername(username);
                Session.getInstance().setCurrentUser(user);

                // Charger le dashboard
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/tp1_jee/dashboard.fxml"));
                Parent root = loader.load();

                // Passer le nom d'utilisateur au dashboard
                DashboardController dashboardController = loader.getController();
                dashboardController.setUsername(username);

                // Changer de scène
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 900, 600));
                stage.setTitle("Stock App - Dashboard");
                stage.centerOnScreen();

            } catch (Exception e) {
                e.printStackTrace();
                showMessage("Erreur lors du chargement du dashboard", "red");
            }
        } else {
            showMessage("Identifiants incorrects", "red");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/tp1_jee/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 450));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message, String color) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: " + color + ";");
        } else {
            System.out.println(message);
        }
    }
}