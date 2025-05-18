package view;
import javax.swing.*;
/**
 *
 * @author ASUS
 */
public class FormExportCSV extends JFrame{
     public FormExportCSV() {
        setTitle("Export Laporan ke CSV");
        setSize(400, 200);
        setLayout(null);

        JButton btnExport = new JButton("Export ke CSV");
        btnExport.setBounds(120, 60, 150, 30);
        add(btnExport);

        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Laporan berhasil diekspor."));

        setVisible(true);
    }
}
