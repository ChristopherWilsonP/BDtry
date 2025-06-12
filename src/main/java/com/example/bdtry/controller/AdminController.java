package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class AdminController {

    @FXML
    private TextField siswa_idField, siswa_full_nameField, siswa_addressField;
    @FXML
    private DatePicker siswa_birth_datePicker;
    @FXML
    private ComboBox<String> kelas_idComboBox, wali_kelas_idComboBox;
    @FXML
    private ComboBox<String> jadwal_dateCombo, jadwal_durationCombo, guru_idCombo, matapel_nameCombo;
    @FXML
    private TextField jadwal_durationField;
    @FXML
    private ComboBox<String> siswa_idComboBox, new_kelas_idComboBox, new_wali_kelas_idComboBox;


    @FXML
    public void initialize() {
        loadKelas();
        loadGuru();
        loadMapel();
        loadWaliKelas();
        loadSiswaNaik();
        loadKelasBaru();
        loadWaliBaru();
    }

    private void loadKelas() {
        kelas_idComboBox.getItems().clear();
        jadwal_dateCombo.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT kelas_name FROM kelas")) {
            while (rs.next()) {
                String namaKelas = rs.getString("kelas_name");
                kelas_idComboBox.getItems().add(namaKelas);
                jadwal_dateCombo.getItems().add(namaKelas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGuru() {
        guru_idCombo.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT guru_name FROM guru")) {
            while (rs.next()) {
                guru_idCombo.getItems().add(rs.getString("guru_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMapel() {
        matapel_nameCombo.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT matpel_name FROM mata_pelajaran")) {
            while (rs.next()) {
                matapel_nameCombo.getItems().add(rs.getString("matpel_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadWaliKelas() {
        wali_kelas_idComboBox.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT wali_kelas_name FROM wali_kelas")) {
            while (rs.next()) {
                wali_kelas_idComboBox.getItems().add(rs.getString("wali_kelas_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSiswaNaik() {
        siswa_idComboBox.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT siswa_name FROM siswa")) {
            while (rs.next()) {
                siswa_idComboBox.getItems().add(rs.getString("siswa_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadKelasBaru() {
        new_kelas_idComboBox.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT kelas_name FROM kelas")) {
            while (rs.next()) {
                new_kelas_idComboBox.getItems().add(rs.getString("kelas_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSetWaliKelas() {
        String kelas = kelas_idComboBox.getValue();
        String wali = wali_kelas_idComboBox.getValue();

        if (kelas == null || wali == null) {
            showAlert("Peringatan", "Silakan pilih kelas dan wali kelas.");
            return;
        }

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE kelas SET wali_kelas_id = (SELECT wali_kelas_id FROM wali_kelas WHERE wali_kelas_name = ?) WHERE kelas_name = ?"
            );
            stmt.setString(1, wali);
            stmt.setString(2, kelas);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                showAlert("Sukses", "Wali kelas berhasil di-set.");
            } else {
                showAlert("Info", "Tidak ada kelas yang diupdate.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", STR."Gagal set wali kelas: \{e.getMessage()}");
        }
    }

    private void loadWaliBaru() {
        new_wali_kelas_idComboBox.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT wali_kelas_name FROM wali_kelas")) {
            while (rs.next()) {
                new_wali_kelas_idComboBox.getItems().add(rs.getString("wali_kelas_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSimpanSiswa() {
        handleTambahSiswa();
    }

    @FXML
    private void handleTambahSiswa() {
        String nis = siswa_idField.getText();
        String nama = siswa_full_nameField.getText();
        String alamat = siswa_addressField.getText();
        LocalDate tanggalLahir = siswa_birth_datePicker.getValue();
        String kelas = kelas_idComboBox.getValue();

        try (Connection conn = MainDataSource.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement siswaStmt = conn.prepareStatement(
                    "INSERT INTO siswa (siswa_id, siswa_name, siswa_birth_date, siswa_address, kelas_id) " +
                            "VALUES (?, ?, ?, ?, (SELECT kelas_id FROM kelas WHERE kelas_name = ?))");
            siswaStmt.setInt(1, Integer.parseInt(nis));
            siswaStmt.setString(2, nama);
            siswaStmt.setDate(3, Date.valueOf(tanggalLahir));
            siswaStmt.setString(4, alamat);
            siswaStmt.setString(5, kelas);
            siswaStmt.executeUpdate();

            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, 'SISWA')");
            siswaStmt.setInt(1, Integer.parseInt(nis));
            userStmt.setString(2, tanggalLahir.toString());
            userStmt.executeUpdate();

            conn.commit();
            showAlert("Sukses", "Siswa dan login berhasil ditambahkan.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menambahkan siswa: " + e.getMessage());
        }
    }

    @FXML
    private void handleSimpanJadwal() {
        handleInputJadwal();
    }

    @FXML
    private void handleInputJadwal() {
        String kelas = kelas_idComboBox.getValue();
        String hari = jadwal_dateCombo.getValue();
        String jadwalDurasi = jadwal_durationField.getText();
        String guru = guru_idCombo.getValue();
        String mapel = matapel_nameCombo.getValue();

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO jadwal (kelas_id, jadwal_day, jadwal_duration, matpel_id) " +
                            "VALUES ((SELECT kelas_id FROM kelas WHERE kelas_name = ?), ?, ?, " +
                            "(SELECT matpel_id FROM mata_pelajaran WHERE matpel_name = ?))");
            stmt.setString(1, kelas);
            stmt.setString(2, hari);
            stmt.setInt(3, Integer.parseInt(jadwalDurasi));
            stmt.setString(4, mapel);
            stmt.executeUpdate();

            showAlert("Sukses", "Jadwal berhasil ditambahkan.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", STR."Gagal menambahkan jadwal: \{e.getMessage()}");
        }
    }

    @FXML
    private void handleUpdateKelasSiswa() {
        handleUpdateWaliKelasSiswa();
    }

    @FXML
    private void handleUpdateWaliKelasSiswa() {
        String siswa = siswa_idComboBox.getValue();
        String kelasBaru = new_kelas_idComboBox.getValue();
        String waliBaru = new_wali_kelas_idComboBox.getValue();

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE siswa SET kelas_id = (SELECT kelas_id FROM kelas WHERE kelas_name = ?) WHERE siswa_name = ?");
            stmt.setString(1, kelasBaru);
            stmt.setString(2, siswa);
            stmt.executeUpdate();

            PreparedStatement waliStmt = conn.prepareStatement(
                    "UPDATE kelas SET wali_kelas_id = (SELECT wali_kelas_id FROM wali_kelas WHERE wali_kelas_name = ?) WHERE kelas_name = ?");
            waliStmt.setString(1, waliBaru);
            waliStmt.setString(2, kelasBaru);
            waliStmt.executeUpdate();

            showAlert("Sukses", "Siswa dan wali kelas berhasil diperbarui.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", STR."Gagal memperbarui kelas siswa: \{e.getMessage()}");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/bdtry/view/login-view.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) kelas_idComboBox.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
