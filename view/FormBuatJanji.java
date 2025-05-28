package view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import controller.JanjiController; // <--- Tambahkan import ini

public class FormBuatJanji extends JFrame {
    private JTextField tfIdPasien;
    private JSpinner dateSpinner;
    private JButton btnSimpan;

    public FormBuatJanji() {
        setTitle("Buat Janji Temu");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lId = new JLabel("ID Pasien");
        tfIdPasien = new JTextField();

        JLabel lTanggal = new JLabel("Tanggal Janji");
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        btnSimpan = new JButton("Simpan");

        lId.setBounds(30, 30, 100, 25);
        tfIdPasien.setBounds(150, 30, 200, 25);
        lTanggal.setBounds(30, 70, 100, 25);
        dateSpinner.setBounds(150, 70, 200, 25);
        btnSimpan.setBounds(150, 120, 100, 30);

        add(lId);
        add(tfIdPasien);
        add(lTanggal);
        add(dateSpinner);
        add(btnSimpan);

        btnSimpan.addActionListener(e -> simpanJanji());
        setVisible(true);
    }

    private void simpanJanji() {
        String idPasien = tfIdPasien.getText().trim();
        Date selectedDate = (Date) dateSpinner.getValue();

        if (idPasien.isEmpty() || selectedDate == null) {
            JOptionPane.showMessageDialog(this,
                    "Harap isi ID pasien dan tanggal janji.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tanggalJanji = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        try {
            JanjiController.simpanJanji(idPasien, tanggalJanji); // <-- Simpan ke database
            JOptionPane.showMessageDialog(this,
                    "Janji berhasil disimpan untuk " + tanggalJanji,
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            tfIdPasien.setText("");
            dateSpinner.setValue(new Date());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan janji: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
