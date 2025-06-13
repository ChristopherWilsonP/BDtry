package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudentController {

    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label classLabel;
    @FXML private Label addressLabel;

    @FXML private TableView<ScheduleRow> scheduleTable;
    @FXML private TableColumn<ScheduleRow, String> dayColumn;
    @FXML private TableColumn<ScheduleRow, String> durationColumn;
    @FXML private TableColumn<ScheduleRow, String> subjectColumn;

    @FXML private TableView<ScoreRow> scoreTable;
    @FXML private TableColumn<ScoreRow, String> subjectScoreColumn;
    @FXML private TableColumn<ScoreRow, Integer> utsColumn;
    @FXML private TableColumn<ScoreRow, Integer> uasColumn;

    private int studentId;

    @FXML
    public void setStudentId(int id) {
        try(Connection connection = MainDataSource.getConnection()){
            String sql = "SELECT siswa_id FROM siswa WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                this.studentId = rs.getInt("siswa_id");
            } else {
                System.out.println("No siswa found for user_id: " + id);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        this.studentId = id;
        initializeTableColumns();
        loadBiodata();
        loadSchedule();
        loadScores();
    }

    private void initializeTableColumns() {
        // Bind columns for schedule
        dayColumn.setCellValueFactory(cellData -> cellData.getValue().dayProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        subjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());

        // Bind columns for scores
        subjectScoreColumn.setCellValueFactory(cellData -> cellData.getValue().namaMapelProperty());
        utsColumn.setCellValueFactory(cellData -> cellData.getValue().utsProperty().asObject());
        uasColumn.setCellValueFactory(cellData -> cellData.getValue().uasProperty().asObject());
//        subjectScoreColumn.setCellValueFactory(new PropertyValueFactory<>("matpelName"));
//        utsColumn.setCellValueFactory(new PropertyValueFactory<>("uts"));
//        uasColumn.setCellValueFactory(new PropertyValueFactory<>("uas"));
    }

    private void loadBiodata() {// benar
        try (Connection conn = MainDataSource.getConnection()) {
            String sql = "SELECT s.siswa_id, s.siswa_name, s.siswa_birth_date, s.siswa_address, k.kelas_name " +
                    "FROM siswa s " +
                    "JOIN kelas k ON s.kelas_id = k.kelas_id " +
                    "WHERE s.siswa_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idLabel.setText(String.valueOf(rs.getInt("siswa_id")));
                nameLabel.setText(rs.getString("siswa_name"));
                LocalDate date = rs.getDate("siswa_birth_date").toLocalDate();
                birthDateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                addressLabel.setText(rs.getString("siswa_address"));
                classLabel.setText(rs.getString("kelas_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadSchedule() {
        ObservableList<ScheduleRow> data = FXCollections.observableArrayList();
        try (Connection conn = MainDataSource.getConnection()) {
            String sql = "SELECT j.jadwal_day, j.jadwal_duration, m.matpel_name " +
                    "FROM jadwal j " +
                    "JOIN mata_pelajaran m ON j.matpel_id = m.matpel_id " +
                    "JOIN siswa s ON s.kelas_id = j.kelas_id " +
                    "WHERE s.siswa_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String hari = rs.getString("jadwal_day");
                String durasi = rs.getInt("jadwal_duration")+" menit";
                String mapel = rs.getString("matpel_name");
                data.add(new ScheduleRow(hari, durasi, mapel));
            }
            
            scheduleTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadScores() {
        ObservableList<ScoreRow> data = FXCollections.observableArrayList();
        try (Connection conn = MainDataSource.getConnection()) {
            String sql =  "SELECT m.matpel_name, " +
                    "MAX(CASE WHEN n.nilai_test_type = 'uts' THEN n.nilai_grade END) AS uts, " +
                    "MAX(CASE WHEN n.nilai_test_type = 'uas' THEN n.nilai_grade END) AS uas " +
                    "FROM nilai n " +
                    "JOIN mata_pelajaran m ON m.matpel_id = n.matpel_id " +
                    "WHERE n.siswa_id = ? " +
                    "GROUP BY m.matpel_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new ScoreRow(
                        rs.getString("matpel_name"),
                        rs.getInt("uts"),
                        rs.getInt("uas")
                ));
            }
            scoreTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Inner Classes for TableView Rows =======

    public static class ScheduleRow {
        private final SimpleStringProperty day;
        private final SimpleStringProperty duration;
        private final SimpleStringProperty subject;

        public ScheduleRow(String day, String duration, String subject) {
            this.day = new SimpleStringProperty(day);
            this.duration = new SimpleStringProperty(duration);
            this.subject = new SimpleStringProperty(subject);
        }

        public StringProperty dayProperty() { return day; }
        public StringProperty durationProperty() { return duration; }
        public StringProperty subjectProperty() { return subject; }
    }

    public static class ScoreRow {
        private final SimpleStringProperty namaMapel;
        private final SimpleIntegerProperty uts;
        private final SimpleIntegerProperty uas;

        public ScoreRow(String namaMapel, int uts, int uas) {
            this.namaMapel = new SimpleStringProperty(namaMapel);
            this.uts = new SimpleIntegerProperty(uts);
            this.uas = new SimpleIntegerProperty(uas);
        }
        public StringProperty namaMapelProperty() { return namaMapel; }
        public IntegerProperty utsProperty() { return uts; }
        public IntegerProperty uasProperty() { return uas; }

    }
}