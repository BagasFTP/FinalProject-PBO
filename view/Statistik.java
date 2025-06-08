package view;

import config.koneksi;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Statistik extends JPanel {
    private JLabel lblTotalKunjungan, lblTrend;
    private JButton btnExport;
    private JTable tabelPreview;
    private int totalKunjunganBulanIni;
    private DefaultTableModel modelTabel;

    public Statistik() {
        setBackground(new Color(240, 246, 255));
        setLayout(new java.awt.BorderLayout(0, 0)); // <--- pakai prefix!

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(39, 60, 117));
        headerPanel.setLayout(new java.awt.BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 30, 15, 30));
        JLabel headerLabel = new JLabel("📊 Statistik Kunjungan", JLabel.LEFT);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 32));
        headerPanel.add(headerLabel, java.awt.BorderLayout.WEST);
        add(headerPanel, java.awt.BorderLayout.NORTH);

        // Main Content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 60, 30, 60));

        // Stat Card
        JPanel cardStat = new JPanel();
        cardStat.setBackground(Color.WHITE);
        cardStat.setBorder(new CompoundBorder(new LineBorder(new Color(52, 152, 219), 2, true),
                new EmptyBorder(25, 40, 25, 40)));
        cardStat.setLayout(new BoxLayout(cardStat, BoxLayout.Y_AXIS));
        cardStat.setMaximumSize(new Dimension(350, 170));

        lblTotalKunjungan = new JLabel("Memuat data...");
        lblTotalKunjungan.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        lblTotalKunjungan.setForeground(new Color(41, 128, 185));
        lblTotalKunjungan.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardStat.add(lblTotalKunjungan);

        lblTrend = new JLabel("");
        lblTrend.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 15));
        lblTrend.setForeground(new Color(39, 174, 96));
        lblTrend.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardStat.add(Box.createVerticalStrut(10));
        cardStat.add(lblTrend);

        contentPanel.add(cardStat);
        contentPanel.add(Box.createVerticalStrut(30));

        // Table Preview
        JLabel lblPreview = new JLabel("Preview Rincian Harian Bulan Ini");
        lblPreview.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPreview.setForeground(new Color(52, 73, 94));
        contentPanel.add(lblPreview);
        contentPanel.add(Box.createVerticalStrut(8));

        String[] kolom = {"Tanggal", "Jumlah Pasien", "Jenis Kunjungan"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelPreview = new JTable(modelTabel);
        tabelPreview.setRowHeight(28);
        tabelPreview.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        tabelPreview.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tabelPreview.setSelectionBackground(new Color(127, 179, 213));
        JScrollPane scrollTable = new JScrollPane(tabelPreview);
        scrollTable.setMaximumSize(new Dimension(600, 180));
        contentPanel.add(scrollTable);
        contentPanel.add(Box.createVerticalStrut(25));

        // Export Button
        btnExport = new JButton("⬇️ Export Statistik ke PDF");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        btnExport.setFocusPainted(false);
        btnExport.setEnabled(false);
        btnExport.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effect
        btnExport.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnExport.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(MouseEvent e) {
                btnExport.setBackground(new Color(46, 204, 113));
            }
        });

        contentPanel.add(btnExport);
        add(contentPanel, java.awt.BorderLayout.CENTER);

        btnExport.addActionListener(e -> exportToPDF());
        ambilTotalKunjungan();
        ambilPreviewHarian();
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

            int totalBulanLalu = 0;
            try (PreparedStatement psPrev = conn.prepareStatement(
                    "SELECT COUNT(*) FROM statistik_kunjungan WHERE MONTH(tanggal_kunjungan) = ? AND YEAR(tanggal_kunjungan) = ?")) {
                int prevMonth = (currentMonth == 1 ? 12 : currentMonth - 1);
                int prevYear = (currentMonth == 1 ? currentYear - 1 : currentYear);
                psPrev.setInt(1, prevMonth);
                psPrev.setInt(2, prevYear);
                ResultSet rsPrev = psPrev.executeQuery();
                if (rsPrev.next()) totalBulanLalu = rsPrev.getInt(1);
            }

            if (rs.next()) {
                totalKunjunganBulanIni = rs.getInt(1);
                lblTotalKunjungan.setText("Total Kunjungan Bulan Ini: " + totalKunjunganBulanIni);

                int diff = totalKunjunganBulanIni - totalBulanLalu;
                if (totalBulanLalu == 0) lblTrend.setText("");
                else if (diff > 0)
                    lblTrend.setText("⬆️ Naik " + diff + " dari bulan lalu (" + totalBulanLalu + ")");
                else if (diff < 0)
                    lblTrend.setText("⬇️ Turun " + Math.abs(diff) + " dari bulan lalu (" + totalBulanLalu + ")");
                else lblTrend.setText("⏺️ Sama dengan bulan lalu");

                btnExport.setEnabled(true);
            } else {
                lblTotalKunjungan.setText("Gagal memuat data statistik.");
            }
        } catch (SQLException ex) {
            lblTotalKunjungan.setText("Error koneksi database.");
        }
    }

    private void ambilPreviewHarian() {
        modelTabel.setRowCount(0);
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT tanggal_kunjungan, COUNT(DISTINCT id_pasien) as jumlah_pasien, GROUP_CONCAT(DISTINCT jenis_kunjungan ORDER BY jenis_kunjungan SEPARATOR ', ') AS jenis_kunjungan_list " +
                             "FROM statistik_kunjungan " +
                             "WHERE MONTH(tanggal_kunjungan) = ? AND YEAR(tanggal_kunjungan) = ? " +
                             "GROUP BY tanggal_kunjungan ORDER BY tanggal_kunjungan DESC LIMIT 7")) {
            ps.setInt(1, LocalDate.now().getMonthValue());
            ps.setInt(2, LocalDate.now().getYear());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelTabel.addRow(new Object[]{
                        rs.getDate("tanggal_kunjungan"),
                        rs.getInt("jumlah_pasien"),
                        rs.getString("jenis_kunjungan_list")
                });
            }
        } catch (SQLException ex) {
            // Optional: tampilkan error preview
        }
    }

    private void exportToPDF() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan PDF Statistik");
            chooser.setSelectedFile(new java.io.File("statistik_kunjungan_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));

            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(chooser.getSelectedFile().getAbsolutePath()));
            doc.open();

            // ----------- PAKAI FontFactory dari iText, bukan java.awt.Font -------------
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(45, 85, 125));
            Paragraph title = new Paragraph("Statistik Kunjungan Klinik", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph(" "));

            com.lowagie.text.Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, com.lowagie.text.Font.NORMAL);
            Paragraph dateReport = new Paragraph(
                    "Tanggal Laporan: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                    dateFont);
            dateReport.setAlignment(Element.ALIGN_RIGHT);
            doc.add(dateReport);
            doc.add(new Paragraph(" "));

            com.lowagie.text.Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 14, com.lowagie.text.Font.NORMAL, Color.DARK_GRAY);
            Paragraph totalKunjunganParagraph = new Paragraph(
                    "Total Kunjungan Bulan Ini (" + LocalDate.now().getMonth().toString() + " "
                            + LocalDate.now().getYear() + "): " + totalKunjunganBulanIni,
                    detailFont);
            totalKunjunganParagraph.setAlignment(Element.ALIGN_LEFT);
            doc.add(totalKunjunganParagraph);
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Rincian Kunjungan Harian Bulan Ini:", detailFont));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(80);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            com.lowagie.text.Font headerTableFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
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

            // Data PDF
            try (Connection conn = koneksi.getKoneksi();
                 PreparedStatement psDaily = conn.prepareStatement(
                         "SELECT tanggal_kunjungan, COUNT(DISTINCT id_pasien) as jumlah_pasien, GROUP_CONCAT(DISTINCT jenis_kunjungan ORDER BY jenis_kunjungan SEPARATOR ', ') AS jenis_kunjungan_list " +
                                 "FROM statistik_kunjungan " +
                                 "WHERE MONTH(tanggal_kunjungan) = ? AND YEAR(tanggal_kunjungan) = ? " +
                                 "GROUP BY tanggal_kunjungan ORDER BY tanggal_kunjungan ASC")) {
                psDaily.setInt(1, LocalDate.now().getMonthValue());
                psDaily.setInt(2, LocalDate.now().getYear());
                ResultSet rsDaily = psDaily.executeQuery();

                com.lowagie.text.Font dataTableFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.lowagie.text.Font.NORMAL);
                while (rsDaily.next()) {
                    table.addCell(new Phrase(rsDaily.getDate("tanggal_kunjungan").toString(), dataTableFont));
                    table.addCell(new Phrase(String.valueOf(rsDaily.getInt("jumlah_pasien")), dataTableFont));
                    table.addCell(new Phrase(rsDaily.getString("jenis_kunjungan_list"), dataTableFont));
                }
            }
            doc.add(table);

            com.lowagie.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.lowagie.text.Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph("Generated by Aplikasi Klinik - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            doc.add(footer);

            doc.close();
            JOptionPane.showMessageDialog(this,
                    "PDF berhasil disimpan di:\n" + chooser.getSelectedFile().getAbsolutePath(), "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuat PDF (Document Error): " + ex.getMessage(), "Error PDF",
                    JOptionPane.ERROR_MESSAGE);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal membuat PDF (I/O Error, mungkin file sedang digunakan atau izin akses): " + ex.getMessage(),
                    "Error PDF", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan tidak terduga saat membuat PDF: " + ex.getMessage(),
                    "Error Umum", JOptionPane.ERROR_MESSAGE);
        }
    }
}
