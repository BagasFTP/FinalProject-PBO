package view;

import config.koneksi;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FormExportPDF extends JFrame {

    private JComboBox<String> cbxDataType;
    private JButton btnExport;

    public FormExportPDF() {
        setTitle("Export Laporan ke PDF");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblDataType = new JLabel("Pilih Data untuk Export:");
        lblDataType.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));

        cbxDataType = new JComboBox<>(new String[] {
                "Data Pasien", "Statistik Kunjungan", "Data Janji Temu", "Data Rekam Medis"
        });
        cbxDataType.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));

        btnExport = new JButton("Export ke PDF");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> exportDataToPDF());

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(lblDataType, gbc);

        gbc.gridx = 1;
        contentPanel.add(cbxDataType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnExport, gbc);

        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void exportDataToPDF() {
        String selectedDataType = (String) cbxDataType.getSelectedItem();
        String fileNamePrefix = "";
        String query = "";
        String[] columnHeaders = null;

        switch (selectedDataType) {
            case "Data Pasien":
                fileNamePrefix = "Laporan_Pasien";
                query = "SELECT id_pasien, nama_pasien, tanggal_lahir, alamat, no_telepon, jenis_kelamin FROM pasien";
                columnHeaders = new String[] { "ID Pasien", "Nama Pasien", "Tanggal Lahir", "Alamat", "Nomor Telepon",
                        "Jenis Kelamin" };
                break;
            case "Statistik Kunjungan":
                fileNamePrefix = "Laporan_Statistik_Kunjungan";
                query = "SELECT sk.id_kunjungan, sk.id_pasien, p.nama_pasien, sk.tanggal_kunjungan, sk.jenis_kunjungan "
                        +
                        "FROM statistik_kunjungan sk JOIN pasien p ON sk.id_pasien = p.id_pasien";
                columnHeaders = new String[] { "ID Kunjungan", "ID Pasien", "Nama Pasien", "Tanggal Kunjungan",
                        "Jenis Kunjungan" };
                break;
            case "Data Janji Temu":
                fileNamePrefix = "Laporan_Janji_Temu";
                query = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status "
                        +
                        "FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien";
                columnHeaders = new String[] { "ID Janji", "ID Pasien", "Nama Pasien", "Tanggal Janji", "Waktu Janji",
                        "Status" };
                break;
            case "Data Rekam Medis":
                fileNamePrefix = "Laporan_Rekam_Medis";
                query = "SELECT rm.id_rekam, rm.id_pasien, p.nama_pasien, rm.tanggal, rm.keluhan, rm.diagnosa, rm.tindakan, rm.obat_yang_diberikan, rm.catatan_tambahan "
                        +
                        "FROM rekam_medis rm JOIN pasien p ON rm.id_pasien = p.id_pasien";
                columnHeaders = new String[] { "ID Rekam", "ID Pasien", "Nama Pasien", "Tanggal", "Keluhan", "Diagnosa",
                        "Tindakan", "Obat Diberikan", "Catatan Tambahan" };
                break;
            default:
                JOptionPane.showMessageDialog(this, "Pilihan data tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileChooser.setSelectedFile(new java.io.File(fileNamePrefix + "_" + timestamp + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION)
            return;

        java.io.File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".pdf");
        }

        try (
                Connection conn = koneksi.getKoneksi();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "Data kosong. Tidak ada yang bisa diekspor.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
            document.open();

            // Font setup
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.HELVETICA, 10);

            // Title
            Paragraph title = new Paragraph("Laporan " + selectedDataType, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            document.add(
                    new Paragraph("Tanggal Cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())));
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(columnHeaders.length);
            table.setWidthPercentage(100);
            float[] columnWidths = new float[columnHeaders.length];
            for (int i = 0; i < columnHeaders.length; i++) {
                columnWidths[i] = 1f;
            }
            table.setWidths(columnWidths);

            // Header styling
            for (String header : columnHeaders) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(new Color(200, 221, 242));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }

            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
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

            JOptionPane.showMessageDialog(this, "Laporan PDF berhasil disimpan:\n" + fileToSave.getAbsolutePath(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menulis file PDF:\n" + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormExportPDF::new);
    }
}
