    package view;

    import config.koneksi;

    import javax.swing.*;
    import javax.swing.border.EmptyBorder;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.sql.*;
    import java.text.SimpleDateFormat;
    import java.util.Date;

    public class FormRekamMedis extends JPanel {
        private JTextField txtIdRekamMedis;
        private JTextField txtIdPasien;
        private JTextField txtNamaPasien;
        private JTextArea txtAreaDiagnosis;
        private JTextArea txtAreaTindakan;
        private JSpinner spinnerTanggal;
        private JTable tableRekamMedis;
        private DefaultTableModel modelRekamMedis;

        public FormRekamMedis() {
            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(10, 10, 10, 10));

            // Header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(52, 152, 219));
            headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
            JLabel lblJudul = new JLabel("Manajemen Rekam Medis");
            lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblJudul.setForeground(Color.WHITE);
            headerPanel.add(lblJudul, BorderLayout.WEST);
            add(headerPanel, BorderLayout.NORTH);

            // Panel input
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBackground(Color.WHITE);
            inputPanel.setBorder(BorderFactory.createTitledBorder("Detail Rekam Medis"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lIdRekamMedis = new JLabel("ID Rekam Medis:");
            lIdRekamMedis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 0;
            gbc.gridy = 0;
            inputPanel.add(lIdRekamMedis, gbc);

            txtIdRekamMedis = new JTextField();
            txtIdRekamMedis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txtIdRekamMedis.setEditable(false);
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            inputPanel.add(txtIdRekamMedis, gbc);

            JLabel lIdPasien = new JLabel("ID Pasien:");
            lIdPasien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            inputPanel.add(lIdPasien, gbc);

            txtIdPasien = new JTextField();
            txtIdPasien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            // Add a button to search for patient or integrate with a patient selection
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            inputPanel.add(txtIdPasien, gbc);

            JButton btnCariPasien = new JButton("Cari Pasien");
            btnCariPasien.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnCariPasien.addActionListener(e -> findPasien());
            gbc.gridx = 2;
            gbc.gridy = 1;
            gbc.weightx = 0;
            inputPanel.add(btnCariPasien, gbc);


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
            gbc.gridwidth = 2; // Span across 2 columns
            gbc.weightx = 1.0;
            inputPanel.add(txtNamaPasien, gbc);
            gbc.gridwidth = 1; // Reset to 1

            JLabel lTanggal = new JLabel("Tanggal Rekam:");
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
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            inputPanel.add(spinnerTanggal, gbc);
            gbc.gridwidth = 1;

            JLabel lDiagnosis = new JLabel("Diagnosis:");
            lDiagnosis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST; // Align label to top-left
            inputPanel.add(lDiagnosis, gbc);

            txtAreaDiagnosis = new JTextArea(5, 20);
            txtAreaDiagnosis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txtAreaDiagnosis.setLineWrap(true);
            txtAreaDiagnosis.setWrapStyleWord(true);
            JScrollPane scrollDiagnosis = new JScrollPane(txtAreaDiagnosis);
            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 0.5; // Allow vertical growth
            gbc.fill = GridBagConstraints.BOTH;
            inputPanel.add(scrollDiagnosis, gbc);
            gbc.gridwidth = 1;
            gbc.weighty = 0; // Reset weighty

            JLabel lTindakan = new JLabel("Tindakan:");
            lTindakan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            inputPanel.add(lTindakan, gbc);

            txtAreaTindakan = new JTextArea(5, 20);
            txtAreaTindakan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txtAreaTindakan.setLineWrap(true);
            txtAreaTindakan.setWrapStyleWord(true);
            JScrollPane scrollTindakan = new JScrollPane(txtAreaTindakan);
            gbc.gridx = 1;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 0.5;
            gbc.fill = GridBagConstraints.BOTH;
            inputPanel.add(scrollTindakan, gbc);
            gbc.gridwidth = 1;
            gbc.weighty = 0;

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(Color.WHITE);

            JButton btnTambah = new JButton("Tambah");
            btnTambah.setBackground(new Color(46, 204, 113));
            btnTambah.setForeground(Color.WHITE);
            btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnTambah.setFocusPainted(false);
            btnTambah.addActionListener(e -> addRekamMedis());
            buttonPanel.add(btnTambah);

            JButton btnUpdate = new JButton("Update");
            btnUpdate.setBackground(new Color(52, 152, 219));
            btnUpdate.setForeground(Color.WHITE);
            btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnUpdate.setFocusPainted(false);
            btnUpdate.addActionListener(e -> updateRekamMedis());
            buttonPanel.add(btnUpdate);

            JButton btnHapus = new JButton("Hapus");
            btnHapus.setBackground(new Color(231, 76, 60));
            btnHapus.setForeground(Color.WHITE);
            btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnHapus.setFocusPainted(false);
            btnHapus.addActionListener(e -> deleteRekamMedis());
            buttonPanel.add(btnHapus);

            JButton btnClear = new JButton("Clear");
            btnClear.setBackground(new Color(149, 165, 166));
            btnClear.setForeground(Color.WHITE);
            btnClear.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnClear.setFocusPainted(false);
            btnClear.addActionListener(e -> clearFields());
            buttonPanel.add(btnClear);

            JPanel formPanel = new JPanel(new BorderLayout());
            formPanel.add(inputPanel, BorderLayout.CENTER);
            formPanel.add(buttonPanel, BorderLayout.SOUTH);
            formPanel.setBackground(Color.WHITE);

            // Tabel Rekam Medis
            modelRekamMedis = new DefaultTableModel(
                    new String[] { "ID RM", "ID Pasien", "Nama Pasien", "Tanggal", "Diagnosis", "Tindakan" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // make table non-editable
                }
            };
            tableRekamMedis = new JTable(modelRekamMedis);
            tableRekamMedis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tableRekamMedis.setRowHeight(25);
            tableRekamMedis.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            tableRekamMedis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tableRekamMedis.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && tableRekamMedis.getSelectedRow() != -1) {
                    displaySelectedRekamMedis();
                }
            });
            JScrollPane scrollRekamMedis = new JScrollPane(tableRekamMedis);
            scrollRekamMedis.setBorder(BorderFactory.createTitledBorder("Daftar Rekam Medis"));
            scrollRekamMedis.setPreferredSize(new Dimension(850, 300));

            JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.add(formPanel, BorderLayout.NORTH);
            contentPanel.add(scrollRekamMedis, BorderLayout.CENTER);

            add(contentPanel, BorderLayout.CENTER);

            loadRekamMedisData();
        }

        private void findPasien() {
            String idPasien = JOptionPane.showInputDialog(this, "Masukkan ID Pasien:", "Cari Pasien", JOptionPane.QUESTION_MESSAGE);
            if (idPasien != null && !idPasien.trim().isEmpty()) {
                try (Connection conn = koneksi.getKoneksi();
                     PreparedStatement ps = conn.prepareStatement("SELECT nama_pasien FROM pasien WHERE id_pasien = ?")) {
                    ps.setString(1, idPasien);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        txtIdPasien.setText(idPasien);
                        txtNamaPasien.setText(rs.getString("nama_pasien"));
                    } else {
                        JOptionPane.showMessageDialog(this, "ID Pasien tidak ditemukan.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        txtIdPasien.setText("");
                        txtNamaPasien.setText("");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error finding patient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


        private void loadRekamMedisData() {
            modelRekamMedis.setRowCount(0);
            try (Connection conn = koneksi.getKoneksi();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(
                            "SELECT rm.id_rekam, p.id_pasien, p.nama_pasien, rm.tanggal, rm.diagnosis, rm.tindakan " + // Changed 'diagnosa' to 'diagnosis'
                                    "FROM rekam_medis rm JOIN pasien p ON rm.id_pasien = p.id_pasien ORDER BY rm.tanggal DESC, rm.id_rekam DESC")) {

                while (rs.next()) {
                    modelRekamMedis.addRow(new Object[] {
                            rs.getInt("id_rekam"),
                            rs.getString("id_pasien"),
                            rs.getString("nama_pasien"),
                            rs.getDate("tanggal"),
                            rs.getString("diagnosis"), // Changed 'diagnosa' to 'diagnosis'
                            rs.getString("tindakan")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading rekam medis data: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void displaySelectedRekamMedis() {
            int selectedRow = tableRekamMedis.getSelectedRow();
            if (selectedRow != -1) {
                txtIdRekamMedis.setText(modelRekamMedis.getValueAt(selectedRow, 0).toString());
                txtIdPasien.setText(modelRekamMedis.getValueAt(selectedRow, 1).toString());
                txtNamaPasien.setText(modelRekamMedis.getValueAt(selectedRow, 2).toString());
                spinnerTanggal.setValue(modelRekamMedis.getValueAt(selectedRow, 3));
                txtAreaDiagnosis.setText(modelRekamMedis.getValueAt(selectedRow, 4).toString());
                txtAreaTindakan.setText(modelRekamMedis.getValueAt(selectedRow, 5).toString());
            }
        }

        private void addRekamMedis() {
            if (txtIdPasien.getText().isEmpty() || txtAreaDiagnosis.getText().isEmpty() || txtAreaTindakan.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua kolom wajib diisi kecuali ID Rekam Medis (auto-generated).", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = koneksi.getKoneksi();
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO rekam_medis (id_pasien, tanggal, diagnosis, tindakan) VALUES (?, ?, ?, ?)")) { // Changed 'diagnosa' to 'diagnosis'

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String tanggalRekam = sdf.format((Date) spinnerTanggal.getValue());

                ps.setString(1, txtIdPasien.getText());
                ps.setString(2, tanggalRekam);
                ps.setString(3, txtAreaDiagnosis.getText());
                ps.setString(4, txtAreaTindakan.getText());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Rekam medis berhasil ditambahkan.", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadRekamMedisData(); // Refresh table
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan rekam medis.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding rekam medis: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateRekamMedis() {
            if (txtIdRekamMedis.getText().isEmpty() || txtIdPasien.getText().isEmpty() || txtAreaDiagnosis.getText().isEmpty() || txtAreaTindakan.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih rekam medis yang akan diupdate dari tabel dan lengkapi semua kolom.", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = koneksi.getKoneksi();
                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE rekam_medis SET id_pasien = ?, tanggal = ?, diagnosis = ?, tindakan = ? WHERE id_rekam = ?")) { // Changed 'diagnosa' to 'diagnosis'

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String tanggalRekam = sdf.format((Date) spinnerTanggal.getValue());

                ps.setString(1, txtIdPasien.getText());
                ps.setString(2, tanggalRekam);
                ps.setString(3, txtAreaDiagnosis.getText());
                ps.setString(4, txtAreaTindakan.getText());
                ps.setInt(5, Integer.parseInt(txtIdRekamMedis.getText()));

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Rekam medis berhasil diupdate.", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadRekamMedisData(); // Refresh table
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengupdate rekam medis.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating rekam medis: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteRekamMedis() {
            if (txtIdRekamMedis.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih rekam medis yang akan dihapus dari tabel.", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus rekam medis ini?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = koneksi.getKoneksi();
                        PreparedStatement ps = conn.prepareStatement("DELETE FROM rekam_medis WHERE id_rekam = ?")) {

                    ps.setInt(1, Integer.parseInt(txtIdRekamMedis.getText()));

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Rekam medis berhasil dihapus.", "Sukses",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadRekamMedisData(); // Refresh table
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus rekam medis.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting rekam medis: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void clearFields() {
            txtIdRekamMedis.setText("");
            txtIdPasien.setText("");
            txtNamaPasien.setText("");
            txtAreaDiagnosis.setText("");
            txtAreaTindakan.setText("");
            spinnerTanggal.setValue(new Date()); // Reset to current date
            tableRekamMedis.clearSelection();
        }
    }