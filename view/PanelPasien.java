package view;

import config.koneksi;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PanelPasien extends JPanel {
    private JTextField tfId, tfNama, tfAlamat, tfNoTelepon;
    private JSpinner spinnerTgl;
    private JComboBox<String> cbJenisKelamin;
    private JTable tablePasien;
    private DefaultTableModel tableModel;

    // Definisikan SimpleDateFormat untuk UI dan Database
    private final SimpleDateFormat UI_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy"); // Format untuk JSpinner
    private final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); // Format untuk Database

    public PanelPasien() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        add(buatHeader(), BorderLayout.NORTH);
        add(buatKonten(), BorderLayout.CENTER);

        loadDataPasien();
    }

    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblJudul = new JLabel("Form Registrasi Pasien");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);

        header.add(lblJudul, BorderLayout.WEST);
        return header;
    }

    private JPanel buatKonten() {
        JPanel konten = new JPanel(new BorderLayout(10, 10));
        konten.setBackground(Color.WHITE);
        konten.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Data Pasien", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID Pasien
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("ID Pasien:"), gbc);
        gbc.gridx = 1;
        tfId = new JTextField(20);
        formPanel.add(tfId, gbc);

        row++;
        // Nama Pasien
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nama Pasien:"), gbc);
        gbc.gridx = 1;
        tfNama = new JTextField(20);
        formPanel.add(tfNama, gbc);

        row++;
        // Tanggal Lahir
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Tanggal Lahir:"), gbc);
        gbc.gridx = 1;
        spinnerTgl = new JSpinner(new SpinnerDateModel());
        // --- PERUBAHAN DI SINI: Ubah format editor spinner ---
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerTgl, UI_DATE_FORMAT.toPattern());
        spinnerTgl.setEditor(dateEditor);
        formPanel.add(spinnerTgl, gbc);

        row++;
        // Jenis Kelamin
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1;
        cbJenisKelamin = new JComboBox<>(new String[]{"Pilih", "Laki-laki", "Perempuan"});
        formPanel.add(cbJenisKelamin, gbc);

        row++;
        // Alamat
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        tfAlamat = new JTextField(20);
        formPanel.add(tfAlamat, gbc);

        row++;
        // No Telepon (sesuai 'telepon' di DB)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1;
        tfNoTelepon = new JTextField(20);
        formPanel.add(tfNoTelepon, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBackground(new Color(46, 204, 113)); // Green
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpanDataPasien());
        buttonPanel.add(btnSimpan);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(52, 152, 219)); // Blue
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateDataPasien());
        buttonPanel.add(btnUpdate);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBackground(new Color(231, 76, 60)); // Red
        btnHapus.setForeground(Color.WHITE);
        btnHapus.addActionListener(e -> hapusDataPasien());
        buttonPanel.add(btnHapus);

        JButton btnReset = new JButton("Reset");
        btnReset.setBackground(new Color(149, 165, 166)); // Gray
        btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> resetForm());
        buttonPanel.add(btnReset);

        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);


        konten.add(formPanel, BorderLayout.NORTH);
        konten.add(buatTabelPasien(), BorderLayout.CENTER);

        return konten;
    }

    private JScrollPane buatTabelPasien() {
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Pasien", "Nama", "Tgl Lahir", "Jenis Kelamin", "Alamat", "Telepon"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePasien = new JTable(tableModel);
        tablePasien.getTableHeader().setReorderingAllowed(false);
        tablePasien.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablePasien.setRowHeight(25);
        tablePasien.getTableHeader().setBackground(new Color(52, 152, 219));
        tablePasien.getTableHeader().setForeground(Color.WHITE);
        tablePasien.setFillsViewportHeight(true);

        tablePasien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablePasien.getSelectedRow() != -1) {
                populateFieldsFromTable();
            }
        });
        return new JScrollPane(tablePasien);
    }

    private void populateFieldsFromTable() {
        int selectedRow = tablePasien.getSelectedRow();
        if (selectedRow != -1) {
            tfId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            tfNama.setText(tableModel.getValueAt(selectedRow, 1).toString());

            try {
                // Tanggal Lahir: Parse dari format DB (String) ke Date
                String tglLahirStr = tableModel.getValueAt(selectedRow, 2).toString();
                Date tglLahir = DB_DATE_FORMAT.parse(tglLahirStr); // Menggunakan DB_DATE_FORMAT
                spinnerTgl.setValue(tglLahir);
            } catch (Exception ex) {
                ex.printStackTrace();
                spinnerTgl.setValue(new Date()); // Fallback jika parsing gagal
            }

            String jenisKelamin = tableModel.getValueAt(selectedRow, 3).toString();
            cbJenisKelamin.setSelectedItem(jenisKelamin);

            tfAlamat.setText(tableModel.getValueAt(selectedRow, 4).toString());
            tfNoTelepon.setText(tableModel.getValueAt(selectedRow, 5).toString());
        }
    }

    private void simpanDataPasien() {
        String id = tfId.getText().trim();
        String nama = tfNama.getText().trim();
        Date tanggalLahir = (Date) spinnerTgl.getValue();
        String jenisKelamin = cbJenisKelamin.getSelectedItem().toString();
        String alamat = tfAlamat.getText().trim();
        String telepon = tfNoTelepon.getText().trim();

        if (id.isEmpty() || nama.isEmpty() || jenisKelamin.equals("Pilih") || alamat.isEmpty() || telepon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- PERUBAHAN DI SINI: Format tanggal untuk database ---
        String tglLahirStr = DB_DATE_FORMAT.format(tanggalLahir);

        String sql = "INSERT INTO pasien (id_pasien, nama_pasien, tanggal_lahir, jenis_kelamin, alamat, telepon) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, nama);
            ps.setString(3, tglLahirStr);
            ps.setString(4, jenisKelamin);
            ps.setString(5, alamat);
            ps.setString(6, telepon);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pasien berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadDataPasien();
            resetForm();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                JOptionPane.showMessageDialog(this, "ID Pasien sudah ada. Gunakan ID lain.", "Error Duplikasi ID", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data pasien: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateDataPasien() {
        String id = tfId.getText().trim();
        String nama = tfNama.getText().trim();
        Date tanggalLahir = (Date) spinnerTgl.getValue();
        String jenisKelamin = cbJenisKelamin.getSelectedItem().toString();
        String alamat = tfAlamat.getText().trim();
        String telepon = tfNoTelepon.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pasien dari tabel untuk diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nama.isEmpty() || jenisKelamin.equals("Pilih") || alamat.isEmpty() || telepon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- PERUBAHAN DI SINI: Format tanggal untuk database ---
        String tglLahirStr = DB_DATE_FORMAT.format(tanggalLahir);

        String sql = "UPDATE pasien SET nama_pasien = ?, tanggal_lahir = ?, jenis_kelamin = ?, alamat = ?, telepon = ? WHERE id_pasien = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nama);
            ps.setString(2, tglLahirStr);
            ps.setString(3, jenisKelamin);
            ps.setString(4, alamat);
            ps.setString(5, telepon);
            ps.setString(6, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data pasien berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDataPasien();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data pasien. ID Pasien tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data pasien: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void hapusDataPasien() {
        String id = tfId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pasien dari tabel untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus data pasien dengan ID " + id + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM pasien WHERE id_pasien = ?";
            try (Connection conn = koneksi.getKoneksi();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, id);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data pasien berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadDataPasien();
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data pasien. ID Pasien tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                if (ex.getSQLState().startsWith("23")) {
                     JOptionPane.showMessageDialog(this, "Tidak dapat menghapus pasien ini karena ada data terkait di tabel lain (misal: janji temu, rekam medis, antrian).", "Error Hapus Data", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data pasien: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }
    }

    private void resetForm() {
        tfId.setText("");
        tfNama.setText("");
        tfAlamat.setText("");
        tfNoTelepon.setText("");
        spinnerTgl.setValue(new Date());
        cbJenisKelamin.setSelectedIndex(0);
        tablePasien.clearSelection();
    }

    private void loadDataPasien() {
        tableModel.setRowCount(0);
        try (Connection conn = koneksi.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pasien")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getString("tanggal_lahir"), // Ini akan menjadi string "yyyy-MM-dd" dari DB
                        rs.getString("jenis_kelamin"),
                        rs.getString("alamat"),
                        rs.getString("telepon")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}