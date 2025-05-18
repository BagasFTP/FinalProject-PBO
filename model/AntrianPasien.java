
package model;

/**
 *
 * @author ASUS
 */
public class AntrianPasien {
    private String idPasien;
    private String waktuDatang; // atau tanggal

    public AntrianPasien(String idPasien, String waktuDatang) {
        this.idPasien = idPasien;
        this.waktuDatang = waktuDatang;
    }

    public String toCSV() {
        return idPasien + "," + waktuDatang;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public String getWaktuDatang() {
        return waktuDatang;
    }
}
