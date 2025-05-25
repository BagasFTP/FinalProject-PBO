package view;

import model.AntrianPasien;
import model.Pasien;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author ASUS
 */
public class FormAntrian extends JFrame {
    JTextField tfIdPasien, tfWaktu;
    JButton btnTambah;
    JTextArea taAntrian;

    public FormAntrian() {
        setTitle("Sistem Antrian Pasien");
        setLayout(null);
        setSize(450, 400);
        setLocationRelativeTo(null);

        JLabel l1 = new JLabel("ID Pasien:");
        JLabel l2 = new JLabel("Waktu Datang:");

        tfIdPasien = new JTextField();
        tfWaktu = new JTextField(); // bisa diisi manual atau pakai timestamp otomatis
        btnTambah = new JButton("Tambah ke Antrian");
        taAntrian = new JTextArea();
        taAntrian.setEditable(false);

        JScrollPane scroll = new JScrollPane(taAntrian);

        l1.setBounds(30, 30, 100, 25);
        tfIdPasien.setBounds(150, 30, 200, 25);
        l2.setBounds(30, 70, 100, 25);
        tfWaktu.setBounds(150, 70, 200, 25);
        btnTambah.setBounds(150, 110, 160, 30);
        scroll.setBounds(30, 160, 360, 180);

        add(l1);
        add(tfIdPasien);
        add(l2);
        add(tfWaktu);
        add(btnTambah);
        add(scroll);

        btnTambah.addActionListener(e -> tambahAntrian());

        loadAntrian();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void tambahAntrian() {
        String id = tfIdPasien.getText().trim();
        String waktu = tfWaktu.getText().trim();
        AntrianPasien ap = new AntrianPasien(id, waktu);

        File file = new File("data/antrian.csv");
        file.getParentFile().mkdirs();

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, true))) {
            w.write(ap.toCSV());
            w.newLine();
            JOptionPane.showMessageDialog(this, "Ditambahkan ke antrian.");
            loadAntrian();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan.");
        }
    }

    private void loadAntrian() {
        taAntrian.setText("");
        File file = new File("data/antrian.csv");
        if (!file.exists())
            return;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] data = line.split(",");
                String id = data[0];
                String waktu = data[1];
                String nama = getNamaPasienById(id);
                taAntrian.append("[" + waktu + "] " + id + " - " + nama + "\n");
            }
        } catch (IOException e) {
            taAntrian.setText("Gagal load antrian.");
        }
    }

    private String getNamaPasienById(String id) {
        try (BufferedReader r = new BufferedReader(new FileReader("data/pasien.csv"))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(id))
                    return data[1];
            }
        } catch (IOException e) {
        }
        return "(Nama tidak ditemukan)";
    }
}
