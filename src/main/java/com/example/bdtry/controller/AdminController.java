package com.example.bdtry.controller;


import com.example.bdtry.connection.MainDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController {

    @FXML private TextField siswaNameField;
    @FXML private DatePicker siswaBirthDatePicker;
    @FXML private TextField siswaAddressField;
    @FXML private ComboBox<String> kelasComboBox;
    @FXML private ComboBox<String> waliKelasComboBox;
    @FXML private ComboBox<String> ekskulComboBox;
    @FXML private Label siswaStatusLabel;

    @FXML private ComboBox<String> kelasJadwalComboBox;
    @FXML private ComboBox<String> hariComboBox;
    @FXML private TextField jamMulaiField;
    @FXML private TextField jamSelesaiField;
    @FXML private ComboBox<String> guruComboBox;
    @FXML private ComboBox<String> matpelComboBox;
    @FXML private Label jadwalStatusLabel;

    @FXML private ComboBox<String> siswaNaikComboBox;
    @FXML private ComboBox<String> kelasBaruComboBox;
    @FXML private ComboBox<String> waliBaruComboBox;
    @FXML private Label updateStatusLabel;

    @FXML
    public void initialize() {
        loadComboBoxes();
    }

    private void loadComboBoxes() {
        try (Connection conn = MainDataSource.getConnection()) {
            ObservableList<String> kelasList = FXCollections.observableArrayList();
            ObservableList<String> waliList = FXCollections.observableArrayList();
            ObservableList<String> guruList = FXCollections.observableArrayList();
            ObservableList<String> matpelList = FXCollections.observableArrayList();
            ObservableList<String> ekskulList = FXCollections.observableArrayList();
            ObservableList<String> siswaList = FXCollections.observableArrayList();
            ObservableList<String> hariList = FXCollections.observableArrayList("Senin", "Selasa", "Rabu", "Kamis", "Jumat");

            ResultSet rs = conn.createStatement().executeQuery("SELECT kelas_name FROM kelas");
            while (rs.next()) kelasList.add(rs.getString(1));
            rs = conn.createStatement().executeQuery("SELECT wali_kelas_name FROM wali_kelas");
            while (rs.next()) waliList.add(rs.getString(1));
            rs = conn.createStatement().executeQuery("SELECT guru_name FROM guru");
            while (rs.next()) guruList.add(rs.getString(1));
            rs = conn.createStatement().executeQuery("SELECT matpel_name FROM mata_pelajaran");
            while (rs.next()) matpelList.add(rs.getString(1));
            rs = conn.createStatement().executeQuery("SELECT ekskur_name FROM ekstra_kulikuler");
            while (rs.next()) ekskulList.add(rs.getString(1));
            rs = conn.createStatement().executeQuery("SELECT siswa_name FROM siswa");
            while (rs.next()) siswaList.add(rs.getString(1));

            kelasComboBox.setItems(kelasList);
            kelasJadwalComboBox.setItems(kelasList);
            kelasBaruComboBox.setItems(kelasList);

            waliKelasComboBox.setItems(waliList);
            waliBaruComboBox.setItems(waliList);

            guruComboBox.setItems(guruList);
            matpelComboBox.setItems(matpelList);
            ekskulComboBox.setItems(ekskulList);
            siswaNaikComboBox.setItems(siswaList);

            hariComboBox.setItems(hariList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSimpanSiswa(ActionEvent event) {
        try (Connection conn = MainDataSource.getConnection()) {
            String sql = "INSERT INTO siswa(siswa_name, siswa_birth_date, siswa_address, kelas_id, wali_kelas_id, ekskur_id)\n" +
                    "VALUES (?, ?, ?, \n" +
                    "(SELECT kelas_id FROM kelas WHERE kelas_name = ?),\n" +
                    "(SELECT wali_kelas_id FROM wali_kelas WHERE wali_kelas_name = ?),\n" +
                    "(SELECT ekskur_id FROM ekstra_kulikuler WHERE ekskur_name = ?))";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, siswaNameField.getText());
            stmt.setDate(2, java.sql.Date.valueOf(siswaBirthDatePicker.getValue()));
            stmt.setString(3, siswaAddressField.getText());
            stmt.setString(4, kelasComboBox.getValue());
            stmt.setString(5, waliKelasComboBox.getValue());
            stmt.setString(6, ekskulComboBox.getValue());
            stmt.executeUpdate();

            siswaStatusLabel.setText("Data siswa berhasil disimpan!");
        } catch (Exception e) {
            siswaStatusLabel.setText("Gagal menyimpan siswa: " + e.getMessage());
        }
    }

    @FXML
    private void handleSimpanJadwal(ActionEvent event) {
        try (Connection conn = MainDataSource.getConnection()) {
            int duration = getDuration(jamMulaiField.getText(), jamSelesaiField.getText());
            String sql = "INSERT INTO jadwal(jadwal_day, jadwal_duration, kelas_id, matpel_id) VALUES (?, ?, \n" +
                    "(SELECT kelas_id FROM kelas WHERE kelas_name = ?),\n" +
                    "(SELECT matpel_id FROM mata_pelajaran WHERE matpel_name = ?))";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hariComboBox.getValue());
            stmt.setInt(2, duration);
            stmt.setString(3, kelasJadwalComboBox.getValue());
            stmt.setString(4, matpelComboBox.getValue());
            stmt.executeUpdate();

            jadwalStatusLabel.setText("Jadwal berhasil ditambahkan.");
        } catch (Exception e) {
            jadwalStatusLabel.setText("Gagal menambahkan jadwal: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateKelasSiswa(ActionEvent event) {
        try (Connection conn = MainDataSource.getConnection()) {
            String sql = "UPDATE siswa SET kelas_id = (SELECT kelas_id FROM kelas WHERE kelas_name = ?),\n" +
                    "wali_kelas_id = (SELECT wali_kelas_id FROM wali_kelas WHERE wali_kelas_name = ?)\n" +
                    "WHERE siswa_name = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kelasBaruComboBox.getValue());
            stmt.setString(2, waliBaruComboBox.getValue());
            stmt.setString(3, siswaNaikComboBox.getValue());
            stmt.executeUpdate();

            updateStatusLabel.setText("Kelas siswa diperbarui.");
        } catch (Exception e) {
            updateStatusLabel.setText("Gagal memperbarui kelas siswa: " + e.getMessage());
        }
    }

    private int getDuration(String start, String end) {
        String[] s = start.split(":"), e = end.split(":");
        int startMin = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
        int endMin = Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1]);
        return endMin - startMin;
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Logout logic here: kembali ke login scene
    }
}
