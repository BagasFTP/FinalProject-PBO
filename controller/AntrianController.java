package controller;

import java.io.*;
import java.util.*;

public class AntrianController {
    private static final String FILE_ANTRIAN = "data/antrian.txt";

    public static void tambahAntrian(String idPasien) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_ANTRIAN, true));
        writer.write(idPasien);
        writer.newLine();
        writer.close();
    }

    public static List<String> getDaftarAntrian() throws IOException {
        List<String> antrian = new ArrayList<>();
        File file = new File(FILE_ANTRIAN);

        if (!file.exists()) {
            return antrian; // Kosong jika file belum dibuat
        }

        BufferedReader reader = new BufferedReader(new FileReader(FILE_ANTRIAN));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                antrian.add(line.trim());
            }
        }
        reader.close();
        return antrian;
    }
}
