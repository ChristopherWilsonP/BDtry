package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import com.example.bdtry.util.RaporRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.w3c.dom.events.MouseEvent;

import java.sql.*;

public class WaliKelasController {

    @FXML
    private ComboBox<String> comboKelas;
    @FXML
    private ComboBox<String> siswaComboBox;
    @FXML
    private TableView<RaporRow> raporTable;
    @FXML
    private TableColumn<RaporRow, String> mataPelajaranColumn;
    @FXML
    private TableColumn<RaporRow, Integer> utsColumn;
    @FXML
    private TableColumn<RaporRow, Integer> uasColumn;

    private final ObservableList<RaporRow> raporData = FXCollections.observableArrayList();

    private int waliKelasId;

    public void setWaliKelasId(int id) {
        this.waliKelasId = id;
        loadSiswa();
    }
    
    @FXML
    private void onMakeReport(MouseEvent Event){
        String selectedKelas = comboKelas.getValue();
        if(selectedKelas == null){
            showAlert("Error","Validasi","Pilih kelas terlebih dahulu");
            return;
        }
        ObservableList<RaporRow> data = FXCollections.observableArrayList();
        try (Connection connection = MainDataSource.getConnection()){
            String query = " SELECT s.nama AS nama_siswa, m.nama_mapel, n.uts, n.uas  " +
                            "FROM nilai n\n " +
                            "JOIN siswa s ON n.siswa_id = s.id\n " +
                            "JOIN mata_pelajaran m ON n.mapel_id = m.id\n " +
                            "JOIN kelas k ON s.kelas_id = k.id\n " +
                            "WHERE k.nama_kelas = ?\n" +
                            "ORDER BY s.nama, m.nama_mapel";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, selectedKelas);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new RaporRow(
                        rs.getString("nama_siswa"),
                        rs.getString("nama_mapel"),
                        rs.getInt("uts"),
                        rs.getInt("uas")
                ));
            }
        } catch (SQLException e){
            showAlert("Error","Database error","Gagal mengambil data rapor");
        }
    }



    private void loadSiswa() {
        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nama FROM siswa");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                siswaComboBox.getItems().add(rs.getInt("id") + " - " + rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTampilanRapor() {
        raporData.clear();
        String selected = siswaComboBox.getValue();
        if (selected == null) return;

        int siswaId = Integer.parseInt(selected.split(" - ")[0]);

        try (Connection conn = MainDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT mp.nama AS mata_pelajaran, n.uts, n.uas " +
                            "FROM nilai n " +
                            "JOIN mata_pelajaran mp ON n.mata_pelajaran_id = mp.id " +
                            "WHERE n.siswa_id = ?"
            );
            stmt.setInt(1, siswaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String siswa = rs.getString("nama_siswa");
                String mapel = rs.getString("mata_pelajaran");
                int uts = rs.getInt("uts");
                int uas = rs.getInt("uas");
                raporData.add(new RaporRow(siswa,mapel, uts, uas));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public int getWaliKelasId() {
        return waliKelasId;
    }
}