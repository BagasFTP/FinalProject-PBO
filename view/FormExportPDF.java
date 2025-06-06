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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FormExportPDF extends JPanel {

    private JComboBox<String> cbxDataType;
    private JButton btnExport;

    public FormExportPDF() {
        setLayout(new BorderLayout(10, 10)); 

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Export Laporan ke PDF");
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

        btnExport = new JButton("Export ke PDF");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new FontUIResource("Segoe UI", Font.BOLD, 14));
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> exportToPdf());
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(btnExport, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void exportToPdf() {
        String selectedDataType = (String) cbxDataType.getSelectedItem();
        String query = "";
        String title = "";
        String fileName = "";

        switch (selectedDataType) {
            case "Data Pasien":
                query = "SELECT id_pasien, nama_pasien, tanggal_lahir, alamat, telepon, jenis_kelamin FROM pasien";
                title = "Laporan Data Pasien";
                fileName = "Laporan_Data_Pasien.pdf";
                break;
            case "Data Janji Temu":
                query = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien";
                title = "Laporan Data Janji Temu";
                fileName = "Laporan_Data_Janji_Temu.pdf";
                break;
            case "Data Antrian":
                query = "SELECT a.id_antrian, a.nomor_antrian, a.id_pasien, p.nama_pasien, a.tanggal_antrian, a.status FROM antrian a JOIN pasien p ON a.id_pasien = p.id_pasien";
                title = "Laporan Data Antrian";
                fileName = "Laporan_Data_Antrian.pdf";
                break;
            case "Data Rekam Medis":
                // Changed 'rm.diagnosa' to 'rm.diagnosis'
                query = "SELECT rm.id_rekam, rm.id_pasien, p.nama_pasien, rm.tanggal, rm.diagnosis, rm.tindakan, rm.obat FROM rekam_medis rm JOIN pasien p ON rm.id_pasien = p.id_pasien";
                title = "Laporan Data Rekam Medis";
                fileName = "Laporan_Data_Rekam_Medis.pdf";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Pilihan data tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        fileChooser.setSelectedFile(new java.io.File(fileName));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            generatePdfReport(query, title, fileToSave);
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

            JOptionPane.showMessageDialog(this, "Laporan PDF berhasil disimpan:\\n" + fileToSave.getAbsolutePath(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menulis file PDF:\\n" + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}