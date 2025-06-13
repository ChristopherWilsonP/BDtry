package com.example.bdtry.controller;

import com.example.bdtry.HelloApplication;
import com.example.bdtry.connection.MainDataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML

    boolean verifyCredentials(String username, String password, String role) throws SQLException {
        try (Connection c = MainDataSource.getConnection()) {
            String table = switch (role.toLowerCase()) {
            case "admin" -> "admins";
            case "siswa" -> "siswa";
            case "guru" -> "guru";
            case "wali_kelas" -> "wali_kelas";
            default -> null;
        };
            String userColumn = switch (role.toLowerCase()){
                case "admin" -> "admin_username";
                case "siswa" -> "siswa_username";
                case "guru" -> "guru_username";
                case "wali_kelas" -> "wali_username";
                default -> null;
            };
            String userPass = switch (role.toLowerCase()){
                case "admin" -> "admin_pass";
                case "siswa" -> "siswa_pass";
                case "guru" -> "guru_pass";
                case "wali_kelas" -> "wali_kelas_pass";
                default -> null;
            };

        if (table == null||userColumn == null) return false;

        String query = "SELECT * FROM " + table + " WHERE " + userColumn + " = ?";
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setString(1, username.trim());

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String dbPassword = rs.getString(userPass);//kene
            return dbPassword.trim().equals(password.trim());
        }
    }
        return false;
    }

    private int getUserIdByUsername(String username, String role) throws SQLException {
        try (Connection c = MainDataSource.getConnection()) {
            String table = switch (role.toLowerCase()) {
                case "admin" -> "admins";
                case "siswa" -> "siswa";
                case "guru" -> "guru";
                case "wali_kelas" -> "wali_kelas";
                default -> null;
            };

            String idColumn = switch (role.toLowerCase()) {
                case "admin" -> "admin_id";
                case "siswa" -> "siswa_id";
                case "guru" -> "guru_id";
                case "wali_kelas" -> "wali_kelas_id";
                default -> null;
            };
            String userColumn = switch (role.toLowerCase()){
                case "admin" -> "admin_username";
                case "siswa" -> "siswa_username";
                case "guru" -> "guru_username";
                case "wali_kelas" -> "wali_username";
                default -> null;
            };

            if (table == null || idColumn == null||userColumn == null) return -1;

            PreparedStatement stmt = c.prepareStatement("SELECT " + idColumn + " FROM " + table + " WHERE " + userColumn +" = ?");
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(idColumn);
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
//        try{
//            if(){
//
//            }
//        }catch (SQLException e){
//            showAlert("Database Error", "Connection Failed", "Could not connect to the database.");
//        }catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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
                    int userId = getUserIdByUsername(username,role);
                    com.example.bdtry.controller.StudentController studentController = loader.getController();
                    studentController.setStudentId(userId);
                }else if(role.equalsIgnoreCase("Guru")){
                    app.getPrimaryStage().setTitle("Guru");
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("guru-view.fxml"));
                    Scene scene = new Scene(loader.load());
                    app.getPrimaryStage().setScene(scene);
                    int userId = getUserIdByUsername(username,role);
                    com.example.bdtry.controller.GuruController guruController = loader.getController();
                    guruController.setGuruId(userId);
                }else{
                    app.getPrimaryStage().setTitle("Wali_Kelas");
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("waliKelas-view.fxml"));
                    Scene scene = new Scene(loader.load());
                    app.getPrimaryStage().setScene(scene);
                    int userId = getUserIdByUsername(username,role);
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
            System.out.println(e.getMessage()+" ,, "+e.getCause());
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