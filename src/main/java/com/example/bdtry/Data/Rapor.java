package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Rapor {
    public int raporId;
    public LocalDate raporPrintDate;
    public String raporNote;
    public int siswaId;
    public int nilaiId;
    public int waliKelasId;

    public Rapor(int raporId, LocalDate raporPrintDate, String raporNote,
                 int siswaId, int nilaiId, int waliKelasId) {
        this.raporId = raporId;
        this.raporPrintDate = raporPrintDate;
        this.raporNote = raporNote;
        this.siswaId = siswaId;
        this.nilaiId = nilaiId;
        this.waliKelasId = waliKelasId;
    }
    public Rapor(ResultSet rs) throws SQLException {
        this.raporId = rs.getInt("rapor_id");
        this.raporPrintDate = rs.getDate("rapor_print_date").toLocalDate();
        this.raporNote = rs.getString("rapor_note");
        this.siswaId = rs.getInt("siswa_id");
        this.nilaiId = rs.getInt("nilai_id");
        this.waliKelasId = rs.getInt("wali_kelas_id");

    }

}
