package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;

public class AdminController {

    @FXML private TextField nisField, namaField, tempatLahirField, alamatField;
    @FXML private DatePicker tanggalLahirPicker;
    @FXML private ComboBox<String> kelasComboBox, waliKelasComboBox;

    @FXML private ComboBox<String> jadwalKelasCombo, hariCombo, guruCombo, mapelCombo;
    @FXML private TextField jamMulaiField, jamSelesaiField;

    @FXML
    public void initialize() {
        loadKelas();
        loadGuru();
        loadMapel();
        loadWaliKelas();
    }

    private void loadKelas() {
        kelasComboBox.getItems().clear();
        jadwalKelasCombo.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nama FROM kelas")) {
            while (rs.next()) {
                String namaKelas = rs.getString("nama");
                kelasComboBox.getItems().add(namaKelas);
                jadwalKelasCombo.getItems().add(namaKelas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGuru() {
        guruCombo.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nama FROM guru")) {
            while (rs.next()) {
                guruCombo.getItems().add(rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMapel() {
        mapelCombo.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nama FROM mata_pelajaran")) {
            while (rs.next()) {
                mapelCombo.getItems().add(rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadWaliKelas() {
        waliKelasComboBox.getItems().clear();
        try (Connection conn = MainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nama FROM wali_kelas")) {
            while (rs.next()) {
                waliKelasComboBox.getItems().add(rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTambahSiswa() {
        String nis = nisField.getText();
        String nama = namaField.getText();
        String tempatLahir = tempatLahirField.getText();
        String alamat = alamatField.getText();
        LocalDate tanggalLahir = tanggalLahirPicker.getValue();
        String kelas = kelasComboBox.getValue();

        try (Connection conn = MainDataSource.getConnection()) {
            conn.setAutoCommit(false);

            // insert siswa
            PreparedStatement siswaStmt = conn.prepareStatement(
                    "INSERT INTO siswa (nomor_induk, nama, tempat_lahir, tanggal_lahir, alamat, kelas_id) " +
                            "VALUES (?, ?, ?, ?, ?, (SELECT id FROM kelas WHERE nama = ?))");
            siswaStmt.setString(1, nis);
            siswaStmt.setString(2, nama);
            siswaStmt.setString(3, tempatLahir);
            siswaStmt.setDate(4, Date.valueOf(tanggalLahir));
            siswaStmt.setString(5, alamat);
            siswaStmt.setString(6, kelas);
            siswaStmt.executeUpdate();

            // insert login ke users
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, 'siswa')");
            userStmt.setString(1, nis);
            userStmt.setString(2, tanggalLahir.toString()); // password = tanggal lahir
            userStmt.executeUpdate();

            conn.commit();
            showAlert("Sukses", "Siswa dan login berhasil ditambahkan.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menambahkan siswa: " + e.getMessage());
        }
    }

    @FXML
    private void handleInputJadwal() {
        String kelas = jadwalKelasCombo.getValue();
        String hari = hariCombo.getValue();
        String jamAwal = jamMulaiField.getText();
        String jamAkhir = jamSelesaiField.getText();
        String guru = guruCombo.getValue();
        String mapel = mapelCombo.getValue();

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO jadwal (kelas_id, hari, jam_mulai, jam_selesai, guru_id, mata_pelajaran_id) " +
                            "VALUES ((SELECT id FROM kelas WHERE nama = ?), ?, ?, ?, " +
                            "(SELECT id FROM guru WHERE nama = ?), (SELECT id FROM mata_pelajaran WHERE nama = ?))");
            stmt.setString(1, kelas);
            stmt.setString(2, hari);
            stmt.setString(3, jamAwal);
            stmt.setString(4, jamAkhir);
            stmt.setString(5, guru);
            stmt.setString(6, mapel);
            stmt.executeUpdate();

            showAlert("Sukses", "Jadwal berhasil ditambahkan.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menambahkan jadwal: " + e.getMessage());
        }
    }

    @FXML
    private void handleSetWaliKelas() {
        String kelas = kelasComboBox.getValue();
        String wali = waliKelasComboBox.getValue();

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE kelas SET wali_kelas_id = (SELECT id FROM wali_kelas WHERE nama = ?) WHERE nama = ?");
            stmt.setString(1, wali);
            stmt.setString(2, kelas);
            stmt.executeUpdate();

            showAlert("Sukses", "Wali kelas berhasil di-set.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal set wali kelas: " + e.getMessage());
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