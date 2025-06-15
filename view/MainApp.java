package view;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private JButton[] tombolFitur = new JButton[9];
    private JPanel panelTengah;

    public MainApp() {
        setTitle("Sistem Informasi Klinik");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tambahNavbar();
        tambahSidebar();
        tambahPanelTengah();

        setVisible(true);
    }

    private void tambahNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(45, 85, 125));
        navbar.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel title = new JLabel("  Sistem Informasi Klinik", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        navbar.add(title, BorderLayout.WEST);
        add(navbar, BorderLayout.NORTH);
    }

    private void tambahSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 30, 47));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        String[] fitur = {
                "Registrasi Pasien", "Sistem Antrian", "Buat Janji",
                "Edit/Hapus Janji", "Reminder Temu", "Statistik Kunjungan",
                "Export Laporan", "Cek Tanggal", "Rekam Medis"
        };

        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        for (int i = 0; i < fitur.length; i++) {
            JButton btn = new JButton(fitur[i]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setFont(font);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(45, 45, 65));
            btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final int index = i;
            btn.addActionListener(e -> bukaForm(index));

            tombolFitur[i] = btn;
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btn);
        }

        add(sidebar, BorderLayout.WEST);
    }

    private void tambahPanelTengah() {
        panelTengah = new JPanel(new GridBagLayout());
        panelTengah.setBackground(new Color(245, 245, 245));

        JLabel welcome = new JLabel("SELAMAT DATANG DI SISTEM INFORMASI KLINIK");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcome.setForeground(new Color(45, 85, 125));

        panelTengah.add(welcome);
        add(panelTengah, BorderLayout.CENTER);
    }

    private void bukaForm(int index) {
        try {
            panelTengah.removeAll();
            panelTengah.setLayout(new BorderLayout());

            switch (index) {
                case 0 -> {
                    PanelPasien panelPasien = new PanelPasien();
                    panelTengah.add(panelPasien, BorderLayout.CENTER);
                }
                case 1 -> {
                    FormAntrian formAntrian = new FormAntrian();
                    panelTengah.add(formAntrian, BorderLayout.CENTER);
                }
                case 2 -> {
                    FormBuatJanji formBuatJanji = new FormBuatJanji();
                    panelTengah.add(formBuatJanji, BorderLayout.CENTER);
                }
                case 3 -> {
                    FormEditJanji formEditJanji = new FormEditJanji();
                    panelTengah.add(formEditJanji, BorderLayout.CENTER);
                }
                case 4 -> {
                    FormReminder formReminder = new FormReminder();
                    panelTengah.add(formReminder, BorderLayout.CENTER);
                }
                case 5 -> {
                    Statistik statistik = new Statistik();
                    panelTengah.add(statistik, BorderLayout.CENTER);
                }
                case 6 -> {
                    FormExportLaporan formExportLaporan = new FormExportLaporan();
                    panelTengah.add(formExportLaporan, BorderLayout.CENTER);
                }
                case 7 -> {
                    FormCekTanggal formCekTanggal = new FormCekTanggal();
                    panelTengah.add(formCekTanggal, BorderLayout.CENTER);
                }
                case 8 -> {
                    FormRekamMedis formRekamMedis = new FormRekamMedis();
                    panelTengah.add(formRekamMedis, BorderLayout.CENTER);
                }
                default -> showMessage("Fitur belum tersedia.");
            }

            // Perbarui tampilan UI
            panelTengah.revalidate();
            panelTengah.repaint();

        } catch (Exception e) {
            showMessage("Error membuka form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
