package view;

import javax.swing.*;
import java.sql.*;
import config.koneksi; 

public class FormReminder extends JFrame {
    public FormReminder() {
        setTitle("Reminder Jadwal Temu");
        setSize(400, 200);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lId = new JLabel("ID Pasien");
        JTextField tfId = new JTextField();
        JButton btnCek = new JButton("Cek Jadwal");

        lId.setBounds(30, 30, 100, 25);
        tfId.setBounds(150, 30, 200, 25);
        btnCek.setBounds(150, 70, 120, 30);

        add(lId);
        add(tfId);
        add(btnCek);

        btnCek.addActionListener(e -> {
            String idPasien = tfId.getText().trim();
            if (idPasien.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Pasien tidak boleh kosong!");
                return;
            }

            try (Connection conn = koneksi.getKoneksi()) {
                String query = "SELECT tanggal_janji FROM janji_temu WHERE id_pasien = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, idPasien);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String tanggalJanji = rs.getString("tanggal");
                    JOptionPane.showMessageDialog(this, "Jadwal temu pasien ID " + idPasien + ": " + tanggalJanji);
                } else {
                    JOptionPane.showMessageDialog(this, "Tidak ada jadwal ditemukan untuk ID Pasien " + idPasien);
                }

                rs.close();
                ps.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + ex.getMessage());
            }
        });

        setVisible(true);
    }
}