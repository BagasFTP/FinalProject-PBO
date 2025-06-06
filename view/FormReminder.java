package view;

import config.koneksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormReminder extends JPanel {
    private JTable tableReminder;
    private DefaultTableModel modelReminder;
    private JButton btnRefresh;
    private JSpinner spinnerTanggal;

    public FormReminder() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblJudul = new JLabel("Pengingat Janji Temu");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(Color.WHITE);

        JLabel lblTanggal = new JLabel("Tanggal:");
        lblTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        controlPanel.add(lblTanggal);

        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd"));
        spinnerTanggal.setPreferredSize(new Dimension(150, 30));
        controlPanel.add(spinnerTanggal);

        btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(46, 204, 113));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadReminderData());
        controlPanel.add(btnRefresh);

        add(controlPanel, BorderLayout.SOUTH); // Placed at the bottom for easy access

        // Table Janji Temu
        modelReminder = new DefaultTableModel(
                new String[] { "ID Janji", "ID Pasien", "Nama Pasien", "Tanggal", "Waktu", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table non-editable
            }
        };
        tableReminder = new JTable(modelReminder);
        tableReminder.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableReminder.setRowHeight(25);
        tableReminder.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableReminder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollReminder = new JScrollPane(tableReminder);
        scrollReminder.setBorder(BorderFactory.createTitledBorder("Janji Temu Mendatang"));
        add(scrollReminder, BorderLayout.CENTER);

        loadReminderData(); // Load data initially
    }

    private void loadReminderData() {
        modelReminder.setRowCount(0);
        Date selectedDate = (Date) spinnerTanggal.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(selectedDate);

        try (Connection conn = koneksi.getKoneksi();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT jt.id_janji_temu, p.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status " +
                                "FROM janji_temu jt JOIN pasien p ON jt.id_pasien = p.id_pasien " +
                                "WHERE jt.tanggal_janji >= ? AND jt.status = 'Dijadwalkan' ORDER BY jt.tanggal_janji ASC, jt.waktu_janji ASC")) {

            ps.setString(1, dateString);
            ResultSet rs = ps.executeQuery();

            boolean foundData = false;
            while (rs.next()) {
                foundData = true;
                modelReminder.addRow(new Object[] {
                        rs.getInt("id_janji_temu"),
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getDate("tanggal_janji"),
                        rs.getTime("waktu_janji"),
                        rs.getString("status")
                });
            }

            if (!foundData) {
                JOptionPane.showMessageDialog(this, "Tidak ada janji temu yang dijadwalkan dari tanggal " + dateString + " dan seterusnya.",
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading reminder data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Removed main method
}