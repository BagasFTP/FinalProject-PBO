package view;

import javax.swing.*;
import java.io.*;
import model.Pasien;

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
        // Validasi input
        if (tfId.getText().isEmpty() || tfNama.getText().isEmpty() || tfTgl.getText().isEmpty()
                || tfAlamat.getText().isEmpty() || tfHp.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Validasi Gagal",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pasien p = new Pasien(
                tfId.getText().trim(),
                tfNama.getText().trim(),
                tfTgl.getText().trim(),
                tfAlamat.getText().trim(),
                tfHp.getText().trim());

        try {
            File folder = new File("data");
            if (!folder.exists())
                folder.mkdirs();

            try (BufferedWriter w = new BufferedWriter(new FileWriter("data/pasien.csv", true))) {
                w.write(p.toCSV());
                w.newLine();
            }

            JOptionPane.showMessageDialog(this, "Data pasien berhasil disimpan.");
            dispose();

            if (onSuksesRegistrasi != null) {
                onSuksesRegistrasi.run(); // Aktifkan tombol lain di MainApp
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Debug di konsol
        }
    }
}
