package view;

import config.koneksi; // Import your database connection class

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormExportCSV extends JFrame {

    private JComboBox<String> cbxDataType;
    private JButton btnExport;

    public FormExportCSV() {
        setTitle("Export Laporan ke CSV");
        setSize(450, 250); // Adjusted size for more elements
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout for better layout

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Export Laporan ke CSV");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblDataType = new JLabel("Pilih Data untuk Export:");
        lblDataType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxDataType = new JComboBox<>(new String[]{"Data Pasien", "Statistik Kunjungan", "Data Janji Temu", "Data Rekam Medis"});
        cbxDataType.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnExport = new JButton("Export ke CSV");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(lblDataType, gbc);

        gbc.gridx = 1;
        contentPanel.add(cbxDataType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span two columns
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnExport, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Add action listener to the export button
        btnExport.addActionListener(e -> exportDataToCSV());

        setVisible(true);
    }

    private void exportDataToCSV() {
        String selectedDataType = (String) cbxDataType.getSelectedItem();
        String tableName = ""; // Not strictly needed for this implementation, but kept for context
        String fileNamePrefix = "";
        String[] columnHeaders = null;
        String query = "";

        switch (selectedDataType) {
            case "Data Pasien":
                tableName = "pasien";
                fileNamePrefix = "Laporan_Pasien";
                query = "SELECT id_pasien, nama_pasien, tanggal_lahir, alamat, no_telepon, jenis_kelamin FROM pasien";
                columnHeaders = new String[]{"ID Pasien", "Nama Pasien", "Tanggal Lahir", "Alamat", "Nomor Telepon", "Jenis Kelamin"};
                break;
            case "Statistik Kunjungan":
                tableName = "statistik_kunjungan";
                fileNamePrefix = "Laporan_Statistik_Kunjungan";
                // Join with pasien to get patient names. 'status_pembayaran' and 'biaya' are intentionally excluded.
                query = "SELECT sk.id_kunjungan, sk.id_pasien, p.nama_pasien, sk.tanggal_kunjungan, sk.jenis_kunjungan" +
                        " FROM statistik_kunjungan sk JOIN pasien p ON sk.id_pasien = p.id_pasien";
                columnHeaders = new String[]{"ID Kunjungan", "ID Pasien", "Nama Pasien", "Tanggal Kunjungan", "Jenis Kunjungan"};
                break;
            case "Data Janji Temu":
                tableName = "janji_temu";
                fileNamePrefix = "Laporan_Janji_Temu";
                // Join with pasien to get patient names
                query = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status " +
                        "FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien";
                columnHeaders = new String[]{"ID Janji", "ID Pasien", "Nama Pasien", "Tanggal Janji", "Waktu Janji", "Status"};
                break;
            case "Data Rekam Medis":
                tableName = "rekam_medis";
                fileNamePrefix = "Laporan_Rekam_Medis";
                // Corrected query: Added comma and included more relevant columns from klinik.sql
                query = "SELECT rm.id_rekam, rm.id_pasien, p.nama_pasien, rm.tanggal, rm.keluhan, rm.diagnosa, rm.tindakan, rm.obat_yang_diberikan, rm.catatan_tambahan " +
                        "FROM rekam_medis rm JOIN pasien p ON rm.id_pasien = p.id_pasien";
                // Updated column headers to match the new query
                columnHeaders = new String[]{"ID Rekam Medis", "ID Pasien", "Nama Pasien", "Tanggal", "Keluhan", "Diagnosa", "Tindakan", "Obat Diberikan", "Catatan Tambahan"};
                break;
            default:
                JOptionPane.showMessageDialog(this, "Pilihan data tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan CSV");
        // Set default file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileChooser.setSelectedFile(new File(fileNamePrefix + "_" + timestamp + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (Connection conn = koneksi.getKoneksi();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query);
                 BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

                // Write CSV header
                if (columnHeaders != null) {
                    writer.write(String.join(",", columnHeaders));
                } else {
                    // Fallback: Get column names from ResultSetMetaData if no custom headers are provided
                    ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        writer.write(metaData.getColumnLabel(i)); // Use getColumnLabel for display names
                        if (i < metaData.getColumnCount()) {
                            writer.write(",");
                        }
                    }
                }
                writer.newLine();

                // Write CSV data
                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String value = rs.getString(i);
                        // Handle commas in data by enclosing in quotes
                        if (value != null && value.contains(",")) {
                            row.append("\"").append(value.replace("\"", "\"\"")).append("\"");
                        } else {
                            row.append(value != null ? value : "");
                        }
                        if (i < rs.getMetaData().getColumnCount()) {
                            row.append(",");
                        }
                    }
                    writer.write(row.toString());
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(this, "Laporan berhasil diekspor ke:\n" + fileToSave.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error saat mengambil data dari database: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saat menulis file CSV: " + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan tidak terduga: " + ex.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Ensure JDBC driver is loaded (important for standalone execution)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found. Please add the library.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(FormExportCSV::new);
    }
}