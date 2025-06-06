package view;

import config.koneksi;

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

        // Panel input
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detail Janji Temu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lIdJanji = new JLabel("ID Janji:");
        lIdJanji.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(lIdJanji, gbc);

        txtIdJanji = new JTextField();
        txtIdJanji.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtIdJanji.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        inputPanel.add(txtIdJanji, gbc);

        JLabel lIdPasien = new JLabel("ID Pasien:");
        lIdPasien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(lIdPasien, gbc);

        txtIdPasien = new JTextField();
        txtIdPasien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtIdPasien.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        inputPanel.add(txtIdPasien, gbc);

        JLabel lNamaPasien = new JLabel("Nama Pasien:");
        lNamaPasien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(lNamaPasien, gbc);

        txtNamaPasien = new JTextField();
        txtNamaPasien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNamaPasien.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        inputPanel.add(txtNamaPasien, gbc);

        JLabel lTanggal = new JLabel("Tanggal Janji:");
        lTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        inputPanel.add(lTanggal, gbc);

        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd"));
        spinnerTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        inputPanel.add(spinnerTanggal, gbc);

        JLabel lWaktu = new JLabel("Waktu Janji:");
        lWaktu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        inputPanel.add(lWaktu, gbc);

        spinnerWaktu = new JSpinner(new SpinnerDateModel());
        spinnerWaktu.setEditor(new JSpinner.DateEditor(spinnerWaktu, "HH:mm:ss"));
        spinnerWaktu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        inputPanel.add(spinnerWaktu, gbc);

        JLabel lStatus = new JLabel("Status:");
        lStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        inputPanel.add(lStatus, gbc);

        cmbStatus = new JComboBox<>(new String[] { "Dijadwalkan", "Selesai", "Batal" });
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        inputPanel.add(cmbStatus, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnUpdate = new JButton("Update Janji");
        btnUpdate.setBackground(new Color(46, 204, 113));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(e -> updateJanjiTemu());
        buttonPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Hapus Janji");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> deleteJanjiTemu());
        buttonPanel.add(btnDelete);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        formPanel.setBackground(Color.WHITE);

        // Tabel Janji Temu
        modelJanjiTemu = new DefaultTableModel(
                new String[] { "ID Janji", "ID Pasien", "Nama Pasien", "Tanggal", "Waktu", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table non-editable
            }
        };
        tableJanjiTemu = new JTable(modelJanjiTemu);
        tableJanjiTemu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableJanjiTemu.setRowHeight(25);
        tableJanjiTemu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableJanjiTemu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableJanjiTemu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableJanjiTemu.getSelectedRow() != -1) {
                displaySelectedJanji();
            }
        });
        JScrollPane scrollJanji = new JScrollPane(tableJanjiTemu);
        scrollJanji.setBorder(BorderFactory.createTitledBorder("Daftar Janji Temu"));
        scrollJanji.setPreferredSize(new Dimension(850, 300));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(scrollJanji, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        loadJanjiTemuData();
    }

    private void loadJanjiTemuData() {
        modelJanjiTemu.setRowCount(0);
        try (Connection conn = koneksi.getKoneksi();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT jt.id_janji_temu, p.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status " +
                                "FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC")) {

            while (rs.next()) {
                modelJanjiTemu.addRow(new Object[] {
                        rs.getInt("id_janji_temu"),
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getDate("tanggal_janji"),
                        rs.getTime("waktu_janji"),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading janji temu data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displaySelectedJanji() {
        int selectedRow = tableJanjiTemu.getSelectedRow();
        if (selectedRow != -1) {
            txtIdJanji.setText(modelJanjiTemu.getValueAt(selectedRow, 0).toString());
            txtIdPasien.setText(modelJanjiTemu.getValueAt(selectedRow, 1).toString());
            txtNamaPasien.setText(modelJanjiTemu.getValueAt(selectedRow, 2).toString());
            spinnerTanggal.setValue(modelJanjiTemu.getValueAt(selectedRow, 3));
            spinnerWaktu.setValue(modelJanjiTemu.getValueAt(selectedRow, 4));
            cmbStatus.setSelectedItem(modelJanjiTemu.getValueAt(selectedRow, 5).toString());
        }
    }

    private void updateJanjiTemu() {
        if (txtIdJanji.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih janji temu yang akan diupdate dari tabel.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = koneksi.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE janji_temu SET tanggal_janji = ?, waktu_janji = ?, status = ? WHERE id_janji_temu = ?")) {

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            String tanggalJanji = sdfDate.format((Date) spinnerTanggal.getValue());
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            String waktuJanji = sdfTime.format((Date) spinnerWaktu.getValue());

            ps.setString(1, tanggalJanji);
            ps.setString(2, waktuJanji);
            ps.setString(3, cmbStatus.getSelectedItem().toString());
            ps.setInt(4, Integer.parseInt(txtIdJanji.getText()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Janji temu berhasil diupdate.", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadJanjiTemuData(); // Refresh table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate janji temu.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating janji temu: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteJanjiTemu() {
        if (txtIdJanji.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih janji temu yang akan dihapus dari tabel.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus janji temu ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = koneksi.getKoneksi();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM janji_temu WHERE id_janji_temu = ?")) {

                ps.setInt(1, Integer.parseInt(txtIdJanji.getText()));

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Janji temu berhasil dihapus.", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadJanjiTemuData(); // Refresh table
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus janji temu.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting janji temu: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        txtIdJanji.setText("");
        txtIdPasien.setText("");
        txtNamaPasien.setText("");
        spinnerTanggal.setValue(new Date()); // Reset to current date
        spinnerWaktu.setValue(new Date()); // Reset to current time
        cmbStatus.setSelectedIndex(0); // Reset to default status
        tableJanjiTemu.clearSelection();
    }
    // Removed main method
}