package controller;

import model.AdminLogin;

public class LoginController {

    public boolean login(String username, String password) {
        // Memanggil model AdminLogin untuk memverifikasi login
        return AdminLogin.verifyAdminLogin(username, password);
    }
}
