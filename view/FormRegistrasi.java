package view;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import config.koneksi;

public class FormRegistrasi extends JFrame {
    private JTextField tfId, tfNama, tfTgl, tfAlamat, tfHp;
    private JButton btnSimpan;
    private final Runnable onSuksesRegistrasi;

    public FormRegistrasi(Runnable onSuksesRegistrasi) {
        this.onSuksesRegistrasi = onSuksesRegistrasi;

        setTitle("Registrasi Pasien");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null); // Tengah layar
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inisialisasiForm();
        setVisible(true);
    }

    private void inisialisasiForm() {
        JLabel[] labels = {
                new JLabel("ID Pasien"), new JLabel("Nama"),
                new JLabel("Tanggal Lahir"), new JLabel("Alamat"), new JLabel("No. HP")
        };

        tfId = new JTextField();
        tfNama = new JTextField();
        tfTgl = new JTextField();
        tfAlamat = new JTextField();
        tfHp = new JTextField();
        btnSimpan = new JButton("Simpan");

        JTextField[] fields = { tfId, tfNama, tfTgl, tfAlamat, tfHp };

        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(30, 30 + i * 30, 100, 25);
            fields[i].setBounds(150, 30 + i * 30, 200, 25);
            add(labels[i]);
            add(fields[i]);
        }

        btnSimpan.setBounds(150, 200, 100, 30);
        btnSimpan.addActionListener(e -> simpanPasien());
        add(btnSimpan);
    }

    private void simpanPasien() {
        if (tfId.getText().isEmpty() || tfNama.getText().isEmpty() || tfTgl.getText().isEmpty()
                || tfAlamat.getText().isEmpty() || tfHp.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Validasi Gagal",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = koneksi.getKoneksi();
            String sql = "INSERT INTO pasien (id_pasien, nama_pasien, tgl_lahir, alamat, noHP) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tfId.getText().trim());
            pst.setString(2, tfNama.getText().trim());
            pst.setString(3, tfTgl.getText().trim());
            pst.setString(4, tfAlamat.getText().trim());
            pst.setString(5, tfHp.getText().trim());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data pasien berhasil disimpan ke database.");
            dispose();

            if (onSuksesRegistrasi != null) {
                onSuksesRegistrasi.run();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
