package view;

import config.koneksi; // Assuming you have a config.koneksi class for database connection
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Statistik extends JFrame {
    private JLabel lblTotalKunjungan;
    private JButton btnExport;
    private int totalKunjunganBulanIni;

    public Statistik() {
        setTitle("Statistik Kunjungan");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(45, 85, 125));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel headerLabel = new JLabel("📊 Statistik Kunjungan");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new FontUIResource("SansSerif", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        lblTotalKunjungan = new JLabel("Memuat data...");
        lblTotalKunjungan.setFont(new FontUIResource("Segoe UI", Font.BOLD, 20));
        lblTotalKunjungan.setForeground(new Color(60, 60, 60));
        contentPanel.add(lblTotalKunjungan, gbc);

        gbc.gridy = 1;
        btnExport = new JButton("Export Statistik ke PDF");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new FontUIResource("Segoe UI", Font.BOLD, 14));
        btnExport.setFocusPainted(false);
        btnExport.setEnabled(false);
        contentPanel.add(btnExport, gbc);

        add(contentPanel, BorderLayout.CENTER);

        btnExport.addActionListener(e -> exportToPDF());
        ambilTotalKunjungan();

        setVisible(true);
    }

    private void ambilTotalKunjungan() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        try (Connection conn = koneksi.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) FROM statistik_kunjungan WHERE MONTH(tanggal_kunjungan) = ? AND YEAR(tanggal_kunjungan) = ?")) {
            ps.setInt(1, currentMonth);
            ps.setInt(2, currentYear);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                totalKunjunganBulanIni = rs.getInt(1);
                lblTotalKunjungan.setText("Total Kunjungan Bulan Ini: " + totalKunjunganBulanIni);
                btnExport.setEnabled(true);
            } else {
                lblTotalKunjungan.setText("Gagal memuat data statistik.");
                JOptionPane.showMessageDialog(this, "Gagal memuat data statistik dari database.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblTotalKunjungan.setText("Error koneksi database.");
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengakses database: " + ex.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToPDF() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan PDF Statistik");
            // Suggest a filename
            chooser.setSelectedFile(new java.io.File("statistik_kunjungan_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));

            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            Document doc = new Document();
            // Use getSelectedFile().getAbsolutePath() to ensure full path
            PdfWriter.getInstance(doc, new FileOutputStream(chooser.getSelectedFile().getAbsolutePath()));
            doc.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(45, 85, 125));
            Paragraph title = new Paragraph("Statistik Kunjungan Klinik", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph(" ")); // Spacer

            // Date of Report Generation
            Font dateFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            // Corrected DateTimeFormatter pattern: "dd MMMM yyyy"
            Paragraph dateReport = new Paragraph(
                    "Tanggal Laporan: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                    dateFont);
            dateReport.setAlignment(Element.ALIGN_RIGHT);
            doc.add(dateReport);
            doc.add(new Paragraph(" ")); // Spacer

            // Statistik details
            Font detailFont = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.DARK_GRAY);
            Paragraph totalKunjunganParagraph = new Paragraph(
                    "Total Kunjungan Bulan Ini (" + LocalDate.now().getMonth().toString() + " "
                            + LocalDate.now().getYear() + "): " + totalKunjunganBulanIni,
                    detailFont);
            totalKunjunganParagraph.setAlignment(Element.ALIGN_LEFT);
            doc.add(totalKunjunganParagraph);
            doc.add(new Paragraph(" ")); // Spacer

            doc.add(new Paragraph("Rincian Kunjungan Harian Bulan Ini:", detailFont));
            doc.add(new Paragraph(" ")); // Spacer

            PdfPTable table = new PdfPTable(3); // Date, Patients, Jenis Kunjungan
            table.setWidthPercentage(80);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Table Headers
            Font headerTableFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Tanggal", headerTableFont));
            cell.setBackgroundColor(new Color(52, 152, 219));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Jumlah Pasien", headerTableFont));
            cell.setBackgroundColor(new Color(52, 152, 219));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Jenis Kunjungan", headerTableFont));
            cell.setBackgroundColor(new Color(52, 152, 219));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);

            // Fetch daily statistics
            try (Connection conn = koneksi.getKoneksi();
                    PreparedStatement psDaily = conn.prepareStatement(
                            "SELECT tanggal_kunjungan, COUNT(DISTINCT id_pasien) as jumlah_pasien, GROUP_CONCAT(DISTINCT jenis_kunjungan ORDER BY jenis_kunjungan SEPARATOR ', ') AS jenis_kunjungan_list "
                                    +
                                    "FROM statistik_kunjungan " +
                                    "WHERE MONTH(tanggal_kunjungan) = ? AND YEAR(tanggal_kunjungan) = ? " +
                                    "GROUP BY tanggal_kunjungan ORDER BY tanggal_kunjungan ASC")) {
                psDaily.setInt(1, LocalDate.now().getMonthValue());
                psDaily.setInt(2, LocalDate.now().getYear());
                ResultSet rsDaily = psDaily.executeQuery();

                Font dataTableFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
                while (rsDaily.next()) {
                    table.addCell(new Phrase(rsDaily.getDate("tanggal_kunjungan").toString(), dataTableFont));
                    table.addCell(new Phrase(String.valueOf(rsDaily.getInt("jumlah_pasien")), dataTableFont));
                    table.addCell(new Phrase(rsDaily.getString("jenis_kunjungan_list"), dataTableFont));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.err.println("Error fetching daily statistics for PDF: " + ex.getMessage());
            }

            doc.add(table);

            // Footer
            Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph("Generated by Aplikasi Klinik - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            doc.add(footer);

            doc.close();
            JOptionPane.showMessageDialog(this,
                    "PDF berhasil disimpan di:\n" + chooser.getSelectedFile().getAbsolutePath(), "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat PDF (Document Error): " + ex.getMessage(), "Error PDF",
                    JOptionPane.ERROR_MESSAGE);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal membuat PDF (I/O Error, mungkin file sedang digunakan atau izin akses): " + ex.getMessage(),
                    "Error PDF", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan tidak terduga saat membuat PDF: " + ex.getMessage(),
                    "Error Umum", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found. Please add the library.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(Statistik::new);
    }
}