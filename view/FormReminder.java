package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import config.koneksi;

public class FormReminder extends JPanel { // Changed from JFrame to JPanel

    private JTextField tfSearchIdPasien; // New: Search field for Patient ID
    private JButton btnSearch; // New: Search button
    private JTable tableReminders;
    private DefaultTableModel modelReminders;

    public FormReminder() {
        setLayout(new BorderLayout(10, 10)); // JPanel can still have its own layout
        setBackground(Color.WHITE); // Set background for the panel
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Add some padding

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("🔔 Janji Temu & Pengingat");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Cari Janji Temu Berdasarkan ID Pasien"));

        JLabel lblSearch = new JLabel("ID Pasien:");
        tfSearchIdPasien = new JTextField(15);
        btnSearch = new JButton("Cari");
        btnSearch.setBackground(new Color(46, 204, 113));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> searchJanjiTemuByIdPasien());

        JButton btnShowAll = new JButton("Tampilkan Semua"); // Button to show all appointments
        btnShowAll.setBackground(new Color(52, 152, 219));
        btnShowAll.setForeground(Color.WHITE);
        btnShowAll.setFocusPainted(false);
        btnShowAll.addActionListener(e -> loadAllReminders());

        searchPanel.add(lblSearch);
        searchPanel.add(tfSearchIdPasien);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll); // Add the "Show All" button

        add(searchPanel, BorderLayout.CENTER); // Add search panel to the top-center

        // Table setup
        modelReminders = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Janji", "ID Pasien", "Nama Pasien", "Tanggal Janji", "Waktu Janji", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells uneditable
            }
        };
        tableReminders = new JTable(modelReminders);
        tableReminders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableReminders.getTableHeader().setReorderingAllowed(false);
        tableReminders.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableReminders.setRowHeight(25);
        tableReminders.getTableHeader().setBackground(new Color(52, 152, 219));
        tableReminders.getTableHeader().setForeground(Color.WHITE);
        tableReminders.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(tableReminders);
        // Changed to BorderLayout.SOUTH to place table below search panel
        add(scrollPane, BorderLayout.SOUTH);


        // Load all data when the panel is initialized
        loadAllReminders();

        // No setVisible(true) for JPanel, as it's added to another container
    }

    private void loadAllReminders() {
        modelReminders.setRowCount(0); // Clear existing data

        String query = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status " +
                       "FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien " +
                       "ORDER BY jt.tanggal_janji ASC, jt.waktu_janji ASC";

        try (Connection conn = koneksi.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            populateTableFromResultSet(rs);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading all janji temu data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void searchJanjiTemuByIdPasien() {
        String idPasien = tfSearchIdPasien.getText().trim();
        if (idPasien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Pasien tidak boleh kosong untuk pencarian!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            loadAllReminders(); // Optionally show all if search field is empty
            return;
        }

        modelReminders.setRowCount(0); // Clear existing data

        String query = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status " +
                       "FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien " +
                       "WHERE jt.id_pasien = ? " + // Filter by id_pasien
                       "ORDER BY jt.tanggal_janji ASC, jt.waktu_janji ASC";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, idPasien);
            ResultSet rs = ps.executeQuery();

            populateTableFromResultSet(rs);

            if (modelReminders.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada janji temu ditemukan untuk ID Pasien " + idPasien, "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching janji temu data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Helper method to populate the table from a ResultSet
    private void populateTableFromResultSet(ResultSet rs) throws SQLException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        while (rs.next()) {
            Object[] row = new Object[6];
            row[0] = rs.getInt("id_janji_temu");
            row[1] = rs.getString("id_pasien");
            row[2] = rs.getString("nama_pasien");
            row[3] = dateFormatter.format(rs.getDate("tanggal_janji"));
            row[4] = timeFormatter.format(rs.getTime("waktu_janji"));
            row[5] = rs.getString("status");
            modelReminders.addRow(row);
        }
    }

    // Removed main method as it's no longer a standalone JFrame
}