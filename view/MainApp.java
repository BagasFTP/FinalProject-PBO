package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

import view.FormRegistrasi;
import view.FormAntrian;
import view.FormBuatJanji;
import view.FormEditJanji;
import view.FormReminder;
import view.FormStatistik;
import view.FormExportCSV;
import view.FormCekTanggal;
import view.FormRekamMedis;

public class MainApp extends JFrame {
    private JButton[] tombolFitur = new JButton[9];

    public MainApp() {
        setTitle("Menu Utama Klinik");
        setSize(700, 400);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel panelKiri = new JPanel();
        panelKiri.setLayout(new GridLayout(9, 1, 10, 10));
        panelKiri.setBackground(new Color(70, 130, 180));

        String[] fitur = {
                "Registrasi Pasien", "Sistem Antrian", "Buat Janji",
                "Edit/Hapus Janji", "Reminder Temu", "Statistik Kunjungan",
                "Export Laporan", "Cek Berdasarkan Tanggal", "Rekam Medis"
        };

        Font font = new Font("Arial", Font.PLAIN, 14);

        for (int i = 0; i < fitur.length; i++) {
            JButton btn = new JButton(fitur[i]);
            btn.setFont(font);
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            btn.setFocusPainted(false);
            final int index = i;

            btn.addActionListener(e -> bukaForm(index));
            tombolFitur[i] = btn;

            panelKiri.add(btn);
        }

        add(panelKiri, BorderLayout.WEST);

        JPanel panelTengah = new JPanel();
        panelTengah.setBackground(new Color(245, 245, 245));
        add(panelTengah, BorderLayout.CENTER);

        // Handle image loading dengan multiple fallback options
        loadBackgroundImage(panelTengah);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loadBackgroundImage(JPanel panelTengah) {
        ImageIcon imageIcon = null;
        boolean imageLoaded = false;

        // Debug info
        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        // Attempt 1: Direct relative path from working directory
        try {
            File imageFile = new File("img/background.jpg");
            System.out.println("Checking path: " + imageFile.getAbsolutePath());
            System.out.println("File exists: " + imageFile.exists());
            
            if (imageFile.exists()) {
                imageIcon = new ImageIcon("img/background.jpg");
                if (imageIcon.getIconWidth() > 0) {
                    imageLoaded = true;
                    System.out.println("Image loaded with relative path: img/background.jpg");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to load with relative path: " + e.getMessage());
        }

        // Attempt 2: Try with absolute path construction
        if (!imageLoaded) {
            try {
                String absolutePath = System.getProperty("user.dir") + File.separator + "img" + File.separator + "background.png";
                File imageFile = new File(absolutePath);
                System.out.println("Trying absolute path: " + absolutePath);
                
                if (imageFile.exists()) {
                    imageIcon = new ImageIcon(absolutePath);
                    if (imageIcon.getIconWidth() > 0) {
                        imageLoaded = true;
                        System.out.println("Image loaded with absolute path");
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to load with absolute path: " + e.getMessage());
            }
        }

        // Attempt 3: Try different relative paths
        if (!imageLoaded) {
            String[] possiblePaths = {
                "./img/background.jpg",
                "../img/background.jpg",
                "../../img/background.jpg",
                "src/img/background.jpg",
                "resources/img/background.jpg"
            };

            for (String path : possiblePaths) {
                try {
                    File imageFile = new File(path);
                    if (imageFile.exists()) {
                        imageIcon = new ImageIcon(path);
                        if (imageIcon.getIconWidth() > 0) {
                            imageLoaded = true;
                            System.out.println("Image loaded with path: " + path);
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Continue to next path
                }
            }
        }

        // Attempt 4: Try loading from resources
        if (!imageLoaded) {
            try {
                URL imageURL = getClass().getResource("/img/background.jpg");
                if (imageURL != null) {
                    imageIcon = new ImageIcon(imageURL);
                    if (imageIcon.getIconWidth() > 0) {
                        imageLoaded = true;
                        System.out.println("Image loaded from resources: /img/background.jpg");
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to load from resources: " + e.getMessage());
            }
        }

        // Attempt 5: Try loading from classpath
        if (!imageLoaded) {
            try {
                URL imageURL = getClass().getClassLoader().getResource("img/background.jpg");
                if (imageURL != null) {
                    imageIcon = new ImageIcon(imageURL);
                    if (imageIcon.getIconWidth() > 0) {
                        imageLoaded = true;
                        System.out.println("Image loaded from classpath: img/background.jpg");
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to load from classpath: " + e.getMessage());
            }
        }

        // Display result
        if (imageLoaded && imageIcon != null) {
            JLabel labelImage = new JLabel(imageIcon);
            panelTengah.add(labelImage);
            System.out.println("Background image successfully displayed!");
        } else {
            // Jika semua cara gagal, tampilkan text
            JLabel labelText = new JLabel("SISTEM INFORMASI KLINIK", JLabel.CENTER);
            labelText.setFont(new Font("Arial", Font.BOLD, 24));
            labelText.setForeground(new Color(70, 130, 180));
            panelTengah.add(labelText);
            System.out.println("Image not found, displaying text instead");
        }
    }

    private void bukaForm(int index) {
        try {
            switch (index) {
                case 0 -> {
                    // FormRegistrasi memerlukan parameter Runnable
                    new FormRegistrasi(() -> {
                        // Callback setelah registrasi berhasil
                        // Bisa ditambahkan logic tambahan jika diperlukan
                        System.out.println("Registrasi pasien berhasil!");
                    });
                }
                case 1 -> {
                    try {
                        new FormAntrian();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Antrian belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 2 -> {
                    try {
                        new FormBuatJanji();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Buat Janji belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 3 -> {
                    try {
                        new FormEditJanji();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Edit Janji belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 4 -> {
                    try {
                        new FormReminder();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Reminder belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 5 -> {
                    try {
                        new FormStatistik();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Statistik belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 6 -> {
                    try {
                        new FormExportCSV();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Export CSV belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 7 -> {
                    try {
                        new FormCekTanggal();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Cek Tanggal belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case 8 -> {
                    try {
                        new FormRekamMedis();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Form Rekam Medis belum tersedia atau ada error: " + ex.getMessage(), 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                default -> JOptionPane.showMessageDialog(this, "Fitur belum tersedia!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error membuka form: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}