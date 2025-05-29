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

public class FormBuatJanji extends JFrame {
    private JTextField tfIdPasien;
    private JSpinner dateSpinner;
    private JButton btnSimpan;
    private JTable tableJanji;
    private DefaultTableModel tableModel;
    private JPopupMenu suggestionPopup;

    public FormBuatJanji() {
        setTitle("Form Buat Janji Temu");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        add(buatHeader(), BorderLayout.NORTH);
        add(buatFormInput(), BorderLayout.CENTER);
        add(buatTabelJanji(), BorderLayout.SOUTH);

        loadJanji();
        setVisible(true);
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

        JLabel lId = new JLabel("ID Pasien:");
        tfIdPasien = new JTextField(20);

        JLabel lTanggal = new JLabel("Tanggal Janji:");
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

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
            public void insertUpdate(DocumentEvent e) {
                showSuggestions();
            }

            public void removeUpdate(DocumentEvent e) {
                showSuggestions();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lId, gbc);
        gbc.gridx = 1;
        panel.add(tfIdPasien, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lTanggal, gbc);
        gbc.gridx = 1;
        panel.add(dateSpinner, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnSimpan, gbc);

        return panel;
    }

    private JScrollPane buatTabelJanji() {
        tableModel = new DefaultTableModel(new String[] { "ID Pasien", "Nama Pasien", "Tanggal Janji" }, 0);
        tableJanji = new JTable(tableModel);
        tableJanji.setRowHeight(22);
        tableJanji.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableJanji.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tableJanji);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Janji"));
        scrollPane.setPreferredSize(new Dimension(500, 200));
        return scrollPane;
    }

    private void simpanJanji() {
        String idPasien = tfIdPasien.getText().trim();
        Date selectedDate = (Date) dateSpinner.getValue();

        if (idPasien.isEmpty() || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Isi ID Pasien dan tanggal janji.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!cekPasienAda(idPasien)) {
            JOptionPane.showMessageDialog(this, "ID Pasien tidak ditemukan di database!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tanggalJanji = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        try {
            JanjiController.simpanJanji(idPasien, tanggalJanji);
            JOptionPane.showMessageDialog(this, "Janji berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            tfIdPasien.setText("");
            dateSpinner.setValue(new Date());
            loadJanji();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan janji: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadJanji() {
        tableModel.setRowCount(0);
        try (Connection conn = koneksi.getKoneksi()) {
            String sql = "SELECT j.id_pasien, p.nama_pasien, j.tanggal_janji FROM janji j JOIN pasien p ON j.id_pasien = p.id_pasien ORDER BY tanggal_janji DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getString("tanggal_janji")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data janji: " + e.getMessage());
        }
    }

    private boolean cekPasienAda(String idPasien) {
        try (Connection conn = koneksi.getKoneksi()) {
            PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM pasien WHERE id_pasien = ?");
            pst.setString(1, idPasien);
            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
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
            if (text.isEmpty())
                return;

            ArrayList<String> suggestions = new ArrayList<>();
            try (Connection conn = koneksi.getKoneksi()) {
                PreparedStatement pst = conn.prepareStatement(
                        "SELECT id_pasien, nama_pasien FROM pasien WHERE id_pasien LIKE ? OR nama_pasien LIKE ?");
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
        });
    }
}
