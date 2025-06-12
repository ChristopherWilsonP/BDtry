package com.example.bdtry.util;

import javafx.beans.property.*;

public class RaporRow {
    private final String  namaSiswa;
    private final String mataPelajaran;
    private final Integer uts;
    private final Integer uas;

    public RaporRow(String namaSiswa, String mataPelajaran, int uts, int uas) {
        this.namaSiswa = namaSiswa;
        this.mataPelajaran = mataPelajaran;
        this.uts = uts;
        this.uas = uas;
    }

    public String mataPelajaranProperty() {
        return mataPelajaran;
    }

    public Integer utsProperty() {
        return uts;
    }

    public Integer uasProperty() {
        return uas;
    }
}

