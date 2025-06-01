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

            // Hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(65, 65, 100));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(45, 45, 65));
                }
            });

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
        // Clear previous content in panelTengah
        panelTengah.removeAll();
        panelTengah.setLayout(new BorderLayout()); // Set layout to BorderLayout for PanelPasien

        switch (index) {
            case 0 -> {
                PanelPasien panelPasien = new PanelPasien();
                panelTengah.add(panelPasien, BorderLayout.CENTER);
                panelTengah.revalidate();
                panelTengah.repaint();
            }
            case 1 -> safeOpen(() -> new FormAntrian(), "Form Antrian");
            case 2 -> safeOpen(() -> new FormBuatJanji(), "Form Buat Janji");
            case 3 -> safeOpen(() -> new FormEditJanji(), "Form Edit Janji");
            case 4 -> safeOpen(() -> new FormReminder(), "Form Reminder");
            case 5 -> safeOpen(() -> new Statistik(), "Form Statistik");
            case 6 -> safeOpen(() -> new FormExportCSV(), "Form Export Laporan");
            case 7 -> safeOpen(() -> new FormCekTanggal(), "Form Cek Tanggal");
            case 8 -> safeOpen(() -> new FormRekamMedis(), "Form Rekam Medis");
            default -> showMessage("Fitur belum tersedia.");
        }
    } catch (Exception e) {
        showMessage("Error membuka form: " + e.getMessage());
        e.printStackTrace();
    }
}

// Remove the unimplemented method:
// private Object tampilkanDiPanelBaru(PanelPasien panelPasien) {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'tampilkanDiPanelBaru'");
// }

    private Object tampilkanDiPanelBaru(PanelPasien panelPasien) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tampilkanDiPanelBaru'");
    }

    private void safeOpen(Runnable action, String formName) {
        try {
            action.run();
        } catch (Exception ex) {
            showMessage(formName + " belum tersedia atau error: " + ex.getMessage());
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
