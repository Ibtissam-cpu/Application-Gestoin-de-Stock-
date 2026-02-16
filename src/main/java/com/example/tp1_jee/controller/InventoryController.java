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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;

public class InventoryController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TableColumn<Product, String> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Integer> minStockColumn;

    @FXML
    private TableColumn<Product, String> statusColumn;

    @FXML
    private TableColumn<Product, Void> actionsColumn;

    @FXML
    private Label totalLabel;

    @FXML
    private Label lowStockLabel;

    private ProductService productService = new ProductService();
    private ObservableList<Product> productsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        minStockColumn.setCellValueFactory(new PropertyValueFactory<>("minStockLevel"));

        // Colonne de statut avec couleur
        statusColumn.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Product product = getTableRow().getItem();
                    if (product.isLowStock()) {
                        setText("⚠️ Stock Faible");
                        setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
                    } else if (product.getQuantity() == 0) {
                        setText("❌ Rupture");
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setText("✅ Disponible");
                        setStyle("-fx-text-fill: #4CAF50;");
                    }
                }
            }
        });

        // Colonne d'actions avec boutons
        actionsColumn.setCellFactory(column -> new TableCell<Product, Void>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                buttons.setAlignment(Pos.CENTER);
                editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleEdit(product);
                });

                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleDelete(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        // Charger les données
        loadProducts();
    }

    private void loadProducts() {
        productsList.clear();
        productsList.addAll(productService.getAllProducts());
        productsTable.setItems(productsList);
        updateStatistics();
    }

    private void updateStatistics() {
        totalLabel.setText("Total: " + productService.getTotalProducts() + " produits");
        lowStockLabel.setText("Stock faible: " + productService.getLowStockCount());
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadProducts();
        } else {
            productsList.clear();
            productsList.addAll(productService.searchProducts(keyword));
            productsTable.setItems(productsList);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        searchField.clear();
        loadProducts();
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/tp1_jee/add-product.fxml"));
            Stage stage = (Stage) productsTable.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEdit(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getQuantity()));
        dialog.setTitle("Modifier la Quantité");
        dialog.setHeaderText("Produit: " + product.getName());
        dialog.setContentText("Nouvelle quantité:");

        dialog.showAndWait().ifPresent(response -> {
            try {
                int newQuantity = Integer.parseInt(response);
                if (newQuantity >= 0) {
                    product.setQuantity(newQuantity);
                    productService.updateProduct(product);
                    loadProducts();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setContentText("Quantité mise à jour avec succès !");
                    alert.showAndWait();
                } else {
                    showError("La quantité ne peut pas être négative");
                }
            } catch (NumberFormatException e) {
                showError("Veuillez entrer un nombre valide");
            }
        });
    }

    private void handleDelete(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Voulez-vous vraiment supprimer \"" + product.getName() + "\" ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (productService.deleteProduct(product.getId())) {
                    loadProducts();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Succès");
                    success.setContentText("Produit supprimé avec succès !");
                    success.showAndWait();
                }
            }
        });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/tp1_jee/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) productsTable.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}