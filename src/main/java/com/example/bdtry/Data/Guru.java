package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Guru {
    public int id;
    public String name;

    public Guru(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Guru(ResultSet rs) throws SQLException {
        this.id = rs.getInt("guru_id");
        this.name = rs.getString("guru_name");
    }


}