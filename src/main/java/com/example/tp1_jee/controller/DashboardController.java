package com.example.tp1_jee.controller;

import com.example.tp1_jee.service.ProductService;
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
    private ProductService productService = new ProductService();

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
        try {
            totalProductsLabel.setText(String.valueOf(productService.getTotalProducts()));
            inStockLabel.setText(String.valueOf(productService.getInStockCount()));
            lowStockLabel.setText(String.valueOf(productService.getLowStockCount()));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== NAVIGATION MENU LATÉRAL ====================

    @FXML
    private void showDashboard(ActionEvent event) {
        // Déjà sur le dashboard, juste actualiser les statistiques
        loadStatistics();
        showInfo("Dashboard", "Statistiques actualisées");
    }

    @FXML
    private void showProducts(ActionEvent event) {
        loadView("/com/example/tp1_jee/inventory.fxml", "Inventaire des Produits", 1000, 600);
    }

    @FXML
    private void showStock(ActionEvent event) {
        loadView("/com/example/tp1_jee/stock.fxml", "Gestion du Stock", 1000, 700);
    }

    @FXML
    private void showProfile(ActionEvent event) {
        loadView("/com/example/tp1_jee/profile.fxml", "Mon Profil", 800, 700);
    }

    // ==================== ACTIONS RAPIDES ====================

    @FXML
    private void addProduct(ActionEvent event) {
        loadView("/com/example/tp1_jee/add-product.fxml", "Ajouter un Produit", 700, 600);
    }

    @FXML
    private void viewInventory(ActionEvent event) {
        loadView("/com/example/tp1_jee/inventory.fxml", "Inventaire", 1000, 600);
    }

    @FXML
    private void viewReports(ActionEvent event) {
        loadView("/com/example/tp1_jee/reports.fxml", "Rapports", 1000, 700);
    }

    // ==================== DÉCONNEXION ====================

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
                    showError("Erreur lors de la déconnexion");
                    e.printStackTrace();
                }
            }
        });
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Charge une vue avec une taille spécifique
     */
    private void loadView(String fxmlPath, String title, int width, int height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.setTitle("Stock App - " + title);
        } catch (Exception e) {
            showError("Erreur lors du chargement de " + title);
            System.err.println("Chemin FXML : " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Affiche un message d'information
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}