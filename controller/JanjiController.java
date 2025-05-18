package controller;
import model.Janji;
import java.io.*;
import java.util.*;
/**
 *
 * @author ASUS
 */
public class JanjiController {
    private static final String FILE_PATH = "data/janji.csv";

    public static void simpanJanji(Janji j) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(j.toCSV());
            bw.newLine();
        }
    }

    public static List<Janji> getDaftarJanji() throws IOException {
        List<Janji> daftar = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                daftar.add(Janji.fromCSV(line));
            }
        }
        return daftar;
    }
}
