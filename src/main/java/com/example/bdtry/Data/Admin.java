package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {
    public int id;
    public String name;
    public String username;

    public Admin(int id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public Admin(ResultSet rs) throws SQLException {
        this.id = rs.getInt("admin_id");
        this.name = rs.getString("admin_name");
        this.username = rs.getString("admin_username");
    }

    @Override
    public String toString() {
        return name;
    }
}
