package controller;
import model.Pasien;
import java.io.*;
/**
 *
 * @author ASUS
 */
public class PasienController {
     private static final String FILE_PATH = "data/pasien.csv";

    public static void simpanPasien(Pasien p) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(p.toCSV());
            bw.newLine();
        }
    }

    public static boolean cekIdPasienExist(String id) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equalsIgnoreCase(id)) return true;
            }
        }
        return false;
    }   
}
