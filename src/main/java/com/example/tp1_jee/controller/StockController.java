package com.example.tp1_jee.controller;

import com.example.tp1_jee.model.Product;
import com.example.tp1_jee.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StockController {

    @FXML
    private Label inStockLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label outOfStockLabel;

    @FXML
    private ComboBox<String> productComboBox;

    @FXML
    private TextField currentStockField;

    @FXML
    private TextField quantityField;

    @FXML
    private ComboBox<String> operationTypeComboBox;

    @FXML
    private TextArea reasonArea;

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<StockMovement> stockMovementsTable;

    @FXML
    private TableColumn<StockMovement, String> dateColumn;

    @FXML
    private TableColumn<StockMovement, String> productNameColumn;

    @FXML
    private TableColumn<StockMovement, String> operationColumn;

    @FXML
    private TableColumn<StockMovement, Integer> quantityMovedColumn;

    @FXML
    private TableColumn<StockMovement, String> reasonColumn;

    @FXML
    private ListView<String> lowStockListView;

    private ProductService productService;
    private List<Product> products;
    private ObservableList<StockMovement> stockMovements;

    @FXML
    public void initialize() {
        productService = new ProductService();
        stockMovements = FXCollections.observableArrayList();

        loadStatistics();
        setupProductComboBox();
        setupStockMovementsTable();
        loadLowStockProducts();

        // Définir la valeur par défaut pour le type d'opération
        operationTypeComboBox.setValue("Ajout");

        // Listener pour la sélection du produit
        productComboBox.setOnAction(event -> updateCurrentStock());
    }

    private void loadStatistics() {
        try {
            products = productService.getAllProducts();

            // Produits en stock
            long inStock = products.stream()
                    .filter(p -> p.getQuantity() > p.getMinStock())
                    .count();
            inStockLabel.setText(String.valueOf(inStock));

            // Stock faible
            long lowStock = products.stream()
                    .filter(p -> p.getQuantity() > 0 && p.getQuantity() <= p.getMinStock())
                    .count();
            lowStockLabel.setText(String.valueOf(lowStock));

            // Rupture de stock
            long outOfStock = products.stream()
                    .filter(p -> p.getQuantity() == 0)
                    .count();
            outOfStockLabel.setText(String.valueOf(outOfStock));

        } catch (Exception e) {
            showError("Erreur lors du chargement des statistiques");
            e.printStackTrace();
        }
    }

    private void setupProductComboBox() {
        ObservableList<String> productNames = FXCollections.observableArrayList(
                products.stream()
                        .map(Product::getName)
                        .collect(Collectors.toList())
        );
        productComboBox.setItems(productNames);
    }

    private void updateCurrentStock() {
        String selectedProductName = productComboBox.getValue();
        if (selectedProductName != null) {
            Product product = products.stream()
                    .filter(p -> p.getName().equals(selectedProductName))
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                currentStockField.setText(String.valueOf(product.getQuantity()));
            }
        }
    }

    private void setupStockMovementsTable() {
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));
        productNameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProductName()));
        operationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOperation()));
        quantityMovedColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        reasonColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReason()));

        stockMovementsTable.setItems(stockMovements);
    }

    private void loadLowStockProducts() {
        ObservableList<String> lowStockItems = FXCollections.observableArrayList(
                products.stream()
                        .filter(p -> p.getQuantity() <= p.getMinStock())
                        .map(p -> String.format("%s - Stock: %d / Seuil: %d",
                                p.getName(), p.getQuantity(), p.getMinStock()))
                        .collect(Collectors.toList())
        );

        lowStockListView.setItems(lowStockItems);

        if (lowStockItems.isEmpty()) {
            lowStockListView.setPlaceholder(new Label("✅ Aucun produit en stock faible"));
        }
    }

    @FXML
    private void updateStock() {
        messageLabel.setVisible(false);

        // Validation
        if (productComboBox.getValue() == null) {
            showMessage("Veuillez sélectionner un produit", "error");
            return;
        }

        if (quantityField.getText().isEmpty()) {
            showMessage("Veuillez entrer une quantité", "error");
            return;
        }

        try {
            String productName = productComboBox.getValue();
            int quantity = Integer.parseInt(quantityField.getText());
            String operation = operationTypeComboBox.getValue();

            if (quantity <= 0) {
                showMessage("La quantité doit être positive", "error");
                return;
            }

            // Trouver le produit
            Product product = products.stream()
                    .filter(p -> p.getName().equals(productName))
                    .findFirst()
                    .orElse(null);

            if (product == null) {
                showMessage("Produit introuvable", "error");
                return;
            }

            // Mettre à jour le stock
            int newQuantity;
            if (operation.equals("Ajout")) {
                newQuantity = product.getQuantity() + quantity;
            } else {
                newQuantity = product.getQuantity() - quantity;
                if (newQuantity < 0) {
                    showMessage("Stock insuffisant pour ce retrait", "error");
                    return;
                }
            }

            product.setQuantity(newQuantity);
            productService.updateProduct(product);

            // Ajouter au mouvement de stock
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String reason = reasonArea.getText().isEmpty() ? "Non spécifié" : reasonArea.getText();

            stockMovements.add(0, new StockMovement(date, productName, operation, quantity, reason));

            // Réinitialiser le formulaire
            currentStockField.setText(String.valueOf(newQuantity));
            quantityField.clear();
            reasonArea.clear();

            // Actualiser les statistiques
            loadStatistics();
            loadLowStockProducts();

            showMessage("Stock mis à jour avec succès !", "success");

        } catch (NumberFormatException e) {
            showMessage("Veuillez entrer un nombre valide", "error");
        } catch (Exception e) {
            showMessage("Erreur lors de la mise à jour: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshHistory() {
        showInfo("Historique actualisé !");
    }

    @FXML
    private void backToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/tp1_jee/dashboard.fxml"));
            Stage stage = (Stage) productComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Erreur lors du retour au dashboard");
            e.printStackTrace();
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);

        if (type.equals("success")) {
            messageLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14; -fx-font-weight: bold;");
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classe interne pour les mouvements de stock
    public static class StockMovement {
        private final String date;
        private final String productName;
        private final String operation;
        private final int quantity;
        private final String reason;

        public StockMovement(String date, String productName, String operation,
                             int quantity, String reason) {
            this.date = date;
            this.productName = productName;
            this.operation = operation;
            this.quantity = quantity;
            this.reason = reason;
        }

        public String getDate() { return date; }
        public String getProductName() { return productName; }
        public String getOperation() { return operation; }
        public int getQuantity() { return quantity; }
        public String getReason() { return reason; }
    }
}