package com.example.tp1_jee.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label inStockLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label statusLabel;

    private String currentUsername;

    @FXML
    public void initialize() {
        // Mettre à jour l'heure dans le status
        updateStatus();

        // Mettre à jour l'heure toutes les secondes
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateStatus())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Charger les statistiques initiales
        loadStatistics();
    }

    public void setUsername(String username) {
        this.currentUsername = username;
        welcomeLabel.setText("Bienvenue, " + username + " !");
    }

    private void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        statusLabel.setText("Prêt | " + now.format(formatter));
    }

    private void loadStatistics() {
        // Simulation de données - à remplacer par vos vraies données
        totalProductsLabel.setText("24");
        inStockLabel.setText("18");
        lowStockLabel.setText("3");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous devrez vous reconnecter pour accéder à l'application.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Parent root = FXMLLoader.load(
                            getClass().getResource("/com/example/tp1_jee/login.fxml"));
                    Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                    stage.setScene(new Scene(root, 400, 350));
                    stage.setTitle("Stock App - Connexion");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        showInfo("Dashboard", "Vous êtes sur le dashboard principal");
    }

    @FXML
    private void showProducts(ActionEvent event) {
        showInfo("Produits", "Module de gestion des produits (à venir)");
    }

    @FXML
    private void showStock(ActionEvent event) {
        showInfo("Stock", "Module de gestion du stock (à venir)");
    }

    @FXML
    private void showProfile(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profil");
        alert.setHeaderText("Informations du profil");
        alert.setContentText("Utilisateur : " + currentUsername);
        alert.showAndWait();
    }

    @FXML
    private void addProduct(ActionEvent event) {
        showInfo("Ajouter Produit", "Fonctionnalité d'ajout de produit (à venir)");
    }

    @FXML
    private void viewInventory(ActionEvent event) {
        showInfo("Inventaire", "Vue de l'inventaire complet (à venir)");
    }

    @FXML
    private void viewReports(ActionEvent event) {
        showInfo("Rapports", "Génération de rapports (à venir)");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}