package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JadwalGuru {
    public int jadwalGuruId;
    public int jadwalGuruDuration;
    public String jadwalGuruDay;
    public int guruId;
    public int waliKelasId;

    public JadwalGuru(int jadwalGuruId, int jadwalGuruDuration, String jadwalGuruDay, int guruId, int waliKelasId) {
        this.jadwalGuruId = jadwalGuruId;
        this.jadwalGuruDuration = jadwalGuruDuration;
        this.jadwalGuruDay = jadwalGuruDay;
        this.guruId = guruId;
        this.waliKelasId = waliKelasId;
    }

    public JadwalGuru(ResultSet rs) throws SQLException {
        this.jadwalGuruId = rs.getInt("jadwal_guru_id");
        this.jadwalGuruDay = rs.getString("jadwal_guru_day");
        this.jadwalGuruDuration = rs.getInt("jadwal_guru_duration");
        this.guruId = rs.getInt("guru_id");
        this.waliKelasId = rs.getInt("wali_kelas_id");

    }
}

