package view;

import javax.swing.*;
import java.io.*;
import model.Pasien;

public class FormRegistrasi extends JFrame {
    JTextField tfId, tfNama, tfTgl, tfAlamat, tfHp;
    JButton btnSimpan;
    Runnable onSuksesRegistrasi;

    public FormRegistrasi(Runnable onSuksesRegistrasi) {
        this.onSuksesRegistrasi = onSuksesRegistrasi;

        setTitle("Registrasi Pasien");
        setLayout(null);
        setSize(400, 300);

        tfId = new JTextField(); tfNama = new JTextField(); tfTgl = new JTextField();
        tfAlamat = new JTextField(); tfHp = new JTextField(); btnSimpan = new JButton("Simpan");

        JLabel[] labels = {
            new JLabel("ID Pasien"), new JLabel("Nama"), new JLabel("Tanggal Lahir"),
            new JLabel("Alamat"), new JLabel("No. HP")
        };
        JTextField[] fields = {tfId, tfNama, tfTgl, tfAlamat, tfHp};

        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(30, 30 + i * 30, 100, 25);
            fields[i].setBounds(150, 30 + i * 30, 200, 25);
            add(labels[i]); add(fields[i]);
        }

        btnSimpan.setBounds(150, 200, 100, 30);
        add(btnSimpan);

        btnSimpan.addActionListener(e -> simpanPasien());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void simpanPasien() {
        Pasien p = new Pasien(
            tfId.getText(), tfNama.getText(), tfTgl.getText(),
            tfAlamat.getText(), tfHp.getText()
        );
        try (BufferedWriter w = new BufferedWriter(new FileWriter("data/pasien.csv", true))) {
            w.write(p.toCSV());
            w.newLine();
            JOptionPane.showMessageDialog(this, "Pasien disimpan.");
            dispose(); // Tutup form
            if (onSuksesRegistrasi != null) onSuksesRegistrasi.run(); // Aktifkan tombol di MainApp
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan!");
        }
    }
}
