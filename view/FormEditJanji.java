package view;

import config.koneksi;
import controller.JanjiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormEditJanji extends JPanel {
    private JTextField txtIdJanji;
    private JTextField txtIdPasien;
    private JTextField txtNamaPasien;
    private JSpinner spinnerTanggal;
    private JSpinner spinnerWaktu;
    private JComboBox<String> cmbStatus;
    private JTable tableJanjiTemu;
    private DefaultTableModel modelJanjiTemu;

    private JButton btnUpdateStatus;
    private JButton btnSelesaikanJanji;

    public FormEditJanji() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Edit / Hapus Janji Temu");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form Input
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID Janji (Read-only, populated from table selection)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("ID Janji Temu:"), gbc);
        gbc.gridx = 1;
        txtIdJanji = new JTextField(15);
        txtIdJanji.setEditable(false);
        formPanel.add(txtIdJanji, gbc);

        row++;
        // ID Pasien (Read-only)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("ID Pasien:"), gbc);
        gbc.gridx = 1;
        txtIdPasien = new JTextField(15);
        txtIdPasien.setEditable(false);
        formPanel.add(txtIdPasien, gbc);

        row++;
        // Nama Pasien (Read-only, fetched from ID Pasien)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nama Pasien:"), gbc);
        gbc.gridx = 1;
        txtNamaPasien = new JTextField(15);
        txtNamaPasien.setEditable(false);
        formPanel.add(txtNamaPasien, gbc);

        row++;
        // Tanggal Janji
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Tanggal Janji:"), gbc);
        gbc.gridx = 1;
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd");
        spinnerTanggal.setEditor(dateEditor);
        formPanel.add(spinnerTanggal, gbc);

        row++;
        // Waktu Janji
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Waktu Janji:"), gbc);
        gbc.gridx = 1;
        spinnerWaktu = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinnerWaktu, "HH:mm");
        spinnerWaktu.setEditor(timeEditor);
        formPanel.add(spinnerWaktu, gbc);

        row++;
        // Status Janji
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(new String[]{"Menunggu", "Dilayani", "Batal"});
        formPanel.add(cmbStatus, gbc);

        add(formPanel, BorderLayout.WEST);

        // Tombol-tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnUpdate = new JButton("Update Janji");
        btnUpdate.setBackground(new Color(46, 204, 113)); // Green
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateJanjiTemu());
        buttonPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Hapus Janji");
        btnDelete.setBackground(new Color(231, 76, 60)); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteJanjiTemu());
        buttonPanel.add(btnDelete);

        btnUpdateStatus = new JButton("Update Status");
        btnUpdateStatus.setBackground(new Color(52, 152, 219)); // Blue
        btnUpdateStatus.setForeground(Color.WHITE);
        btnUpdateStatus.addActionListener(e -> updateJanjiStatus());
        buttonPanel.add(btnUpdateStatus);

        btnSelesaikanJanji = new JButton("Selesaikan Janji");
        btnSelesaikanJanji.setBackground(new Color(0, 128, 0)); // Darker green
        btnSelesaikanJanji.setForeground(Color.WHITE);
        btnSelesaikanJanji.addActionListener(e -> selesaikanJanjiTemuAksi());
        buttonPanel.add(btnSelesaikanJanji);

        add(buttonPanel, BorderLayout.SOUTH);

        // Tabel Janji Temu
        modelJanjiTemu = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Janji", "ID Pasien", "Nama Pasien", "Tanggal", "Waktu", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableJanjiTemu = new JTable(modelJanjiTemu);
        tableJanjiTemu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableJanjiTemu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableJanjiTemu.getSelectedRow() != -1) {
                populateFieldsFromTable();
            }
        });
        JScrollPane scrollPane = new JScrollPane(tableJanjiTemu);
        add(scrollPane, BorderLayout.CENTER);

        loadJanjiTemuData();
    }

    private void loadJanjiTemuData() {
        modelJanjiTemu.setRowCount(0);
        String sql = """
            SELECT
                jt.id_janji_temu,
                jt.id_pasien,
                p.nama_pasien,
                jt.tanggal_janji,
                jt.waktu_janji,
                jt.status
            FROM janji_temu jt
            JOIN pasien p ON jt.id_pasien = p.id_pasien
            ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC
        """;
        try (Connection conn = koneksi.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Pastikan tidak ada nilai null yang langsung dipanggil toString()
                Object idJanji = rs.getObject("id_janji_temu"); // Gunakan getObject untuk tipe int, mungkin null
                String idPasien = rs.getString("id_pasien");
                String namaPasien = rs.getString("nama_pasien");
                Date tanggalJanji = rs.getDate("tanggal_janji");
                Time waktuJanji = rs.getTime("waktu_janji"); // Menggunakan Time untuk waktu
                String status = rs.getString("status");

                modelJanjiTemu.addRow(new Object[]{
                        idJanji != null ? idJanji : "",
                        idPasien != null ? idPasien : "",
                        namaPasien != null ? namaPasien : "",
                        tanggalJanji,
                        waktuJanji,
                        status != null ? status : ""
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading janji temu data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void populateFieldsFromTable() {
        int selectedRow = tableJanjiTemu.getSelectedRow();
        if (selectedRow != -1) {
            // Pastikan mengambil data dari model tabel dan menangani kemungkinan null
            Object idJanjiObj = modelJanjiTemu.getValueAt(selectedRow, 0);
            Object idPasienObj = modelJanjiTemu.getValueAt(selectedRow, 1);
            Object namaPasienObj = modelJanjiTemu.getValueAt(selectedRow, 2);
            Date tanggal = (Date) modelJanjiTemu.getValueAt(selectedRow, 3);
            Date waktu = (Date) modelJanjiTemu.getValueAt(selectedRow, 4);
            Object statusObj = modelJanjiTemu.getValueAt(selectedRow, 5);

            txtIdJanji.setText(idJanjiObj != null ? idJanjiObj.toString() : "");
            txtIdPasien.setText(idPasienObj != null ? idPasienObj.toString() : "");
            txtNamaPasien.setText(namaPasienObj != null ? namaPasienObj.toString() : "");

            if (tanggal != null) {
                spinnerTanggal.setValue(tanggal);
            } else {
                spinnerTanggal.setValue(new Date()); // Default jika null
            }
            if (waktu != null) {
                spinnerWaktu.setValue(waktu);
            } else {
                spinnerWaktu.setValue(new Date()); // Default jika null
            }

            String status = statusObj != null ? statusObj.toString() : "Menunggu"; // Default jika null
            cmbStatus.setSelectedItem(status);
        }
    }

    private void updateJanjiTemu() {
        if (txtIdJanji.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih janji temu dari tabel untuk diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idJanji = Integer.parseInt(txtIdJanji.getText());
        Date tanggalJanji = (Date) spinnerTanggal.getValue();
        Date waktuJanji = (Date) spinnerWaktu.getValue();
        String status = cmbStatus.getSelectedItem().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String sql = "UPDATE janji_temu SET tanggal_janji = ?, waktu_janji = ?, status = ? WHERE id_janji_temu = ?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dateFormat.format(tanggalJanji));
            ps.setString(2, timeFormat.format(waktuJanji));
            ps.setString(3, status);
            ps.setInt(4, idJanji);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Janji temu berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadJanjiTemuData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui janji temu.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating janji temu: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteJanjiTemu() {
        if (txtIdJanji.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih janji temu yang ingin dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus janji temu ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try (Connection conn = koneksi.getKoneksi();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM janji_temu WHERE id_janji_temu = ?")) {

                ps.setInt(1, Integer.parseInt(txtIdJanji.getText()));

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Janji temu berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadJanjiTemuData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus janji temu.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting janji temu: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateJanjiStatus() {
        if (txtIdJanji.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih janji temu dari tabel untuk memperbarui status.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idJanji = Integer.parseInt(txtIdJanji.getText());
        String currentStatus = (String) modelJanjiTemu.getValueAt(tableJanjiTemu.getSelectedRow(), 5); // Cast to String

        if (currentStatus != null && (currentStatus.equals("Dilayani") || currentStatus.equals("Batal"))) {
            JOptionPane.showMessageDialog(this, "Janji temu dengan status 'Dilayani' atau 'Batal' tidak dapat diubah statusnya secara langsung.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String statusBaru = cmbStatus.getSelectedItem().toString();

        try {
            JanjiController.updateStatusJanjiTemu(idJanji, statusBaru);
            JOptionPane.showMessageDialog(this, "Status janji temu berhasil diperbarui menjadi '" + statusBaru + "'.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadJanjiTemuData();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui status janji temu: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void selesaikanJanjiTemuAksi() {
        int selectedRow = tableJanjiTemu.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih janji temu yang ingin diselesaikan dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idJanjiTemu = Integer.parseInt(txtIdJanji.getText());
        String currentStatus = (String) modelJanjiTemu.getValueAt(selectedRow, 5); // Cast to String

        if (currentStatus != null && currentStatus.equals("Dilayani")) {
            JOptionPane.showMessageDialog(this, "Janji temu ini sudah berstatus 'Dilayani'.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (currentStatus != null && currentStatus.equals("Batal")) {
            JOptionPane.showMessageDialog(this, "Janji temu ini sudah berstatus 'Batal', tidak dapat diselesaikan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextField tfDiagnosis = new JTextField(20);
        JTextField tfTindakan = new JTextField(20);
        JTextField tfObat = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Diagnosis:"));
        panel.add(tfDiagnosis);
        panel.add(new JLabel("Tindakan:"));
        panel.add(tfTindakan);
        panel.add(new JLabel("Obat:"));
        panel.add(tfObat);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Detail Rekam Medis untuk Janji Temu " + idJanjiTemu,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String diagnosis = tfDiagnosis.getText().trim();
            String tindakan = tfTindakan.getText().trim();
            String obat = tfObat.getText().trim();

            if (diagnosis.isEmpty() || tindakan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Diagnosis dan Tindakan tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                JanjiController.selesaikanJanjiTemu(idJanjiTemu, diagnosis, tindakan, obat);
                JOptionPane.showMessageDialog(this, "Janji temu berhasil diselesaikan dan dicatat ke rekam medis.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadJanjiTemuData();
                clearFields();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyelesaikan janji temu: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void clearFields() {
        txtIdJanji.setText("");
        txtIdPasien.setText("");
        txtNamaPasien.setText("");
        spinnerTanggal.setValue(new Date());
        spinnerWaktu.setValue(new Date());
        cmbStatus.setSelectedIndex(0);
        tableJanjiTemu.clearSelection();
    }
}