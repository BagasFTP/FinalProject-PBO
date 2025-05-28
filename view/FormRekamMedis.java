package view;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import config.koneksi;

public class FormRekamMedis extends JFrame {
    private JTextField tfId, tfKeluhan, tfDiagnosa;
    private JTextArea taRiwayat;
    private JButton btnSimpan, btnLihat, btnExport;

    public FormRekamMedis() {
        setTitle("Rekam Medis Pasien");
        setSize(500, 500);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblId = new JLabel("ID Pasien:");
        tfId = new JTextField();

        JLabel lblKeluhan = new JLabel("Keluhan:");
        tfKeluhan = new JTextField();

        JLabel lblDiagnosa = new JLabel("Diagnosa:");
        tfDiagnosa = new JTextField();

        btnSimpan = new JButton("Simpan Rekam");
        btnLihat = new JButton("Lihat Rekam");
        btnExport = new JButton("Download PDF");

        taRiwayat = new JTextArea();
        JScrollPane scroll = new JScrollPane(taRiwayat);
        taRiwayat.setEditable(false);

        // Layout
        lblId.setBounds(30, 20, 100, 25);
        tfId.setBounds(130, 20, 200, 25);
        lblKeluhan.setBounds(30, 60, 100, 25);
        tfKeluhan.setBounds(130, 60, 300, 25);
        lblDiagnosa.setBounds(30, 100, 100, 25);
        tfDiagnosa.setBounds(130, 100, 300, 25);
        btnSimpan.setBounds(30, 140, 150, 30);
        btnLihat.setBounds(190, 140, 130, 30);
        btnExport.setBounds(330, 140, 150, 30);
        scroll.setBounds(30, 190, 420, 240);

        add(lblId);
        add(tfId);
        add(lblKeluhan);
        add(tfKeluhan);
        add(lblDiagnosa);
        add(tfDiagnosa);
        add(btnSimpan);
        add(btnLihat);
        add(btnExport);
        add(scroll);

        btnSimpan.addActionListener(e -> simpanRekam());
        btnLihat.addActionListener(e -> tampilkanRekam());
        btnExport.addActionListener(e -> exportPDF());

        setVisible(true);
    }

    private void simpanRekam() {
        String id = tfId.getText().trim();
        String keluhan = tfKeluhan.getText().trim();
        String diagnosa = tfDiagnosa.getText().trim();

        if (id.isEmpty() || keluhan.isEmpty() || diagnosa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        try (Connection conn = koneksi.getKoneksi()) {
            String sql = "INSERT INTO rekam_medis (id_pasien, tanggal, keluhan, diagnosa) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setString(3, keluhan);
            ps.setString(4, diagnosa);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Rekam medis berhasil disimpan.");
            tfKeluhan.setText("");
            tfDiagnosa.setText("");
            tampilkanRekam();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan rekam medis.");
        }
    }

    private void tampilkanRekam() {
        String id = tfId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan ID Pasien terlebih dahulu.");
            return;
        }

        StringBuilder hasil = new StringBuilder();
        try (Connection conn = koneksi.getKoneksi()) {
            String sql = "SELECT tanggal, keluhan, diagnosa FROM rekam_medis WHERE id_pasien = ? ORDER BY tanggal DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                hasil.append("Tanggal: ").append(rs.getDate("tanggal")).append("\n")
                        .append("Keluhan: ").append(rs.getString("keluhan")).append("\n")
                        .append("Diagnosa: ").append(rs.getString("diagnosa")).append("\n")
                        .append("------------------------------------------------\n");
            }

            if (hasil.length() == 0) {
                hasil.append("Tidak ada data rekam medis untuk ID ini.");
            }

            taRiwayat.setText(hasil.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            taRiwayat.setText("Gagal mengambil data rekam medis.");
        }
    }

    private void exportPDF() {
        String id = tfId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan ID Pasien terlebih dahulu.");
            return;
        }

        List<String> data = new ArrayList<>();
        try (Connection conn = koneksi.getKoneksi()) {
            String sql = "SELECT tanggal, keluhan, diagnosa FROM rekam_medis WHERE id_pasien = ? ORDER BY tanggal DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.add("Tanggal: " + rs.getDate("tanggal") + "\n"
                        + "Keluhan: " + rs.getString("keluhan") + "\n"
                        + "Diagnosa: " + rs.getString("diagnosa") + "\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada rekam medis untuk diekspor.");
            return;
        }

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("RekamMedis_" + id + ".pdf"));
            doc.open();

            doc.add(new Paragraph("Rekam Medis Pasien - ID: " + id));
            doc.add(new Paragraph("------------------------------------------------------"));

            for (String entry : data) {
                doc.add(new Paragraph(entry));
            }

            doc.close();
            JOptionPane.showMessageDialog(this, "Rekam medis berhasil disimpan sebagai PDF!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat file PDF.");
        }
    }
}
