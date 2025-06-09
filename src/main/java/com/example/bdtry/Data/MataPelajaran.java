package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MataPelajaran {
    public int matpelId;
    public String matpelName;
    public int guruId;


    public MataPelajaran(int matpelId, String matpelName, int guruId) {
        this.matpelId = matpelId;
        this.matpelName = matpelName;
        this.guruId = guruId;
    }

    public MataPelajaran(ResultSet rs) throws SQLException {
        this.matpelId = rs.getInt("matpel_id");
        this.matpelName = rs.getString("matpel_name");
        this.guruId = rs.getInt("guru_id");
    }
}
