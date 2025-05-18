package view;
import javax.swing.*;
/**
 *
 * @author ASUS
 */
public class FormReminder extends JFrame{
    public FormReminder() {
        setTitle("Reminder Jadwal Temu");
        setSize(400, 200);
        setLayout(null);

        JLabel lId = new JLabel("ID Pasien");
        JTextField tfId = new JTextField();
        JButton btnCek = new JButton("Cek Jadwal");

        lId.setBounds(30, 30, 100, 25);
        tfId.setBounds(150, 30, 200, 25);
        btnCek.setBounds(150, 70, 120, 30);

        add(lId); add(tfId); add(btnCek);

        btnCek.addActionListener(e -> JOptionPane.showMessageDialog(this, "Jadwal temu: 20 Mei 2025"));

        setVisible(true);
    }
}
