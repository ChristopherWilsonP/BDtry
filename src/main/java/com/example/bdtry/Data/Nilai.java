package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Nilai {
    public int nilaiId;
    public String nilaiTestType;
    public int nilaiGrade;
    public int siswaId;
    public int matpelId;
    public int kelasId;

    public Nilai(int nilaiId, String nilaiTestType, int nilaiGrade, int siswaId, int matpelId, int kelasId) {
        this.nilaiId = nilaiId;
        this.nilaiTestType = nilaiTestType;
        this.nilaiGrade = nilaiGrade;
        this.siswaId = siswaId;
        this.matpelId = matpelId;
        this.kelasId = kelasId;
    }

    public Nilai(ResultSet rs) throws SQLException {
        this.nilaiId = rs.getInt("nilai_id");
        this.nilaiTestType = rs.getString("nilai_test_type");
        this.nilaiGrade = rs.getInt("nilai_grade");
        this.siswaId = rs.getInt("siswa_id");
        this.matpelId = rs.getInt("matpel_id");
        this.kelasId = rs.getInt("kelas_id");

    }
}
