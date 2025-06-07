package view;

import config.koneksi;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FormExportLaporan extends JPanel {

    private JComboBox<String> cbxDataType;
    private JComboBox<String> cbxExportFormat;
    private JButton btnExport;

    public FormExportLaporan() {
        setLayout(new BorderLayout(10, 10)); 

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Export Laporan");
        lblJudul.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label dan ComboBox untuk pilih data
        JLabel lblPilihData = new JLabel("Pilih Data untuk Export:");
        lblPilihData.setFont(new FontUIResource("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(lblPilihData, gbc);

        cbxDataType = new JComboBox<>(new String[]{"Data Pasien", "Data Janji Temu", "Data Antrian", "Data Rekam Medis"});
        cbxDataType.setFont(new FontUIResource("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        contentPanel.add(cbxDataType, gbc);

        // Label dan ComboBox untuk pilih format export
        JLabel lblPilihFormat = new JLabel("Pilih Format Export:");
        lblPilihFormat.setFont(new FontUIResource("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        contentPanel.add(lblPilihFormat, gbc);

        cbxExportFormat = new JComboBox<>(new String[]{"PDF", "CSV"});
        cbxExportFormat.setFont(new FontUIResource("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        contentPanel.add(cbxExportFormat, gbc);

        // Button Export
        btnExport = new JButton("Export Laporan");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new FontUIResource("Segoe UI", Font.BOLD, 14));
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> exportLaporan());
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(btnExport, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void exportLaporan() {
        String selectedDataType = (String) cbxDataType.getSelectedItem();
        String selectedFormat = (String) cbxExportFormat.getSelectedItem();
        String query = "";
        String title = "";
        String fileName = "";

        switch (selectedDataType) {
            case "Data Pasien":
                query = "SELECT id_pasien, nama_pasien, tanggal_lahir, alamat, telepon, jenis_kelamin FROM pasien";
                title = "Laporan Data Pasien";
                fileName = "Laporan_Data_Pasien";
                break;
            case "Data Janji Temu":
                query = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien";
                title = "Laporan Data Janji Temu";
                fileName = "Laporan_Data_Janji_Temu";
                break;
            case "Data Antrian":
                query = "SELECT a.id_antrian, a.nomor_antrian, a.id_pasien, p.nama_pasien, a.tanggal_antrian, a.status FROM antrian a JOIN pasien p ON a.id_pasien = p.id_pasien";
                title = "Laporan Data Antrian";
                fileName = "Laporan_Data_Antrian";
                break;
            case "Data Rekam Medis":
                query = "SELECT rm.id_rekam, rm.id_pasien, p.nama_pasien, rm.tanggal, rm.diagnosis, rm.tindakan, rm.obat FROM rekam_medis rm JOIN pasien p ON rm.id_pasien = p.id_pasien";
                title = "Laporan Data Rekam Medis";
                fileName = "Laporan_Data_Rekam_Medis";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Pilihan data tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        // Tambahkan ekstensi file sesuai format
        if ("PDF".equals(selectedFormat)) {
            fileName += ".pdf";
        } else if ("CSV".equals(selectedFormat)) {
            fileName += ".csv";
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan " + selectedFormat);
        fileChooser.setSelectedFile(new java.io.File(fileName));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            if ("PDF".equals(selectedFormat)) {
                generatePdfReport(query, title, fileToSave);
            } else if ("CSV".equals(selectedFormat)) {
                generateCsvReport(query, title, fileToSave);
            }
        }
    }

    private void generatePdfReport(String query, String title, java.io.File fileToSave) {
        Document document = new Document(PageSize.A4.rotate()); // Landscape mode for wider tables
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
            document.open();

            // Add title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(41, 128, 185));
            Paragraph documentTitle = new Paragraph(title, titleFont);
            documentTitle.setAlignment(Element.ALIGN_CENTER);
            documentTitle.setSpacingAfter(15);
            document.add(documentTitle);

            // Add report generation date/time
            Font dateFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.DARK_GRAY);
            Paragraph reportDate = new Paragraph("Tanggal Laporan: " +
                    new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), dateFont);
            reportDate.setAlignment(Element.ALIGN_RIGHT);
            reportDate.setSpacingAfter(10);
            document.add(reportDate);

            // Add table
            Connection conn = koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            PdfPTable table = new PdfPTable(columnCount);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            
            // Set table widths dynamically
            float[] columnWidths = new float[columnCount];
            for (int i = 0; i < columnCount; i++) {
                // Adjust width based on column name or type if necessary
                columnWidths[i] = 1f; // Default width
            }
            table.setWidths(columnWidths);

            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);

            // Add table headers
            for (int i = 1; i <= columnCount; i++) {
                PdfPCell headerCell = new PdfPCell(new Phrase(rsmd.getColumnName(i).replace("_", " ").toUpperCase(), headerFont));
                headerCell.setBackgroundColor(new Color(52, 152, 219));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }

            // Add table data
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    PdfPCell dataCell = new PdfPCell(new Phrase(value != null ? value : "", cellFont));
                    dataCell.setPadding(4);
                    dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(dataCell);
                }
            }

            document.add(table);

            Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph("Generated by Aplikasi Klinik - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            rs.close();
            ps.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Laporan PDF berhasil disimpan:\n" + fileToSave.getAbsolutePath(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menulis file PDF:\n" + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void generateCsvReport(String query, String title, java.io.File fileToSave) {
        try (FileWriter writer = new FileWriter(fileToSave)) {
            Connection conn = koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Write CSV header (header laporan)
            writer.append("# ").append(title).append("\n");
            writer.append("# Tanggal Laporan: ").append(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date())).append("\n");
            writer.append("# Generated by Aplikasi Klinik\n\n");

            // Write column headers
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    writer.append(",");
                }
                writer.append("\"").append(rsmd.getColumnName(i).replace("_", " ").toUpperCase()).append("\"");
            }
            writer.append("\n");

            // Write data rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        writer.append(",");
                    }
                    String value = rs.getString(i);
                    if (value != null) {
                        // Escape quotes and wrap in quotes if contains comma or quote
                        value = value.replace("\"", "\"\"");
                        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                            writer.append("\"").append(value).append("\"");
                        } else {
                            writer.append(value);
                        }
                    } else {
                        writer.append("");
                    }
                }
                writer.append("\n");
            }

            writer.flush();
            rs.close();
            ps.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Laporan CSV berhasil disimpan:\n" + fileToSave.getAbsolutePath(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menulis file CSV:\n" + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}