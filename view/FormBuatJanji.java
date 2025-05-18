package view;
import javax.swing.*;
/**
 *
 * @author ASUS
 */
public class FormBuatJanji extends JFrame{
    public FormBuatJanji() {
        setTitle("Buat Janji Temu");
        setSize(400, 300);
        setLayout(null);

        JLabel lId = new JLabel("ID Pasien");
        JTextField tfId = new JTextField();
        JLabel lTanggal = new JLabel("Tanggal Janji");
        JTextField tfTanggal = new JTextField();
        JButton btnSimpan = new JButton("Simpan");

        lId.setBounds(30, 30, 100, 25);
        tfId.setBounds(150, 30, 200, 25);
        lTanggal.setBounds(30, 70, 100, 25);
        tfTanggal.setBounds(150, 70, 200, 25);
        btnSimpan.setBounds(150, 110, 100, 30);

        add(lId); add(tfId); add(lTanggal); add(tfTanggal); add(btnSimpan);

        btnSimpan.addActionListener(e -> {
            // Simpan janji ke file atau database
            JOptionPane.showMessageDialog(this, "Janji berhasil dibuat.");
        });

        setVisible(true);
    }
}
