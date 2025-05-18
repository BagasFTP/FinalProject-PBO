package view;
import javax.swing.*;
/**
 *
 * @author ASUS
 */
public class FormEditJanji extends JFrame{
    public FormEditJanji() {
        setTitle("Edit / Hapus Janji");
        setSize(400, 300);
        setLayout(null);

        JLabel lId = new JLabel("ID Pasien");
        JTextField tfId = new JTextField();
        JLabel lTanggal = new JLabel("Tanggal Baru");
        JTextField tfTanggal = new JTextField();
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");

        lId.setBounds(30, 30, 100, 25);
        tfId.setBounds(150, 30, 200, 25);
        lTanggal.setBounds(30, 70, 100, 25);
        tfTanggal.setBounds(150, 70, 200, 25);
        btnEdit.setBounds(80, 110, 100, 30);
        btnHapus.setBounds(200, 110, 100, 30);

        add(lId); add(tfId); add(lTanggal); add(tfTanggal);
        add(btnEdit); add(btnHapus);

        btnEdit.addActionListener(e -> JOptionPane.showMessageDialog(this, "Janji berhasil diedit."));
        btnHapus.addActionListener(e -> JOptionPane.showMessageDialog(this, "Janji berhasil dihapus."));

        setVisible(true);
    }
}
