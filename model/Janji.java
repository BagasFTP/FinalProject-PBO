package model;

/**
 *
 * @author ASUS
 */
public class Janji {
    private String idPasien;
    private String tanggal;
    private String jam;
    private String keterangan;

    public Janji(String idPasien, String tanggal, String jam, String keterangan) {
        this.idPasien = idPasien;
        this.tanggal = tanggal;
        this.jam = jam;
        this.keterangan = keterangan;
    }

    public String toCSV() {
        return idPasien + "," + tanggal + "," + jam + "," + keterangan;
    }

    public static Janji fromCSV(String line) {
        String[] parts = line.split(",");
        return new Janji(parts[0], parts[1], parts[2], parts[3]);
    }
}
