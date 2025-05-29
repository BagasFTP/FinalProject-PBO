package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormCekTanggal extends JFrame {
    private JSpinner spinnerTanggal;

    public FormCekTanggal() {
        setTitle("Cek Pasien & Janji Berdasarkan Tanggal");
        setSize(450, 250);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lTgl = new JLabel("Tanggal");
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd"));

        JButton btnCari = new JButton("Cari");

        lTgl.setBounds(30, 30, 100, 25);
        spinnerTanggal.setBounds(150, 30, 200, 25);
        btnCari.setBounds(150, 70, 100, 30);

        add(lTgl);
        add(spinnerTanggal);
        add(btnCari);

        btnCari.addActionListener(e -> cariDataTanggal());

        setVisible(true);
    }

    private void cariDataTanggal() {
        Date selectedDate = (Date) spinnerTanggal.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tanggalCari = sdf.format(selectedDate);

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/klinik", "root", "");
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.id_pasien, p.nama_pasien " +
                "FROM statistik_kunjungan s " +
                "JOIN pasien p ON s.id_pasien = p.id_pasien " +
                "WHERE s.tanggal_kunjungan = ?"
            );
            ps.setString(1, tanggalCari);

            ResultSet rs = ps.executeQuery();
            StringBuilder hasil = new StringBuilder();
            while (rs.next()) {
                String id = rs.getString("id_pasien");
                String nama = rs.getString("nama_pasien");
                hasil.append("Pasien: ").append(id).append(", Nama: ").append(nama).append("\n");
            }

            if (hasil.length() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada kunjungan pada tanggal tersebut.");
            } else {
                JOptionPane.showMessageDialog(this, hasil.toString());
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengakses database.");
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Connector 8.x
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan.");
            return;
        }

        SwingUtilities.invokeLater(FormCekTanggal::new);
    }
}
