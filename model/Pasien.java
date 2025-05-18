package model;

/**
 *
 * @author ASUS
 */
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
        return id + "," + nama + "," + tglLahir + "," + alamat + "," + noHp;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }
}

