package com.example.bdtry.controller;

import com.example.bdtry.connection.MainDataSource;
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
    @FXML private TableView<ScoreRow> scoreTable;

    private int studentId;

    public void setStudentId(int id) {
        this.studentId = id;
        loadBiodata();
        loadSchedule();
        loadScores();
    }

    private void loadBiodata() {
        try (Connection conn = MainDataSource.getConnection()) {
            String sql = """
                SELECT s.name, s.address, k.name as class_name
                FROM siswa s
                JOIN kelas k ON s.class_id = k.id
                WHERE s.id = ?
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameLabel.setText(rs.getString("name"));
                addressLabel.setText(rs.getString("address"));
                classLabel.setText(rs.getString("class_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSchedule() {
        // Load schedule based on student's class
    }

    private void loadScores() {
        // Load exam scores per subject
    }

    public static class ScheduleRow {
        private  String day;
        private  String time;
        private  String subject;
        private  String teacher;
        // constructor, getters...

        public ScheduleRow(String day, String time, String subject, String teacher) {
            this.day = day;
            this.time = time;
            this.subject = subject;
            this.teacher = teacher;
        }

        public String getDay() {
            return day;
        }

        public String getTime() {
            return time;
        }

        public String getSubject() {
            return subject;
        }

        public String getTeacher() {
            return teacher;
        }
    }

    public static class ScoreRow {
        private  String subject;
        private  int uts;
        private  int uas;

        public ScoreRow(String subject, int uts, int uas) {
            this.subject = subject;
            this.uts = uts;
            this.uas = uas;
        }

        public String getSubject() {
            return subject;
        }

        public int getUts() {
            return uts;
        }

        public int getUas() {
            return uas;
        }
    }
}
