package model;

/**
 *
 * @author ASUS
 */
public class RekamMedis {
    private String idPasien;
    private String tanggal;
    private String keluhan;
    private String tindakan;

    public RekamMedis(String idPasien, String tanggal, String keluhan, String tindakan) {
        this.idPasien = idPasien;
        this.tanggal = tanggal;
        this.keluhan = keluhan;
        this.tindakan = tindakan;
    }

    public String toCSV() {
        return idPasien + "," + tanggal + "," + keluhan + "," + tindakan;
    }
}
