package view;
import javax.swing.*;
/**
 *
 * @author ASUS
 */
public class FormStatistik extends JFrame{
     public FormStatistik() {
        setTitle("Statistik Kunjungan");
        setSize(400, 300);
        setLayout(null);

        JLabel lblStat = new JLabel("Total Kunjungan Bulan Ini: 45");
        lblStat.setBounds(80, 100, 300, 30);
        add(lblStat);

        setVisible(true);
    }
}
