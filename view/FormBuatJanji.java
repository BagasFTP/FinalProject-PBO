package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import config.koneksi;

public class FormBuatJanji extends JPanel {
    private JTextField tfIdPasien;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JButton btnSimpan;
    private JTable tableJanji;
    private DefaultTableModel tableModel;
    private JPopupMenu suggestionPopup;

    public FormBuatJanji() {
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        add(buatHeader(), BorderLayout.NORTH);
        add(buatFormInput(), BorderLayout.CENTER);
        add(buatTabelJanji(), BorderLayout.SOUTH);

        loadJanji();
    }

    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Buat Janji Temu");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(lblTitle, BorderLayout.WEST);
        return header;
    }

    private JPanel buatFormInput() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Input Data Janji",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.DARK_GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Pasien
        JLabel lId = new JLabel("ID Pasien:");
        tfIdPasien = new JTextField(20);

        // Tanggal Janji
        JLabel lTanggal = new JLabel("Tanggal Janji:");
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        // Jam Janji
        JLabel lJam = new JLabel("Jam Janji:");
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 9);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        timeSpinner.setValue(cal.getTime());

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBackground(new Color(41, 128, 185));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setPreferredSize(new Dimension(120, 35));
        btnSimpan.addActionListener(e -> simpanJanji());

        // Autocomplete
        suggestionPopup = new JPopupMenu();
        tfIdPasien.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(DocumentEvent e) { }
        });

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lId, gbc);
        gbc.gridx = 1; panel.add(tfIdPasien, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lTanggal, gbc);
        gbc.gridx = 1; panel.add(dateSpinner, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lJam, gbc);
        gbc.gridx = 1; panel.add(timeSpinner, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; panel.add(btnSimpan, gbc);

        return panel;
    }

    private JScrollPane buatTabelJanji() {
        tableModel = new DefaultTableModel(new String[] { "ID Pasien", "Nama Pasien", "Tanggal Janji", "Jam Janji", "Status" }, 0) { //
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //
            }
        };
        
        tableJanji = new JTable(tableModel);
        tableJanji.setRowHeight(25); //
        tableJanji.setFont(new Font("Segoe UI", Font.PLAIN, 12)); //
        tableJanji.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); //

        tableJanji.getColumnModel().getColumn(0).setPreferredWidth(80); //
        tableJanji.getColumnModel().getColumn(1).setPreferredWidth(150); //
        tableJanji.getColumnModel().getColumn(2).setPreferredWidth(100); //
        tableJanji.getColumnModel().getColumn(3).setPreferredWidth(80); //
        tableJanji.getColumnModel().getColumn(4).setPreferredWidth(100); //

        JScrollPane scrollPane = new JScrollPane(tableJanji);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Janji Temu")); //
        scrollPane.setPreferredSize(new Dimension(600, 250)); //
        return scrollPane;
    }

    private void simpanJanji() {
        String idPasien = tfIdPasien.getText().trim();
        Date selectedDate = (Date) dateSpinner.getValue();
        Date selectedTime = (Date) timeSpinner.getValue();

        if (idPasien.isEmpty() || selectedDate == null || selectedTime == null) { //
            JOptionPane.showMessageDialog(this, "Isi semua field: ID Pasien, tanggal janji, dan jam janji.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!cekPasienAda(idPasien)) { //
            JOptionPane.showMessageDialog(this, "ID Pasien tidak ditemukan di database!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tanggalJanji = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate); //
        String jamJanji = new SimpleDateFormat("HH:mm:ss").format(selectedTime); //

        try {
            simpanJanjiTemu(idPasien, tanggalJanji, jamJanji); //
            JOptionPane.showMessageDialog(this, "Janji temu berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            resetForm(); // Memanggil method resetForm
            loadJanji(); //
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan janji: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void simpanJanjiTemu(String idPasien, String tanggalJanji, String jamJanji) throws SQLException {
        Connection conn = null;
        PreparedStatement pstJanji = null;
        PreparedStatement pstJanjiTemu = null;
        
        try {
            conn = koneksi.getKoneksi(); //
            conn.setAutoCommit(false); //
            
            String sqlCek = "SELECT COUNT(*) FROM janji_temu WHERE id_pasien = ? AND tanggal_janji = ? AND status = 'scheduled'"; //
            try (PreparedStatement pstCek = conn.prepareStatement(sqlCek)) {
                pstCek.setString(1, idPasien);
                pstCek.setString(2, tanggalJanji);
                ResultSet rs = pstCek.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) { //
                    throw new SQLException("Pasien sudah memiliki janji di tanggal tersebut"); //
                }
            }
            
            String sqlJanji = "INSERT INTO janji (id_pasien, tanggal_janji, status) VALUES (?, ?, 'active')"; //
            pstJanji = conn.prepareStatement(sqlJanji);
            pstJanji.setString(1, idPasien);
            pstJanji.setString(2, tanggalJanji);
            pstJanji.executeUpdate(); //
            
            String sqlJanjiTemu = "INSERT INTO janji_temu (id_pasien, tanggal_janji, waktu_janji, status, keterangan) VALUES (?, ?, ?, 'scheduled', 'Janji temu reguler')"; //
            pstJanjiTemu = conn.prepareStatement(sqlJanjiTemu);
            pstJanjiTemu.setString(1, idPasien);
            pstJanjiTemu.setString(2, tanggalJanji);
            pstJanjiTemu.setString(3, jamJanji);
            pstJanjiTemu.executeUpdate(); //
            
            conn.commit(); //
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); //
            throw e; //
        } finally {
            if (pstJanjiTemu != null) pstJanjiTemu.close();
            if (pstJanji != null) pstJanji.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private void resetForm() {
        tfIdPasien.setText(""); //
        dateSpinner.setValue(new Date()); //
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 9); //
        cal.set(java.util.Calendar.MINUTE, 0); //
        cal.set(java.util.Calendar.SECOND, 0); //
        timeSpinner.setValue(cal.getTime()); //
    }

    private void loadJanji() {
        tableModel.setRowCount(0); //
        try (Connection conn = koneksi.getKoneksi()) {
            String sql = """
                SELECT jt.id_pasien, p.nama_pasien, jt.tanggal_janji, 
                       COALESCE(jt.waktu_janji, '00:00:00') as waktu_janji, jt.status
                FROM janji_temu jt 
                JOIN pasien p ON jt.id_pasien = p.id_pasien 
                ORDER BY jt.tanggal_janji DESC, jt.waktu_janji ASC
                """; //
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String waktuJanji = rs.getString("waktu_janji");
                if (waktuJanji != null && waktuJanji.length() > 5) { //
                    waktuJanji = waktuJanji.substring(0, 5); //
                }
                
                Object[] row = {
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getString("tanggal_janji"),
                        waktuJanji != null ? waktuJanji : "-",
                        rs.getString("status")
                }; //
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data janji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean cekPasienAda(String idPasien) {
        try (Connection conn = koneksi.getKoneksi()) {
            PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM pasien WHERE id_pasien = ?"); //
            pst.setString(1, idPasien);
            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0; //
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showSuggestions() {
        SwingUtilities.invokeLater(() -> {
            suggestionPopup.setVisible(false);
            suggestionPopup.removeAll();

            String text = tfIdPasien.getText().trim();
            if (text.isEmpty()) return;

            ArrayList<String> suggestions = new ArrayList<>();
            try (Connection conn = koneksi.getKoneksi()) {
                PreparedStatement pst = conn.prepareStatement(
                        "SELECT id_pasien, nama_pasien FROM pasien WHERE id_pasien LIKE ? OR nama_pasien LIKE ? LIMIT 10"); //
                pst.setString(1, "%" + text + "%");
                pst.setString(2, "%" + text + "%");
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    suggestions.add(rs.getString("id_pasien") + " - " + rs.getString("nama_pasien")); //
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!suggestions.isEmpty()) {
                for (String s : suggestions) {
                    JMenuItem item = new JMenuItem(s);
                    item.addActionListener(e -> {
                        String[] parts = s.split(" - "); //
                        tfIdPasien.setText(parts[0]); //
                        suggestionPopup.setVisible(false);
                    });
                    suggestionPopup.add(item);
                }
                suggestionPopup.show(tfIdPasien, 0, tfIdPasien.getHeight()); //
            }
        });
    }
}