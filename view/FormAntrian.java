package view;

import controller.AntrianController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FormAntrian extends JFrame {
    private JTextArea textArea;
    private JButton btnTambah, btnRefresh, btnSelesai;

    public FormAntrian() {
        setTitle("Form Antrian Pasien");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TextArea untuk menampilkan antrian
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Panel tombol
        JPanel panelButton = new JPanel();
        btnTambah = new JButton("Tambah Antrian");
        btnRefresh = new JButton("Refresh");
        btnSelesai = new JButton("Selesaikan Antrian");

        panelButton.add(btnTambah);
        panelButton.add(btnRefresh);
        panelButton.add(btnSelesai);

        add(panelButton, BorderLayout.SOUTH);

        // Action Button Tambah
        btnTambah.addActionListener(e -> {
            String idPasien = JOptionPane.showInputDialog(this, "Masukkan ID Pasien:");
            if (idPasien != null && !idPasien.trim().isEmpty()) {
                try {
                    AntrianController.tambahAntrian(idPasien.trim());
                    JOptionPane.showMessageDialog(this, "Antrian ditambahkan.");
                    refreshAntrian();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan antrian: " + ex.getMessage());
                }
            }
        });

        // Action Button Refresh
        btnRefresh.addActionListener(e -> refreshAntrian());

        // Action Button Selesaikan Antrian
        btnSelesai.addActionListener(e -> selesaikanAntrian());

        refreshAntrian();
        setVisible(true);
    }

    private void refreshAntrian() {
        try {
            List<String[]> daftar = AntrianController.getDaftarAntrian();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5s %-10s %-15s\n", "No.", "ID Pasien", "Tanggal Antrian"));
            sb.append("=====================================\n");
            for (int i = 0; i < daftar.size(); i++) {
                String[] data = daftar.get(i); // [id_janji, id_pasien, tanggal_janji]
                sb.append(String.format("%-5d %-10s %-15s\n", i + 1, data[1], data[2]));
            }
            textArea.setText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            textArea.setText("Gagal memuat data antrian.");
        }
    }

    private void selesaikanAntrian() {
        String idJanji = JOptionPane.showInputDialog(this, "Masukkan ID JANJI yang akan diselesaikan:");
        if (idJanji != null && !idJanji.trim().isEmpty()) {
            try {
                List<String[]> daftar = AntrianController.getDaftarAntrian();
                String idPasien = null;
                for (String[] row : daftar) {
                    if (row[0].equals(idJanji.trim())) {
                        idPasien = row[1];
                        break;
                    }
                }

                if (idPasien != null) {
                    AntrianController.hapusAntrianDanCatat(idJanji.trim(), idPasien);
                    JOptionPane.showMessageDialog(this, "Antrian telah diselesaikan dan dicatat.");
                    refreshAntrian();
                } else {
                    JOptionPane.showMessageDialog(this, "ID janji tidak ditemukan!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menyelesaikan antrian: " + ex.getMessage());
            }
        }
    }
}
