package view;

import config.koneksi; // Assuming you have a config.koneksi class for database connection

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SpinnerDateModel;

public class FormEditJanji extends JFrame {
    private JSpinner spinnerTanggal;
    private JTextField tfIdJanji, tfIdPasien; // Added tfIdJanji and tfIdPasien for editing
    private JTable tableJanji;
    private DefaultTableModel tableModel;
    private JButton btnEdit, btnHapus;

    public FormEditJanji() {
        setTitle("Edit / Hapus Janji");
        setSize(800, 600); // Increased size to accommodate the table
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout for better layout management
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Edit / Hapus Janji Temu");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Input and Button Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lIdJanji = new JLabel("ID Janji");
        tfIdJanji = new JTextField(15);
        tfIdJanji.setEditable(false); // ID Janji should be selected from table, not manually entered

        JLabel lIdPasien = new JLabel("ID Pasien");
        tfIdPasien = new JTextField(15);
        tfIdPasien.setEditable(false); // ID Pasien should be selected from table

        JLabel lTanggal = new JLabel("Tanggal Baru");
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd");
        spinnerTanggal.setEditor(editor);

        btnEdit = new JButton("Edit Janji");
        btnHapus = new JButton("Hapus Janji");

        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(Color.WHITE);

        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Add components to inputPanel
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(lIdJanji, gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(tfIdJanji, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(lIdPasien, gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(tfIdPasien, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(lTanggal, gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(spinnerTanggal, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);
        buttonPanel.setBackground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.WEST); // Add input panel to the left

        // Table Panel
        tableModel = new DefaultTableModel(new String[]{"ID Janji", "ID Pasien", "Nama Pasien", "Tanggal Janji", "Waktu Janji", "Status"}, 0);
        tableJanji = new JTable(tableModel);
        tableJanji.setRowHeight(25);
        tableJanji.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableJanji.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableJanji.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        tableJanji.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableJanji.getSelectedRow() != -1) {
                int selectedRow = tableJanji.getSelectedRow();
                tfIdJanji.setText(tableModel.getValueAt(selectedRow, 0).toString());
                tfIdPasien.setText(tableModel.getValueAt(selectedRow, 1).toString());
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tableModel.getValueAt(selectedRow, 3).toString());
                    spinnerTanggal.setValue(date);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error parsing date: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableJanji);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Daftar Janji Temu",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER); // Add table to the center

        loadJanjiTemu(); // Load existing appointments when the form opens

        btnEdit.addActionListener(e -> editJanji());
        btnHapus.addActionListener(e -> hapusJanji());

        setVisible(true);
    }

    private void loadJanjiTemu() {
        tableModel.setRowCount(0); // Clear existing data
        try (Connection conn = koneksi.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id_janji_temu"),
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getDate("tanggal_janji"),
                        rs.getTime("waktu_janji"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void editJanji() {
        String idJanji = tfIdJanji.getText().trim();
        Date tanggalBaru = (Date) spinnerTanggal.getValue();
        String tglFormatted = new SimpleDateFormat("yyyy-MM-dd").format(tanggalBaru);

        if (idJanji.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih janji yang ingin diedit dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pst = conn.prepareStatement("UPDATE janji_temu SET tanggal_janji = ? WHERE id_janji_temu = ?")) {
            pst.setString(1, tglFormatted);
            pst.setString(2, idJanji);
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Janji berhasil diedit ke tanggal: " + tglFormatted, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadJanjiTemu(); // Reload data after edit
                resetForm(); // Clear the input fields
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengedit janji. ID Janji mungkin tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error editing appointment: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void hapusJanji() {
        String idJanji = tfIdJanji.getText().trim();

        if (idJanji.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih janji yang ingin dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus janji ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = koneksi.getKoneksi();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM janji_temu WHERE id_janji_temu = ?")) {
                pst.setString(1, idJanji);
                int affectedRows = pst.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Janji berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadJanjiTemu(); // Reload data after delete
                    resetForm(); // Clear the input fields
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus janji. ID Janji mungkin tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting appointment: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void resetForm() {
        tfIdJanji.setText("");
        tfIdPasien.setText("");
        spinnerTanggal.setValue(new Date()); // Reset to current date
        tableJanji.clearSelection(); // Clear table selection
    }
}