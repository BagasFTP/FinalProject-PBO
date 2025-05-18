package view;

import javax.swing.*;
import java.awt.event.*;

// Impor form
import view.FormRegistrasi;
import view.FormAntrian;
import view.FormBuatJanji;
import view.FormEditJanji;
import view.FormReminder;
import view.FormStatistik;
import view.FormExportCSV;
import view.FormCekTanggal;
import view.FormRekamMedis;

public class MainApp extends JFrame {
    private JButton[] tombolFitur = new JButton[9];

    public MainApp() {
        setTitle("Menu Utama Klinik");
        setSize(400, 450);
        setLayout(null);

        String[] fitur = {
            "Registrasi Pasien", "Sistem Antrian", "Buat Janji",
            "Edit/Hapus Janji", "Reminder Temu", "Statistik Kunjungan",
            "Export Laporan", "Cek Berdasarkan Tanggal", "Rekam Medis"
        };

        for (int i = 0; i < fitur.length; i++) {
            JButton btn = new JButton(fitur[i]);
            btn.setBounds(80, 30 + i * 40, 240, 30);
            final int index = i;

            btn.addActionListener(e -> bukaForm(index));
            tombolFitur[i] = btn;

            // Hanya tombol Registrasi (index 0) yang aktif awalnya
            if (i != 0) btn.setEnabled(false);

            add(btn);
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void bukaForm(int index) {
        switch (index) {
            case 0 -> {
                new FormRegistrasi(() -> {
                    // Setelah registrasi, aktifkan tombol lain
                    for (int i = 1; i < tombolFitur.length; i++) {
                        tombolFitur[i].setEnabled(true);
                    }
                });
            }
            case 1 -> new FormAntrian();
            case 2 -> new FormBuatJanji();
            case 3 -> new FormEditJanji();
            case 4 -> new FormReminder();
            case 5 -> new FormStatistik();
            case 6 -> new FormExportCSV();
            case 7 -> new FormCekTanggal();
            case 8 -> new FormRekamMedis();
        }
    }

    public static void main(String[] args) {
        new MainApp();
    }
}
