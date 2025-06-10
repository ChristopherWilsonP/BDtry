package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import com.example.bdtry.util.RaporRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class WaliKelasController {

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

    @FXML
    public void initialize() {
        mataPelajaranColumn.setCellValueFactory(data -> data.getValue().mataPelajaranProperty());
        utsColumn.setCellValueFactory(data -> data.getValue().utsProperty().asObject());
        uasColumn.setCellValueFactory(data -> data.getValue().uasProperty().asObject());

        raporTable.setItems(raporData);

        loadSiswa();
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
    private void handleTampilkanRapor() {
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
                String mapel = rs.getString("mata_pelajaran");
                int uts = rs.getInt("uts");
                int uas = rs.getInt("uas");
                raporData.add(new RaporRow(mapel, uts, uas));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}