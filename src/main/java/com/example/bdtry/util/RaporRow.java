package com.example.bdtry.util;

import javafx.beans.property.*;

public class RaporRow {
    private final StringProperty mataPelajaran;
    private final IntegerProperty uts;
    private final IntegerProperty uas;

    public RaporRow(String mataPelajaran, int uts, int uas) {
        this.mataPelajaran = new SimpleStringProperty(mataPelajaran);
        this.uts = new SimpleIntegerProperty(uts);
        this.uas = new SimpleIntegerProperty(uas);
    }

    public StringProperty mataPelajaranProperty() {
        return mataPelajaran;
    }

    public IntegerProperty utsProperty() {
        return uts;
    }

    public IntegerProperty uasProperty() {
        return uas;
    }
}

