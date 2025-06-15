package view;

import controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormLogin extends JFrame {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;

    public FormLogin() {
        setTitle("Login Admin");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Label dan input fields
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");

        tfUsername = new JTextField();
        pfPassword = new JPasswordField();
        btnLogin = new JButton("Login");

        add(lblUsername);
        add(tfUsername);
        add(lblPassword);
        add(pfPassword);
        add(btnLogin);

        // Aksi ketika tombol login diklik
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = tfUsername.getText();
                String password = new String(pfPassword.getPassword());

                LoginController controller = new LoginController();
                if (controller.login(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login Sukses! Masuk ke Panel Admin.");
                    // Hide the login form and show MainApp
                    setVisible(false);
                    new MainApp(); // Show MainApp when login is successful
                } else {
                    JOptionPane.showMessageDialog(null, "Login Gagal! Username atau Password Salah.");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormLogin loginForm = new FormLogin();
            loginForm.setVisible(true);
        });
    }
}
