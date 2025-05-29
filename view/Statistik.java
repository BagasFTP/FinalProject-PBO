package view;

import javax.swing.*;
import java.awt.*;

public class Statistik extends JFrame {
    public Statistik() {
        setTitle("Statistik Kunjungan");
        setSize(400, 250);
        setLocationRelativeTo(null); // Tengah layar
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Gunakan BorderLayout
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(45, 85, 125));
        JLabel headerLabel = new JLabel("📊 Statistik Kunjungan");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(headerLabel);

        // Konten utama
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50));
        contentPanel.setBackground(Color.WHITE);

        JLabel lblStat = new JLabel("Total Kunjungan Bulan Ini: 45");
        lblStat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblStat.setForeground(new Color(60, 60, 60));

        // Tambahkan ke panel
        contentPanel.add(lblStat);

        // Tambahkan semua ke frame
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
