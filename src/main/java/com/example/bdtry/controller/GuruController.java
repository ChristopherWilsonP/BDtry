package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class GuruController {

    @FXML private Label labelNamaGuru;
    @FXML private TableView<JadwalItem> tabelJadwal;
    @FXML private TableColumn<JadwalItem, String> colHari;
    @FXML private TableColumn<JadwalItem, String> colJam;
    @FXML private TableColumn<JadwalItem, String> colKelas;
    @FXML private TableColumn<JadwalItem, String> colPelajaran;

    @FXML private ComboBox<String> comboKelas;
    @FXML private ComboBox<String> comboPelajaran;
    @FXML private ComboBox<String> comboSiswa;
    @FXML private ComboBox<String> comboUjian;
    @FXML private TextField inputNilai;
    @FXML private Button btnSimpan;

    private int guruId;

    public void setGuruId(int id) {
        this.guruId = id;
        loadGuruInfo();
        loadJadwal();
        loadComboData();
    }

    private void loadGuruInfo() {
        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT nama FROM guru WHERE id_user = ?");
            stmt.setInt(1, guruId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                labelNamaGuru.setText(rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadJadwal() {
        ObservableList<JadwalItem> data = FXCollections.observableArrayList();
        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT j.hari, j.jam_mulai, j.jam_selesai, k.nama_kelas, m.nama_pelajaran
                FROM jadwal j
                JOIN guru g ON j.id_guru = g.id
                JOIN kelas k ON j.id_kelas = k.id
                JOIN mata_pelajaran m ON j.id_pelajaran = m.id
                WHERE g.id_user = ?
            """);
            stmt.setInt(1, guruId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new JadwalItem(
                        rs.getString("hari"),
                        rs.getString("jam_mulai") + " - " + rs.getString("jam_selesai"),
                        rs.getString("nama_kelas"),
                        rs.getString("nama_pelajaran")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        colHari.setCellValueFactory(cell -> cell.getValue().hariProperty());
        colJam.setCellValueFactory(cell -> cell.getValue().jamProperty());
        colKelas.setCellValueFactory(cell -> cell.getValue().kelasProperty());
        colPelajaran.setCellValueFactory(cell -> cell.getValue().pelajaranProperty());
        tabelJadwal.setItems(data);
    }

    private void loadComboData() {
        comboUjian.getItems().addAll("UTS", "UAS");

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT DISTINCT k.nama_kelas, m.nama_pelajaran
                FROM jadwal j
                JOIN kelas k ON j.id_kelas = k.id
                JOIN mata_pelajaran m ON j.id_pelajaran = m.id
                WHERE j.id_guru = (SELECT id FROM guru WHERE id_user = ?)
            """);
            stmt.setInt(1, guruId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String kelas = rs.getString("nama_kelas");
                String pelajaran = rs.getString("nama_pelajaran");
                if (!comboKelas.getItems().contains(kelas)) comboKelas.getItems().add(kelas);
                if (!comboPelajaran.getItems().contains(pelajaran)) comboPelajaran.getItems().add(pelajaran);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        comboKelas.setOnAction(e -> loadSiswaUntukKelas());
    }

    private void loadSiswaUntukKelas() {
        comboSiswa.getItems().clear();
        String kelas = comboKelas.getValue();
        if (kelas == null) return;

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT s.id, s.nama, s.nomor_induk
                FROM siswa s
                JOIN kelas k ON s.id_kelas = k.id
                WHERE k.nama_kelas = ?
            """);
            stmt.setString(1, kelas);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboSiswa.getItems().add(rs.getInt("id") + " - " + rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSimpanNilai() {
        String siswaData = comboSiswa.getValue();
        String pelajaran = comboPelajaran.getValue();
        String ujian = comboUjian.getValue();
        String nilaiText = inputNilai.getText();

        if (siswaData == null || pelajaran == null || ujian == null || nilaiText.isEmpty()) {
            showAlert("Harap isi semua field!");
            return;
        }

        try {
            int idSiswa = Integer.parseInt(siswaData.split(" - ")[0]);
            double nilai = Double.parseDouble(nilaiText);

            try (Connection conn = MainDataSource.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("""
                    INSERT INTO nilai (id_siswa, id_pelajaran, jenis_ujian, nilai)
                    VALUES (?, 
                        (SELECT id FROM mata_pelajaran WHERE nama_pelajaran = ?), 
                        ?, ?)
                    ON CONFLICT (id_siswa, id_pelajaran, jenis_ujian) DO UPDATE 
                    SET nilai = EXCLUDED.nilai
                """);
                stmt.setInt(1, idSiswa);
                stmt.setString(2, pelajaran);
                stmt.setString(3, ujian);
                stmt.setDouble(4, nilai);
                stmt.executeUpdate();
                showAlert("Nilai berhasil disimpan.");
                inputNilai.clear();
            }

        } catch (Exception e) {
            showAlert("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class untuk item jadwal
    public static class JadwalItem {
        private final SimpleStringProperty hari, jam, kelas, pelajaran;

        public JadwalItem(String hari, String jam, String kelas, String pelajaran) {
            this.hari = new SimpleStringProperty(hari);
            this.jam = new SimpleStringProperty(jam);
            this.kelas = new SimpleStringProperty(kelas);
            this.pelajaran = new SimpleStringProperty(pelajaran);
        }

        public SimpleStringProperty hariProperty() { return hari; }
        public SimpleStringProperty jamProperty() { return jam; }
        public SimpleStringProperty kelasProperty() { return kelas; }
        public SimpleStringProperty pelajaranProperty() { return pelajaran; }
    }
}