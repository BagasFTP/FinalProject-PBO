package view;
import javax.swing.*;
/**
 *
 * @author ASUS
 */
public class FormRekamMedis extends JFrame{
    public FormRekamMedis() {
        setTitle("Rekam Medis Pasien");
        setSize(400, 300);
        setLayout(null);

        JLabel lId = new JLabel("ID Pasien");
        JTextField tfId = new JTextField();
        JButton btnLihat = new JButton("Lihat Rekam Medis");

        lId.setBounds(30, 30, 100, 25);
        tfId.setBounds(150, 30, 200, 25);
        btnLihat.setBounds(120, 70, 160, 30);

        add(lId); add(tfId); add(btnLihat);

        btnLihat.addActionListener(e -> JOptionPane.showMessageDialog(this, "Riwayat: 20/4/25 - Flu, 10/5/25 - Cek gigi"));

        setVisible(true);
    }
}
