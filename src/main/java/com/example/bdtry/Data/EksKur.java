package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EksKur {
    public int ekskurId;
    public String ekskurName;
    public int ekskurDuration;

    public EksKur(int ekskurDuration, String ekskurName, int ekskurId) {
        this.ekskurDuration = ekskurDuration;
        this.ekskurName = ekskurName;
        this.ekskurId = ekskurId;
    }

    public EksKur(ResultSet rs) throws SQLException {
        this.ekskurId = rs.getInt("ekskur_id");
        this.ekskurName = rs.getString("ekskur_name");
        this.ekskurDuration = rs.getInt("ekskur_duration");
    }
}
