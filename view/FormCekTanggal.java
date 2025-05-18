package view;
import javax.swing.*;
/**
 *
 * @author ASUS
 */
public class FormCekTanggal extends JFrame{
     public FormCekTanggal() {
        setTitle("Cek Pasien & Janji Berdasarkan Tanggal");
        setSize(400, 200);
        setLayout(null);

        JLabel lTgl = new JLabel("Tanggal");
        JTextField tfTgl = new JTextField();
        JButton btnCari = new JButton("Cari");

        lTgl.setBounds(30, 30, 100, 25);
        tfTgl.setBounds(150, 30, 200, 25);
        btnCari.setBounds(150, 70, 100, 30);

        add(lTgl); add(tfTgl); add(btnCari);

        btnCari.addActionListener(e -> JOptionPane.showMessageDialog(this, "Pasien: ID001, Nama: Lisa"));

        setVisible(true);
    }
}
