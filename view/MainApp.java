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

            // Semua tombol aktif dari awal

            panelKiri.add(btn);
        }

        add(panelKiri, BorderLayout.WEST);

        JPanel panelTengah = new JPanel();
        panelTengah.setBackground(new Color(245, 245, 245));
        add(panelTengah, BorderLayout.CENTER);

        // Handle image loading dengan error handling
        try {
            ImageIcon imageIcon = new ImageIcon("img/background.png");
            if (imageIcon.getIconWidth() > 0) {
                JLabel labelImage = new JLabel(imageIcon);
                panelTengah.add(labelImage);
            } else {
                // Jika gambar tidak ditemukan, tampilkan text
                JLabel labelText = new JLabel("SISTEM INFORMASI KLINIK", JLabel.CENTER);
                labelText.setFont(new Font("Arial", Font.BOLD, 24));
                labelText.setForeground(new Color(70, 130, 180));
                panelTengah.add(labelText);
            }
        } catch (Exception e) {
            // Jika error loading gambar, tampilkan text
            JLabel labelText = new JLabel("SISTEM INFORMASI KLINIK", JLabel.CENTER);
            labelText.setFont(new Font("Arial", Font.BOLD, 24));
            labelText.setForeground(new Color(70, 130, 180));
            panelTengah.add(labelText);
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void bukaForm(int index) {
        try {
            switch (index) {
                case 0 -> {
                    // FormRegistrasi memerlukan parameter Runnable
                    new FormRegistrasi(() -> {
                        // Callback setelah registrasi berhasil
                        // Bisa ditambahkan logic tambahan jika diperlukan
                        System.out.println("Registrasi pasien berhasil!");
                    });
                }
                case 1 -> {
                    try {
                        new FormAntrian();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Antrian belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 2 -> {
                    try {
                        new FormBuatJanji();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Buat Janji belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 3 -> {
                    try {
                        new FormEditJanji();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Edit Janji belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 4 -> {
                    try {
                        new FormReminder();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Reminder belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 5 -> {
                    try {
                        new FormStatistik();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Statistik belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 6 -> {
                    try {
                        new FormExportCSV();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Export CSV belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 7 -> {
                    try {
                        new FormCekTanggal();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Cek Tanggal belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 8 -> {
                    try {
                        new FormRekamMedis();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Rekam Medis belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                default -> JOptionPane.showMessageDialog(this, "Fitur belum tersedia!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error membuka form: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}