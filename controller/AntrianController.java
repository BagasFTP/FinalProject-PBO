package controller;
import java.io.*;
import java.util.*;
/**
 *
 * @author ASUS
 */
public class AntrianController {
    private static final String FILE_PATH = "data/antrian.txt";

    public static void tambahAntrian(String idPasien) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(idPasien);
            bw.newLine();
        }
    }

    public static List<String> getDaftarAntrian() throws IOException {
        List<String> daftar = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                daftar.add(line);
            }
        }
        return daftar;
    }
}
