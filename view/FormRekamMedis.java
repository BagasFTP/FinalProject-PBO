package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import config.koneksi;

public class FormRekamMedis extends JFrame {
    private JTextField tfId, tfKeluhan, tfDiagnosa, tfTindakan, tfObat;
    private JTextArea taCatatan, taRiwayat;
    private JButton btnSimpan, btnLihat, btnExport;

    public FormRekamMedis() {
        setTitle("Rekam Medis Pasien");
        setSize(550, 700);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblId = new JLabel("ID Pasien:");
        tfId = new JTextField();

        JLabel lblKeluhan = new JLabel("Keluhan:");
        tfKeluhan = new JTextField();

        JLabel lblDiagnosa = new JLabel("Diagnosa:");
        tfDiagnosa = new JTextField();

        JLabel lblTindakan = new JLabel("Tindakan:");
        tfTindakan = new JTextField();

        JLabel lblObat = new JLabel("Obat:");
        tfObat = new JTextField();

        JLabel lblCatatan = new JLabel("Catatan Tambahan:");
        taCatatan = new JTextArea(3, 20);
        JScrollPane scrollCatatan = new JScrollPane(taCatatan);

        btnSimpan = new JButton("Simpan Rekam");
        btnLihat = new JButton("Lihat Rekam");
        btnExport = new JButton("Download PDF");

        taRiwayat = new JTextArea();
        JScrollPane scroll = new JScrollPane(taRiwayat);
        taRiwayat.setEditable(false);

        // Layout
        int labelW = 130, fieldW = 350, height = 25, gap = 10;
        int y = 20;

        lblId.setBounds(30, y, labelW, height);
        tfId.setBounds(170, y, fieldW, height);
        y += height + gap;

        lblKeluhan.setBounds(30, y, labelW, height);
        tfKeluhan.setBounds(170, y, fieldW, height);
        y += height + gap;

        lblDiagnosa.setBounds(30, y, labelW, height);
        tfDiagnosa.setBounds(170, y, fieldW, height);
        y += height + gap;

        lblTindakan.setBounds(30, y, labelW, height);
        tfTindakan.setBounds(170, y, fieldW, height);
        y += height + gap;

        lblObat.setBounds(30, y, labelW, height);
        tfObat.setBounds(170, y, fieldW, height);
        y += height + gap;

        lblCatatan.setBounds(30, y, labelW, height);
        scrollCatatan.setBounds(170, y, fieldW, 60);
        y += 60 + gap;

        btnSimpan.setBounds(30, y, 150, 30);
        btnLihat.setBounds(190, y, 130, 30);
        btnExport.setBounds(330, y, 170, 30);
        y += 40;

        scroll.setBounds(30, y, 470, 300);

        add(lblId);
        add(tfId);
        add(lblKeluhan);
        add(tfKeluhan);
        add(lblDiagnosa);
        add(tfDiagnosa);
        add(lblTindakan);
        add(tfTindakan);
        add(lblObat);
        add(tfObat);
        add(lblCatatan);
        add(scrollCatatan);
        add(btnSimpan);
        add(btnLihat);
        add(btnExport);
        add(scroll);

        btnSimpan.addActionListener(e -> simpanRekam());
        btnLihat.addActionListener(e -> tampilkanRekam());
        btnExport.addActionListener(e -> exportPDF(null));

        setVisible(true);
    }

    private void simpanRekam() {
        String id = tfId.getText().trim();
        String keluhan = tfKeluhan.getText().trim();
        String diagnosa = tfDiagnosa.getText().trim();
        String tindakan = tfTindakan.getText().trim();
        String obat = tfObat.getText().trim();
        String catatan = taCatatan.getText().trim();

        if (id.isEmpty() || keluhan.isEmpty() || diagnosa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Field ID, Keluhan, dan Diagnosa wajib diisi!");
            return;
        }

        try (Connection conn = koneksi.getKoneksi()) {
            String sql = "INSERT INTO rekam_medis (id_pasien, tanggal, keluhan, diagnosa, tindakan, obat_yang_diberikan, catatan_tambahan) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setString(3, keluhan);
            ps.setString(4, diagnosa);
            ps.setString(5, tindakan);
            ps.setString(6, obat);
            ps.setString(7, catatan);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Rekam medis berhasil disimpan.");
            tfKeluhan.setText("");
            tfDiagnosa.setText("");
            tfTindakan.setText("");
            tfObat.setText("");
            taCatatan.setText("");
            tampilkanRekam();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan rekam medis.\nError: " + e.getMessage());
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
            String sql = "SELECT tanggal, keluhan, diagnosa, tindakan, obat_yang_diberikan, catatan_tambahan FROM rekam_medis WHERE id_pasien = ? ORDER BY tanggal DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                hasil.append("Tanggal: ").append(rs.getDate("tanggal")).append("\n")
                        .append("Keluhan: ").append(rs.getString("keluhan")).append("\n")
                        .append("Diagnosa: ").append(rs.getString("diagnosa")).append("\n")
                        .append("Tindakan: ").append(rs.getString("tindakan")).append("\n")
                        .append("Obat: ").append(rs.getString("obat_yang_diberikan")).append("\n")
                        .append("Catatan: ").append(rs.getString("catatan_tambahan")).append("\n")
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

    /**
     * @param e
     */
    private void exportPDF(Action e) {
        String id = tfId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan ID Pasien terlebih dahulu.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        chooser.setSelectedFile(new File("RekamMedis_" + id + "_" + timestamp + ".pdf"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File file = chooser.getSelectedFile();

        try (Connection conn = koneksi.getKoneksi()) {
            String sql = "SELECT tanggal, keluhan, diagnosa, tindakan, obat_yang_diberikan, catatan_tambahan " +
                    "FROM rekam_medis WHERE id_pasien = ? ORDER BY tanggal DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Laporan Rekam Medis Pasien - ID: " + id, fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            doc.add(title);

            String[] headers = { "Tanggal", "Keluhan", "Diagnosa", "Tindakan", "Obat Diberikan", "Catatan Tambahan" };
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(new Color(211, 211, 211));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            while (rs.next()) {
                table.addCell(rs.getString("tanggal"));
                table.addCell(rs.getString("keluhan"));
                table.addCell(rs.getString("diagnosa"));
                table.addCell(rs.getString("tindakan"));
                table.addCell(rs.getString("obat_yang_diberikan"));
                table.addCell(rs.getString("catatan_tambahan"));
            }

            doc.add(table);
            doc.close();

            JOptionPane.showMessageDialog(this, "PDF berhasil disimpan:\n" + file.getAbsolutePath());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal ekspor PDF: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}