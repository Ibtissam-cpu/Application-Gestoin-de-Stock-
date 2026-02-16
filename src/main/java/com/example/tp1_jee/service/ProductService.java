package com.example.tp1_jee.service;

import com.example.tp1_jee.model.Product;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductService {
    private static final String FILE_PATH = "products.dat";
    private List<Product> products;

    public ProductService() {
        this.products = loadProducts();
    }

    // Charger les produits depuis le fichier
    @SuppressWarnings("unchecked")
    private List<Product> loadProducts() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Product>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement des produits: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Sauvegarder les produits dans le fichier
    private void saveProducts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(products);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des produits: " + e.getMessage());
        }
    }

    // Générer un ID unique
    private String generateId() {
        return "PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Ajouter un produit
    public boolean addProduct(Product product) {
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(generateId());
        }

        // Vérifier si le produit existe déjà
        if (findProductById(product.getId()) != null) {
            return false;
        }

        products.add(product);
        saveProducts();
        return true;
    }

    // Mettre à jour un produit
    public boolean updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(product.getId())) {
                products.set(i, product);
                saveProducts();
                return true;
            }
        }
        return false;
    }

    // Supprimer un produit
    public boolean deleteProduct(String id) {
        boolean removed = products.removeIf(p -> p.getId().equals(id));
        if (removed) {
            saveProducts();
        }
        return removed;
    }

    // Trouver un produit par ID
    public Product findProductById(String id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Obtenir tous les produits
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Obtenir les produits en stock faible
    public List<Product> getLowStockProducts() {
        return products.stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    // Obtenir les statistiques
    public int getTotalProducts() {
        return products.size();
    }

    public int getInStockCount() {
        return (int) products.stream()
                .filter(p -> p.getQuantity() > 0)
                .count();
    }

    public int getLowStockCount() {
        return getLowStockProducts().size();
    }

    // Rechercher des produits
    public List<Product> searchProducts(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword) ||
                        p.getCategory().toLowerCase().contains(lowerKeyword) ||
                        p.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
}