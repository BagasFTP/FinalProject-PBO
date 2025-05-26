package model;

import java.util.Date;

public class JanjiModel {
    private int id;
    private String idPasien;
    private Date tanggalJanji;

    public JanjiModel(String idPasien, Date tanggalJanji) {
        this.idPasien = idPasien;
        this.tanggalJanji = tanggalJanji;
    }

    public int getId() { return id; }
    public String getIdPasien() { return idPasien; }
    public Date getTanggalJanji() { return tanggalJanji; }

    public void setId(int id) { this.id = id; }
    public void setIdPasien(String idPasien) { this.idPasien = idPasien; }
    public void setTanggalJanji(Date tanggalJanji) { this.tanggalJanji = tanggalJanji; }
}
