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
import controller.JanjiController;

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

    // Header panel
    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel title = new JLabel("  ➕ Buat Janji Temu Baru");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        return header;
    }

    // Input form
    private JPanel buatFormInput() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Pasien
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID Pasien:"), gbc);
        gbc.gridx = 1;
        tfIdPasien = new JTextField(20);
        formPanel.add(tfIdPasien, gbc);

        // Auto-suggestion for ID Pasien
        setupIdPasienAutoSuggest();

        // Tanggal Janji
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tanggal Janji:"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner, gbc);

        // Waktu Janji
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Waktu Janji:"), gbc);
        gbc.gridx = 1;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        formPanel.add(timeSpinner, gbc);

        // Tombol Simpan
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        btnSimpan = new JButton("Simpan Janji");
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpanJanjiBaru());
        formPanel.add(btnSimpan, gbc);

        return formPanel;
    }

    // Table for appointments
    private JScrollPane buatTabelJanji() {
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Janji", "ID Pasien", "Nama Pasien", "Tanggal Janji", "Waktu Janji", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableJanji = new JTable(tableModel);
        tableJanji.getTableHeader().setReorderingAllowed(false);
        tableJanji.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableJanji.setRowHeight(25);
        tableJanji.getTableHeader().setBackground(new Color(52, 152, 219));
        tableJanji.getTableHeader().setForeground(Color.WHITE);
        tableJanji.setFillsViewportHeight(true);
        return new JScrollPane(tableJanji);
    }

    // Load appointment data into the table
    private void loadJanji() {
        tableModel.setRowCount(0);
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
                Object idJanji = rs.getObject("id_janji_temu");
                String idPasien = rs.getString("id_pasien");
                String namaPasien = rs.getString("nama_pasien");
                Date tanggalJanji = rs.getDate("tanggal_janji");
                Time waktuJanji = rs.getTime("waktu_janji");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{
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

    // Save new appointment
    private void simpanJanjiBaru() {
        String idPasien = tfIdPasien.getText().trim();
        Date tanggalJanjiDate = (Date) dateSpinner.getValue();
        Date waktuJanjiDate = (Date) timeSpinner.getValue();

        if (idPasien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Pasien tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String tanggalJanjiStr = dateFormat.format(tanggalJanjiDate);
        String waktuJanjiStr = timeFormat.format(waktuJanjiDate);

        try {
            String namaPasien = JanjiController.getNamaPasien(idPasien);
            if (namaPasien == null) {
                JOptionPane.showMessageDialog(this, "ID Pasien tidak ditemukan.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JanjiController.simpanJanji(idPasien, tanggalJanjiStr, waktuJanjiStr);
            JOptionPane.showMessageDialog(this, "Janji temu berhasil disimpan untuk pasien " + namaPasien + ".", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadJanji();
            tfIdPasien.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan janji temu: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Setup auto-suggest for ID Pasien
    private void setupIdPasienAutoSuggest() {
        suggestionPopup = new JPopupMenu();
        tfIdPasien.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                showSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                showSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not used for plain text documents
            }
        });

        tfIdPasien.addActionListener(e -> {
            if (suggestionPopup.isVisible()) {
                suggestionPopup.setVisible(false);
            }
        });
    }

    // Show suggestions for ID Pasien
    private void showSuggestions() {
        suggestionPopup.setVisible(false);
        suggestionPopup.removeAll();

        String text = tfIdPasien.getText().trim();
        if (text.isEmpty())
            return;

        ArrayList<String> suggestions = new ArrayList<>();
        try (Connection conn = koneksi.getKoneksi()) {
            PreparedStatement pst = conn.prepareStatement(
                    "SELECT id_pasien, nama_pasien FROM pasien WHERE id_pasien LIKE ? OR nama_pasien LIKE ? LIMIT 10");
            pst.setString(1, "%" + text + "%");
            pst.setString(2, "%" + text + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                suggestions.add(rs.getString("id_pasien") + " - " + rs.getString("nama_pasien"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!suggestions.isEmpty()) {
            for (String s : suggestions) {
                JMenuItem item = new JMenuItem(s);
                item.addActionListener(e -> {
                    String[] parts = s.split(" - ");
                    tfIdPasien.setText(parts[0]);
                    suggestionPopup.setVisible(false);
                });
                suggestionPopup.add(item);
            }
            suggestionPopup.show(tfIdPasien, 0, tfIdPasien.getHeight());
        }
    }
}
