package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import com.example.bdtry.util.LoginSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.sql.*;
import javafx.scene.control.TextField;


public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("Semua field harus diisi!");
            return;
        }

        try (Connection conn = MainDataSource.getConnection()) {
            String query = buildQuery(role);
            if (query == null) {
                errorLabel.setText("Peran tidak dikenali.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt(1);
                LoginSession.startSession(userId, username, role);
                // TODO: Ganti ke scene sesuai role
                errorLabel.setText("Login berhasil");
            } else {
                errorLabel.setText("Login gagal. Periksa username/password.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Kesalahan koneksi: " + e.getMessage());
        }
    }

    private String buildQuery(String role) {
        return switch (role) {
            case "ADMIN" -> "SELECT admin_id FROM admins WHERE admin_username = ? AND admin_username = ?";
            case "SISWA" -> "SELECT siswa_id FROM siswa WHERE siswa_id::text = ? AND siswa_birth_date::text = ?";
            case "GURU" -> "SELECT guru_id FROM guru WHERE guru_name = ? AND guru_name = ?";
            case "WALI_KELAS" -> "SELECT wali_kelas_id FROM wali_kelas WHERE wali_kelas_name = ? AND wali_kelas_name = ?";
            default -> null;
        };
    }
}

