package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WaliKelas {
    public int id;
    public String name;

    public WaliKelas(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public WaliKelas(ResultSet rs) throws SQLException {
        this.id = rs.getInt("wali_kelas_id");
        this.name = rs.getString("wali_kelas_name");
    }

    @Override
    public String toString() {
        return name;
    }
}
