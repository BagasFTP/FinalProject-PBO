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
    private JTextField tfId, tfNama, tfAlamat, tfHp;
    private JSpinner spinnerTgl;
    private JTable tablePasien;
    private DefaultTableModel tableModel;

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
        JPanel konten = new JPanel(new BorderLayout(15, 15));
        konten.setBackground(Color.WHITE);
        konten.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        konten.add(buatFormInput(), BorderLayout.NORTH);
        konten.add(buatTabelPasien(), BorderLayout.CENTER);

        return konten;
    }

    private JPanel buatFormInput() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Input Data Pasien",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.DARK_GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfId = new JTextField(15);
        tfNama = new JTextField(15);
        tfAlamat = new JTextField(15);
        tfHp = new JTextField(15);
        spinnerTgl = new JSpinner(new SpinnerDateModel());
        spinnerTgl.setEditor(new JSpinner.DateEditor(spinnerTgl, "yyyy-MM-dd"));

        String[] labelList = { "ID Pasien", "Nama", "Tanggal Lahir", "Alamat", "No. HP" };
        Component[] fieldList = { tfId, tfNama, spinnerTgl, tfAlamat, tfHp };

        for (int i = 0; i < labelList.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labelList[i]), gbc);

            gbc.gridx = 1;
            panel.add(fieldList[i], gbc);
        }

        JButton btnSimpan = new JButton("Simpan");
        JButton btnReset = new JButton("Reset");

        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.WHITE);
        btnReset.setBackground(new Color(189, 195, 199));
        btnReset.setForeground(Color.BLACK);

        btnSimpan.setFocusPainted(false);
        btnReset.setFocusPainted(false);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnSimpan.addActionListener(e -> simpanPasien());
        btnReset.addActionListener(e -> resetForm());

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelButton.setBackground(new Color(245, 245, 245));
        panelButton.add(btnSimpan);
        panelButton.add(btnReset);

        gbc.gridx = 0;
        gbc.gridy = labelList.length;
        gbc.gridwidth = 2;
        panel.add(panelButton, gbc);

        return panel;
    }

    private JScrollPane buatTabelPasien() {
        tableModel = new DefaultTableModel(new String[] {
                "ID", "Nama", "Tanggal Lahir", "Alamat", "No. HP"
        }, 0);
        tablePasien = new JTable(tableModel);
        tablePasien.setRowHeight(24);
        tablePasien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablePasien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablePasien.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(tablePasien);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Data Pasien",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.DARK_GRAY));
        return scrollPane;
    }

    private void simpanPasien() {
        try {
            String id = tfId.getText().trim();
            String nama = tfNama.getText().trim();
            Date tgl = (Date) spinnerTgl.getValue();
            String alamat = tfAlamat.getText().trim();
            String hp = tfHp.getText().trim();

            if (id.isEmpty() || nama.isEmpty() || alamat.isEmpty() || hp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tglFormatted = new SimpleDateFormat("yyyy-MM-dd").format(tgl);
            Connection conn = koneksi.getKoneksi();
            String sql = "INSERT INTO pasien (id_pasien, nama_pasien, tgl_lahir, alamat, noHP) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, nama);
            pst.setString(3, tglFormatted);
            pst.setString(4, alamat);
            pst.setString(5, hp);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataPasien();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetForm() {
        tfId.setText("");
        tfNama.setText("");
        tfAlamat.setText("");
        tfHp.setText("");
        spinnerTgl.setValue(new Date());
    }

    private void loadDataPasien() {
        tableModel.setRowCount(0);
        try {
            Connection conn = koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pasien");

            while (rs.next()) {
                Object[] row = {
                        rs.getString("id_pasien"),
                        rs.getString("nama_pasien"),
                        rs.getString("tgl_lahir"),
                        rs.getString("alamat"),
                        rs.getString("noHP")
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
