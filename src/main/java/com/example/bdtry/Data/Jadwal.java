package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Jadwal {
    public int jadwalId;
    public String jadwalDay;
    public int jadwalDuration;
    public int kelasId;
    public int matpelId;

    public Jadwal(int jadwalId, String jadwalDay, int jadwalDuration, int kelasId, int matpelId) {
        this.jadwalId = jadwalId;
        this.jadwalDay = jadwalDay;
        this.jadwalDuration = jadwalDuration;
        this.kelasId = kelasId;
        this.matpelId = matpelId;
    }

    public Jadwal(ResultSet rs) throws SQLException {
        this.jadwalId = rs.getInt("jadwal_id");
        this.jadwalDay = rs.getString("jadwal_day");
        this.jadwalDuration = rs.getInt("jadwal_duration");
        this.kelasId = rs.getInt("kelas_id");
        this.matpelId = rs.getInt("matpel_id");

    }
}
