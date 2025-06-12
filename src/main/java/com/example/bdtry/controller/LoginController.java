package com.example.bdtry.controller;

import com.example.bdtry.HelloApplication;
import com.example.bdtry.connection.MainDataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML

    boolean verifyCredentials(String username, String password, String role) throws SQLException {
        try (Connection c = MainDataSource.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND role = ?";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setString(1, username.trim());
            stmt.setString(2, role.trim());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (dbPassword.trim().equals(password.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getUserIdByUsername(String username) throws SQLException {
        try (Connection c = MainDataSource.getConnection()) {
            PreparedStatement stmt = c.prepareStatement("SELECT id FROM users WHERE username = ?");
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    @FXML
    void onLoginClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username == null || password == null || role == null ||
                username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Semua field harus diisi.");
            return;
        }

        try {
            if (verifyCredentials(username, password, role)) {
                HelloApplication app = HelloApplication.getApplicationInstance();
                if (role.equalsIgnoreCase("Admin")) {
                    app.getPrimaryStage().setTitle("Admin");
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("admin-view.fxml"));
                    Scene scene = new Scene(loader.load());
                    app.getPrimaryStage().setScene(scene);
                }
                else if(role.equalsIgnoreCase("Siswa")){
                    app.getPrimaryStage().setTitle("Siswa");
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("siswa-view.fxml"));
                    Scene scene = new Scene(loader.load());
                    app.getPrimaryStage().setScene(scene);
                    int userId = getUserIdByUsername(username);
                    com.example.bdtry.controller.StudentController studentController = loader.getController();
                    studentController.setStudentId(userId);
                }else if(role.equalsIgnoreCase("Guru")){
                    app.getPrimaryStage().setTitle("Guru");
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("guru-view.fxml"));
                    Scene scene = new Scene(loader.load());
                    app.getPrimaryStage().setScene(scene);
                    int userId = getUserIdByUsername(username);
                    com.example.bdtry.controller.GuruController guruController = loader.getController();
                    guruController.setGuruId(userId);
                }else{
                    app.getPrimaryStage().setTitle("Wali_Kelas");
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("waliKelas-view.fxml"));
                    Scene scene = new Scene(loader.load());
                    app.getPrimaryStage().setScene(scene);
                    int userId = getUserIdByUsername(username);
                    if (userId == -1) {
                        showAlert("Login Error", "User Not Found", "Cannot find user ID in database.");
                    }
                    else {
                        com.example.bdtry.controller.WaliKelasController waliKelasController = loader.getController();
                        waliKelasController.setWaliKelasId(userId);
                    }
                }

            } else {
                showAlert("Login Failed", "Invalid Credentials", "Please check your username, password, and role.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Connection Failed", "Could not connect to the database.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}