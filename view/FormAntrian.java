package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import controller.AntrianController;
import controller.PasienController;

public class FormAntrian extends JFrame {
    private JTextField tfIdPasien;
    private JButton btnTambahAntrian, btnRefresh;
    private JTextArea taAntrian;
    private JScrollPane scrollPane;

    public FormAntrian() {
        setTitle("Sistem Antrian Pasien");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        inisialisasiForm();
        refreshAntrian();
        setVisible(true);
    }

    private void inisialisasiForm() {
        setLayout(new BorderLayout(10, 10));

        // Panel Input
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInput.setBorder(BorderFactory.createTitledBorder("Tambah Antrian"));
        
        JLabel lblId = new JLabel("ID Pasien:");
        tfIdPasien = new JTextField(15);
        btnTambahAntrian = new JButton("Tambah ke Antrian");
        btnRefresh = new JButton("Refresh");

        panelInput.add(lblId);
        panelInput.add(tfIdPasien);
        panelInput.add(btnTambahAntrian);
        panelInput.add(btnRefresh);

        // Panel Daftar Antrian
        JPanel panelAntrian = new JPanel(new BorderLayout());
        panelAntrian.setBorder(BorderFactory.createTitledBorder("Daftar Antrian"));
        
        taAntrian = new JTextArea(15, 40);
        taAntrian.setEditable(false);
        taAntrian.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(taAntrian);
        
        panelAntrian.add(scrollPane, BorderLayout.CENTER);

        // Panel Info
        JPanel panelInfo = new JPanel();
        JLabel lblInfo = new JLabel("Masukkan ID Pasien untuk menambahkan ke antrian");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        panelInfo.add(lblInfo);

        // Tambahkan ke frame
        add(panelInput, BorderLayout.NORTH);
        add(panelAntrian, BorderLayout.CENTER);
        add(panelInfo, BorderLayout.SOUTH);

        // Event listeners
        btnTambahAntrian.addActionListener(e -> tambahAntrian());
        btnRefresh.addActionListener(e -> refreshAntrian());
        
        // Enter key pada text field
        tfIdPasien.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tambahAntrian();
                }
            }
        });
    }

    private void tambahAntrian() {
        String idPasien = tfIdPasien.getText().trim();
        
        if (idPasien.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "ID Pasien tidak boleh kosong!", 
                "Validasi", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Cek apakah ID pasien exist
            if (!PasienController.cekIdPasienExist(idPasien)) {
                int pilihan = JOptionPane.showConfirmDialog(this,
                    "ID Pasien '" + idPasien + "' tidak ditemukan dalam database.\n" +
                    "Apakah Anda yakin ingin menambahkan ke antrian?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (pilihan != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Tambah ke antrian
            AntrianController.tambahAntrian(idPasien);
            
            JOptionPane.showMessageDialog(this, 
                "Pasien dengan ID '" + idPasien + "' berhasil ditambahkan ke antrian!", 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            
            tfIdPasien.setText("");
            refreshAntrian();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error saat menambah antrian:\n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void refreshAntrian() {
        try {
            List<String> daftarAntrian = AntrianController.getDaftarAntrian();
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== DAFTAR ANTRIAN PASIEN ===\n\n");
            
            if (daftarAntrian.isEmpty()) {
                sb.append("Tidak ada pasien dalam antrian.\n");
            } else {
                sb.append(String.format("%-5s %-15s\n", "No.", "ID Pasien"));
                sb.append("-------------------------\n");
                
                for (int i = 0; i < daftarAntrian.size(); i++) {
                    sb.append(String.format("%-5d %-15s\n", 
                        (i + 1), daftarAntrian.get(i)));
                }
                
                sb.append("\nTotal pasien dalam antrian: ").append(daftarAntrian.size());
            }
            
            taAntrian.setText(sb.toString());
            taAntrian.setCaretPosition(0); // Scroll ke atas
            
        } catch (IOException ex) {
            taAntrian.setText("Error saat memuat daftar antrian:\n" + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error saat memuat antrian:\n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}