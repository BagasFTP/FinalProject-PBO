package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        setSize(700, 400);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel panelKiri = new JPanel();
        panelKiri.setLayout(new GridLayout(9, 1, 10, 10));
        panelKiri.setBackground(new Color(70, 130, 180));

        String[] fitur = {
                "Registrasi Pasien", "Sistem Antrian", "Buat Janji",
                "Edit/Hapus Janji", "Reminder Temu", "Statistik Kunjungan",
                "Export Laporan", "Cek Berdasarkan Tanggal", "Rekam Medis"
        };

        Font font = new Font("Arial", Font.PLAIN, 14);

        for (int i = 0; i < fitur.length; i++) {
            JButton btn = new JButton(fitur[i]);
            btn.setFont(font);
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            btn.setFocusPainted(false);
            final int index = i;

            btn.addActionListener(e -> bukaForm(index));
            tombolFitur[i] = btn;

            if (i != 0) btn.setEnabled(false);

            panelKiri.add(btn);
        }

        add(panelKiri, BorderLayout.WEST);

        JPanel panelTengah = new JPanel();
        panelTengah.setBackground(new Color(245, 245, 245));
        add(panelTengah, BorderLayout.CENTER);

        ImageIcon imageIcon = new ImageIcon("img/background.png");
        JLabel labelImage = new JLabel(imageIcon);
        panelTengah.add(labelImage);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void bukaForm(int index) {
        switch (index) {
            case 0 -> {
                new FormRegistrasi(() -> {
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
