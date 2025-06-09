package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Absensi {
    public int absensiId;
    public LocalDate absensiDate;
    public String absensiStatus;
    public int kelasId;
    public int siswaId;

    public Absensi(int absensiId, LocalDate absensiDate, String absensiStatus, int kelasId, int siswaId) {
        this.absensiId = absensiId;
        this.absensiDate = absensiDate;
        this.absensiStatus = absensiStatus;
        this.kelasId = kelasId;
        this.siswaId = siswaId;
    }

    public Absensi(ResultSet rs) throws SQLException {
        this.absensiId = rs.getInt("absensi_id");
        this.absensiDate = rs.getDate("absensi_date").toLocalDate();
        this.absensiStatus = rs.getString("absensi_status");
        this.kelasId = rs.getInt("kelas_id");
        this.siswaId = rs.getInt("siswa_id");

    }
}
