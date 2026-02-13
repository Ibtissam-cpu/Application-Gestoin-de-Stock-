package com.example.tp1_jee.service;

import com.example.tp1_jee.model.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final String FILE_PATH = "users.dat";
    private List<User> users;

    public UserService() {
        this.users = loadUsers();
    }

    // Charger les utilisateurs depuis le fichier
    @SuppressWarnings("unchecked")
    private List<User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement des utilisateurs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Sauvegarder les utilisateurs dans le fichier
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des utilisateurs: " + e.getMessage());
        }
    }

    // Enregistrer un nouvel utilisateur
    public boolean register(String username, String password, String email) {
        // Vérifier si l'utilisateur existe déjà
        if (findUserByUsername(username) != null) {
            return false; // Utilisateur existe déjà
        }

        User newUser = new User(username, password, email);
        users.add(newUser);
        saveUsers();
        return true;
    }

    // Authentifier un utilisateur
    public boolean authenticate(String username, String password) {
        User user = findUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    // Trouver un utilisateur par nom d'utilisateur
    public User findUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Obtenir tous les utilisateurs
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}