package view;

import config.koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormExportCSV extends JFrame {

    private JComboBox<String> cbxDataType;
    private JButton btnExport;

    public FormExportCSV() {
        setTitle("Export Laporan ke CSV");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Export Laporan ke CSV");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
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
        lblDataType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxDataType = new JComboBox<>(new String[] {
                "Data Pasien", "Statistik Kunjungan", "Data Janji Temu", "Data Rekam Medis"
        });
        cbxDataType.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnExport = new JButton("Export ke CSV");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> exportDataToCSV());

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

    private void exportDataToCSV() {
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
                columnHeaders = new String[] { "ID Rekam Medis", "ID Pasien", "Nama Pasien", "Tanggal", "Keluhan",
                        "Diagnosa", "Tindakan", "Obat Diberikan", "Catatan Tambahan" };
                break;
            default:
                JOptionPane.showMessageDialog(this, "Pilihan data tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan CSV");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileChooser.setSelectedFile(new File(fileNamePrefix + "_" + timestamp + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (
                    Connection conn = koneksi.getKoneksi();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8"))) {
                writer.write("\uFEFF"); // Write BOM for UTF-8

                // Write header
                writer.write(String.join(";", columnHeaders));
                writer.newLine();

                ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String value = rs.getString(i);
                        value = (value == null) ? "" : value.replace("\"", "\"\"");
                        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
                            row.append("\"").append(value).append("\"");
                        } else {
                            row.append(value);
                        }
                        if (i < metaData.getColumnCount()) {
                            row.append(";");
                        }
                    }
                    writer.write(row.toString());
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(this, "Laporan berhasil disimpan:\n" + fileToSave.getAbsolutePath(),
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database:\n" + ex.getMessage(),
                        "Error Database", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menulis file CSV:\n" + ex.getMessage(),
                        "Error I/O", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver tidak ditemukan.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(FormExportCSV::new);
    }
}
