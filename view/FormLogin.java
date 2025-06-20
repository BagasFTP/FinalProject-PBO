package view;

import controller.LoginController;

import javax.swing.*;
import java.awt.*;

public class FormLogin extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;

    public FormLogin() {
        setTitle("Login Admin");
        setSize(530, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false); // Bisa diganti ke true jika ingin tanpa frame default

        // Panel utama (dua kolom)
        JPanel panelUtama = new JPanel(new GridLayout(1, 2));

        // Panel kiri (Welcome Back!)
        JPanel panelKiri = new JPanel();
        panelKiri.setBackground(new Color(52, 152, 219));
        panelKiri.setLayout(new GridBagLayout());
        JLabel lblWelcome = new JLabel("Welcome Back!");
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        panelKiri.add(lblWelcome);

        // Panel kanan (form login)
        JPanel panelKanan = new JPanel();
        panelKanan.setLayout(new BoxLayout(panelKanan, BoxLayout.Y_AXIS));
        panelKanan.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panelKanan.setBackground(Color.WHITE);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfUsername = new JTextField(15);
        tfUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        pfPassword = new JPasswordField(15);
        pfPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.addActionListener(e -> pfPassword.setEchoChar(showPass.isSelected() ? (char) 0 : '•'));

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(100, 35));

        panelKanan.add(lblUsername);
        panelKanan.add(Box.createVerticalStrut(5));
        panelKanan.add(tfUsername);
        panelKanan.add(Box.createVerticalStrut(15));
        panelKanan.add(lblPassword);
        panelKanan.add(Box.createVerticalStrut(5));
        panelKanan.add(pfPassword);
        panelKanan.add(Box.createVerticalStrut(5));
        panelKanan.add(showPass); // ✅ Tambahkan checkbox di sini
        panelKanan.add(Box.createVerticalStrut(20));
        panelKanan.add(btnLogin);

        // Tambahkan panel ke panel utama
        panelUtama.add(panelKiri);
        panelUtama.add(panelKanan);
        add(panelUtama);

        // Aksi tombol login
        btnLogin.addActionListener(e -> {
            String username = tfUsername.getText().trim();
            String password = new String(pfPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong.", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LoginController controller = new LoginController();
            if (controller.login(username, password)) {
                JOptionPane.showMessageDialog(this, "Login Sukses!");
                dispose();
                new MainApp();
            } else {
                JOptionPane.showMessageDialog(this, "Login Gagal! Username atau Password salah.", "Kesalahan",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
