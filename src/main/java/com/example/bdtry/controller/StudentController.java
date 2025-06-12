package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class StudentController {

    @FXML private Label nameLabel;
    @FXML private Label classLabel;
    @FXML private Label addressLabel;

    @FXML private TableView<ScheduleRow> scheduleTable;
    @FXML private TableColumn<ScheduleRow, String> dayColumn;
    @FXML private TableColumn<ScheduleRow, String> timeColumn;
    @FXML private TableColumn<ScheduleRow, String> subjectColumn;
    @FXML private TableColumn<ScheduleRow, String> teacherColumn;

    @FXML private TableView<ScoreRow> scoreTable;
    @FXML private TableColumn<ScoreRow, String> subjectScoreColumn;
    @FXML private TableColumn<ScoreRow, Integer> utsColumn;
    @FXML private TableColumn<ScoreRow, Integer> uasColumn;

    private int studentId;

    public void setStudentId(int id) {
        this.studentId = id;
        initializeTableColumns();
        loadBiodata();
        loadSchedule();
        loadScores();
    }

    private void initializeTableColumns() {
        // Bind columns for schedule
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));

        // Bind columns for scores
        subjectScoreColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        utsColumn.setCellValueFactory(new PropertyValueFactory<>("uts"));
        uasColumn.setCellValueFactory(new PropertyValueFactory<>("uas"));
    }

    private void loadBiodata() {
        try (Connection conn = MainDataSource.getConnection()) {
            String sql = """
                SELECT s.siswa_name, s.siswa_address, k.kelas_name
                FROM siswa s
                JOIN kelas k ON s.kelas_id = k.kelas_id
                WHERE s.siswa_id = ?
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameLabel.setText(rs.getString("siswa_name"));
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
            String sql = """
                SELECT j.jadwal_day, j.jadwal_duration, m.matpel_name, g.guru_name
                FROM jadwal j
                JOIN mata_pelajaran m ON j.matpel_id = m.matpel_id
                JOIN guru g ON m.guru_id = g.guru_id
                JOIN siswa s ON s.kelas_id = j.kelas_id
                WHERE s.siswa_id = ?
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String waktu = rs.getInt("jadwal_duration") + " menit";
                data.add(new ScheduleRow(
                        rs.getString("jadwal_day"),
                        waktu,
                        rs.getString("matpel_name"),
                        rs.getString("guru_name")
                ));
            }
            scheduleTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadScores() {
        ObservableList<ScoreRow> data = FXCollections.observableArrayList();
        try (Connection conn = MainDataSource.getConnection()) {
            String sql = """
                SELECT m.matpel_name,
                       MAX(CASE WHEN n.nilai_test_type = 'UTS' THEN n.nilai_grade END) AS uts,
                       MAX(CASE WHEN n.nilai_test_type = 'UAS' THEN n.nilai_grade END) AS uas
                FROM nilai n
                JOIN mata_pelajaran m ON n.matpel_id = m.matpel_id
                WHERE n.siswa_id = ?
                GROUP BY m.matpel_name
                """;
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
        private final String day;
        private final String time;
        private final String subject;
        private final String teacher;

        public ScheduleRow(String day, String time, String subject, String teacher) {
            this.day = day;
            this.time = time;
            this.subject = subject;
            this.teacher = teacher;
        }

        public String getDay() { return day; }
        public String getTime() { return time; }
        public String getSubject() { return subject; }
        public String getTeacher() { return teacher; }
    }

    public static class ScoreRow {
        private final String subject;
        private final int uts;
        private final int uas;

        public ScoreRow(String subject, int uts, int uas) {
            this.subject = subject;
            this.uts = uts;
            this.uas = uas;
        }

        public String getSubject() { return subject; }
        public int getUts() { return uts; }
        public int getUas() { return uas; }
    }
}