package model;

public class Pasien {
    private String id, nama, tglLahir, alamat, noHp;

    public Pasien(String id, String nama, String tglLahir, String alamat, String noHp) {
        this.id = id;
        this.nama = nama;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.noHp = noHp;
    }

    public String toCSV() {
        return String.join(",", id, nama, tglLahir, alamat, noHp);
    }
}
