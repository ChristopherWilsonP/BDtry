package com.example.bdtry.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Murid {
    public int siswaId;
    public String siswaName;
    public LocalDate siswaBirthDate;
    public String siswaAddress;
    public int guruId;
    public int waliKelasId;
    public int kelasId;
    public int ekskurId;

    public Murid(int siswaId, String siswaName, LocalDate siswaBirthDate,
                 String siswaAddress, int guruId, int waliKelasId, int kelasId, int ekskurId) {
        this.siswaId = siswaId;
        this.siswaName = siswaName;
        this.siswaBirthDate = siswaBirthDate;
        this.siswaAddress = siswaAddress;
        this.guruId = guruId;
        this.waliKelasId = waliKelasId;
        this.kelasId = kelasId;
        this.ekskurId = ekskurId;
    }

    public Murid(ResultSet rs) throws SQLException {
        this.siswaId = rs.getInt("siswa_id");
        this.siswaName = rs.getString("siswa_name");
        this.siswaBirthDate = rs.getDate("siswa_birth_date").toLocalDate();
        this.siswaAddress = rs.getString("siswa_address");
        this.guruId = rs.getInt("guru_id");
        this.waliKelasId = rs.getInt("wali_kelas_id");
        this.kelasId = rs.getInt("kelas_id");
        this.ekskurId = rs.getInt("ekskur_id");

    }

}
