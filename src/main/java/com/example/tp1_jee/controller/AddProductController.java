package com.example.tp1_jee.controller;

import com.example.tp1_jee.model.Product;
import com.example.tp1_jee.service.ProductService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AddProductController {

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField minStockField;

    @FXML
    private Label messageLabel;

    private ProductService productService;

    @FXML
    public void initialize() {
        productService = new ProductService();
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleSave() {
        // Réinitialiser le message
        messageLabel.setVisible(false);

        // Validation des champs obligatoires
        if (!validateFields()) {
            return;
        }

        try {
            // Créer un nouveau produit
            Product product = new Product();
            product.setName(nameField.getText().trim());
            product.setCategory(categoryComboBox.getValue());
            product.setDescription(descriptionArea.getText().trim());
            product.setPrice(Double.parseDouble(priceField.getText().trim()));
            product.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            product.setMinStock(Integer.parseInt(minStockField.getText().trim()));

            // Enregistrer le produit via le service
            productService.addProduct(product);

            // Afficher un message de succès
            showMessage("Produit ajouté avec succès !", "success");

            // Réinitialiser le formulaire
            clearFields();

        } catch (NumberFormatException e) {
            showMessage("Erreur : Veuillez entrer des nombres valides pour le prix et les quantités.", "error");
        } catch (Exception e) {
            showMessage("Erreur lors de l'ajout du produit : " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        clearFields();
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleBack() {
        try {
            // Charger la vue du dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tp1_jee/dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et changer la vue
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showMessage("Erreur lors du retour au tableau de bord.", "error");
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        // Vérifier le nom
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("• Le nom du produit est obligatoire\n");
        }

        // Vérifier la catégorie
        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().isEmpty()) {
            errors.append("• La catégorie est obligatoire\n");
        }

        // Vérifier le prix
        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
            errors.append("• Le prix est obligatoire\n");
        } else {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) {
                    errors.append("• Le prix doit être positif\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Le prix doit être un nombre valide\n");
            }
        }

        // Vérifier la quantité
        if (quantityField.getText() == null || quantityField.getText().trim().isEmpty()) {
            errors.append("• La quantité est obligatoire\n");
        } else {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity < 0) {
                    errors.append("• La quantité doit être positive\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• La quantité doit être un nombre entier\n");
            }
        }

        // Vérifier le seuil d'alerte
        if (minStockField.getText() == null || minStockField.getText().trim().isEmpty()) {
            errors.append("• Le seuil d'alerte est obligatoire\n");
        } else {
            try {
                int minStock = Integer.parseInt(minStockField.getText().trim());
                if (minStock < 0) {
                    errors.append("• Le seuil d'alerte doit être positif\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Le seuil d'alerte doit être un nombre entier\n");
            }
        }

        // Si des erreurs existent, les afficher
        if (errors.length() > 0) {
            showMessage(errors.toString(), "error");
            return false;
        }

        return true;
    }

    private void clearFields() {
        nameField.clear();
        categoryComboBox.setValue(null);
        descriptionArea.clear();
        priceField.clear();
        quantityField.clear();
        minStockField.clear();
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
}