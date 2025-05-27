package view;

import javax.swing.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.SpinnerDateModel;

public class FormEditJanji extends JFrame {
    private JSpinner spinnerTanggal;

    public FormEditJanji() {
        setTitle("Edit / Hapus Janji");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lId = new JLabel("ID Pasien");
        JTextField tfId = new JTextField();
        JLabel lTanggal = new JLabel("Tanggal Baru");

        // Spinner untuk tanggal
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd");
        spinnerTanggal.setEditor(editor);

        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");

        lId.setBounds(30, 30, 100, 25);
        tfId.setBounds(150, 30, 200, 25);
        lTanggal.setBounds(30, 70, 100, 25);
        spinnerTanggal.setBounds(150, 70, 200, 25);
        btnEdit.setBounds(80, 120, 100, 30);
        btnHapus.setBounds(200, 120, 100, 30);

        add(lId);
        add(tfId);
        add(lTanggal);
        add(spinnerTanggal);
        add(btnEdit);
        add(btnHapus);

        btnEdit.addActionListener(e -> {
            Date tanggalBaru = (Date) spinnerTanggal.getValue();
            String tglFormatted = new SimpleDateFormat("yyyy-MM-dd").format(tanggalBaru);
            JOptionPane.showMessageDialog(this, "Janji berhasil diedit ke tanggal: " + tglFormatted);
        });

        btnHapus.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Janji berhasil dihapus.");
        });

        setVisible(true);
    }
}