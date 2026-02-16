package com.example.tp1_jee.controller;

import com.example.tp1_jee.model.Product;
import com.example.tp1_jee.service.ProductService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.*;
import java.util.stream.Collectors;

public class ReportsController {

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label totalValueLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label outOfStockLabel;

    @FXML
    private Label avgPriceLabel;

    @FXML
    private Label categoriesLabel;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private BarChart<String, Number> stockBarChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        loadStatistics();
        loadCharts();
    }

    private void loadStatistics() {
        List<Product> products = productService.getAllProducts();

        // Total des produits
        totalProductsLabel.setText(String.valueOf(products.size()));

        // Valeur totale du stock
        double totalValue = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        totalValueLabel.setText(String.format("%.2f DH", totalValue));

        // Stock faible
        lowStockLabel.setText(String.valueOf(productService.getLowStockCount()));

        // Ruptures de stock
        long outOfStock = products.stream()
                .filter(p -> p.getQuantity() == 0)
                .count();
        outOfStockLabel.setText(String.valueOf(outOfStock));

        // Prix moyen
        double avgPrice = products.stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);
        avgPriceLabel.setText(String.format("%.2f DH", avgPrice));

        // Nombre de catégories
        long categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .count();
        categoriesLabel.setText(String.valueOf(categories));
    }

    private void loadCharts() {
        List<Product> products = productService.getAllProducts();

        // Graphique par catégorie (Pie Chart)
        Map<String, Long> categoryCount = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        categoryPieChart.setData(FXCollections.observableArrayList(
                categoryCount.entrySet().stream()
                        .map(entry -> new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()))
                        .collect(Collectors.toList())
        ));

        // Graphique des stocks (Bar Chart)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantité en Stock");

        // Prendre les 10 premiers produits (ou tous si moins de 10)
        products.stream()
                .limit(10)
                .forEach(product -> {
                    series.getData().add(new XYChart.Data<>(
                            product.getName().length() > 15 ?
                                    product.getName().substring(0, 15) + "..." :
                                    product.getName(),
                            product.getQuantity()
                    ));
                });

        stockBarChart.getData().clear();
        stockBarChart.getData().add(series);
    }

    @FXML
    private void handleExportPDF(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export PDF");
        alert.setHeaderText("Fonctionnalité d'export PDF");
        alert.setContentText("Cette fonctionnalité sera disponible prochainement.");
        alert.showAndWait();
    }

    @FXML
    private void handleExportExcel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Excel");
        alert.setHeaderText("Fonctionnalité d'export Excel");
        alert.setContentText("Cette fonctionnalité sera disponible prochainement.");
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadStatistics();
        loadCharts();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualisation");
        alert.setContentText("Les statistiques ont été actualisées !");
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/tp1_jee/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) totalProductsLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}