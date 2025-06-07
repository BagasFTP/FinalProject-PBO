package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class koneksi {
    
    public static Connection getKoneksi() {
        Connection conn = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/klinik", 
                "root", 
                ""
            );
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return conn;
    }
    
    public static void main(String[] args) {
        Connection conn = getKoneksi();
        if (conn != null) {
            System.out.println("Koneksi Berhasil");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Koneksi Gagal");
        }
    }
}