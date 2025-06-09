package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Kelas {
    public int id;
    public String name;
    public int WaliKelasID;

    public Kelas(int id, String name, int WaliKelasID) {
        this.id = id;
        this.name = name;
        this.WaliKelasID = WaliKelasID;
    }

    public Kelas(ResultSet rs) throws SQLException {
        this.id = rs.getInt("kelas_id");
        this.name = rs.getString("kelas_name");
        this.WaliKelasID = rs.getInt("wali_kelas_id");
    }


}