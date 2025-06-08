package view;

import config.koneksi;
import controller.JanjiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormEditJanji extends JPanel {
    // Fields UI
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtIdJanji, txtIdPasien, txtNamaPasien;
    private JSpinner spinnerTanggal, spinnerWaktu;
    private JComboBox<String> cmbStatus;
    private JTable tableJanjiTemu;
    private DefaultTableModel modelJanjiTemu;
    private JButton btnUpdate, btnDelete, btnUpdateStatus, btnSelesaikan;

    public FormEditJanji() {
        initUI();
        initEvents();
        loadJanjiData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header & Search
        JPanel north = new JPanel(new BorderLayout(10, 10));
        north.setBackground(new Color(63, 81, 181));
        north.setBorder(new EmptyBorder(20,20,20,20));
        JLabel lblTitle = new JLabel("✎ Edit / Hapus Janji Temu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        north.add(lblTitle, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.setToolTipText("Cari berdasarkan ID, Nama, atau Tanggal...");
        north.add(txtSearch, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // Left Form
        JPanel form = createFormPanel();
        add(form, BorderLayout.WEST);

        // Table
        modelJanjiTemu = new DefaultTableModel(
            new Object[]{"ID Janji","ID Pasien","Nama Pasien","Tanggal","Waktu","Status"},0) {
            public boolean isCellEditable(int r, int c){return false;}
        };
        tableJanjiTemu = new JTable(modelJanjiTemu);
        tableJanjiTemu.setRowHeight(28);
        tableJanjiTemu.setDefaultRenderer(Object.class, new StatusHighlightRenderer());
        sorter = new TableRowSorter<>(modelJanjiTemu);
        tableJanjiTemu.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(tableJanjiTemu);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Janji Temu"));
        add(scroll, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        south.setBackground(new Color(250,250,255));
        btnUpdate = createBtn("↺ Update Janji", new Color(46,204,113));
        btnDelete = createBtn("❌ Hapus Janji", new Color(231,76,60));
        btnUpdateStatus = createBtn("⬆️ Update Status", new Color(52,152,219));
        btnSelesaikan = createBtn("✔️ Selesaikan Janji", new Color(39,174,96));
        south.add(btnUpdate);
        south.add(btnDelete);
        south.add(btnUpdateStatus);
        south.add(btnSelesaikan);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Detail Janji Temu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;

        p.add(new JLabel("ID Janji:"), fieldConstraint(gbc,0,y));
        txtIdJanji = new JTextField(); txtIdJanji.setEditable(false);
        p.add(txtIdJanji, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("ID Pasien:"), fieldConstraint(gbc,0,y));
        txtIdPasien = new JTextField(); txtIdPasien.setEditable(false);
        p.add(txtIdPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Nama Pasien:"), fieldConstraint(gbc,0,y));
        txtNamaPasien = new JTextField(); txtNamaPasien.setEditable(false);
        p.add(txtNamaPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Tanggal:"), fieldConstraint(gbc,0,y));
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal,"yyyy‑MM‑dd"));
        p.add(spinnerTanggal, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Waktu:"), fieldConstraint(gbc,0,y));
        spinnerWaktu = new JSpinner(new SpinnerDateModel());
        spinnerWaktu.setEditor(new JSpinner.DateEditor(spinnerWaktu,"HH:mm"));
        p.add(spinnerWaktu, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Status:"), fieldConstraint(gbc,0,y));
        cmbStatus = new JComboBox<>(new String[]{"Menunggu","Dilayani","Batal"});
        p.add(cmbStatus, fieldConstraint(gbc,1,y++));
        return p;
    }

    private GridBagConstraints fieldConstraint(GridBagConstraints gbc, int x, int y){
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = x; c.gridy = y;
        return c;
    }

    private JButton createBtn(String text, Color bg){
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD,13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(160,36));
        b.setFocusPainted(false);
        return b;
    }

    private void initEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
            private void filter(){
                String t = txtSearch.getText().trim();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)"+t));
            }
        });

        tableJanjiTemu.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                populateFromTable();
            }
        });

        btnUpdate.addActionListener(e->updateJanji());
        btnDelete.addActionListener(e->deleteJanji());
        btnUpdateStatus.addActionListener(e->updateStatus());
        btnSelesaikan.addActionListener(e->selesaikanJanji());
    }

    private void loadJanjiData(){
        modelJanjiTemu.setRowCount(0);
        String sql = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien=p.id_pasien ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC";
        try(Connection conn=koneksi.getKoneksi(); Statement s=conn.createStatement(); ResultSet r=s.executeQuery(sql)){
            while(r.next()){
                modelJanjiTemu.addRow(new Object[]{
                    r.getInt("id_janji_temu"),
                    r.getString("id_pasien"),
                    r.getString("nama_pasien"),
                    r.getDate("tanggal_janji"),
                    r.getTime("waktu_janji"),
                    r.getString("status")
                });
            }
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(this, "Load error: "+ex.getMessage());
        }
    }

    private void populateFromTable(){
        int row = tableJanjiTemu.getSelectedRow();
        if(row<0) return;
        row = tableJanjiTemu.convertRowIndexToModel(row);
        txtIdJanji.setText(modelJanjiTemu.getValueAt(row,0).toString());
        txtIdPasien.setText(modelJanjiTemu.getValueAt(row,1).toString());
        txtNamaPasien.setText(modelJanjiTemu.getValueAt(row,2).toString());
        spinnerTanggal.setValue(modelJanjiTemu.getValueAt(row,3));
        spinnerWaktu.setValue(modelJanjiTemu.getValueAt(row,4));
        cmbStatus.setSelectedItem(modelJanjiTemu.getValueAt(row,5).toString());
    }

    private void updateJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"), tf=new SimpleDateFormat("HH:mm:ss");
            JanjiController.updateFull(Integer.parseInt(txtIdJanji.getText()),
                df.format(spinnerTanggal.getValue()),
                tf.format(spinnerWaktu.getValue()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields();
            alert("Berhasil diupdate");
        }catch(Exception e){alert("Update gagal: "+e.getMessage());}
    }

    private void deleteJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji"); return;}
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus?")==JOptionPane.OK_OPTION){
            try{
                JanjiController.delete(Integer.parseInt(txtIdJanji.getText()));
                loadJanjiData(); clearFields(); alert("Terhapus");
            }catch(Exception e){alert("Gagal hapus: "+e.getMessage());}
        }
    }

    private void updateStatus(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            JanjiController.updateStatus(Integer.parseInt(txtIdJanji.getText()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields(); alert("Status berubah");
        }catch(Exception e){alert("Gagal update status: "+e.getMessage());}
    }

    private void selesaikanJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        if(cmbStatus.getSelectedItem().equals("Dilayani")){alert("Sudah selesai"); return;}
        String diag = JOptionPane.showInputDialog(this,"Masukkan diagnosis:");
        if(diag==null||diag.isBlank()){alert("Diagnosis tidak boleh kosong");return;}
        // Anda bisa tambahkan more dialog fields
        try{
            JanjiController.selesaikan(Integer.parseInt(txtIdJanji.getText()), diag, "", "");
            loadJanjiData(); clearFields(); alert("Selesai!");
        }catch(Exception e){alert("Gagal menyelesaikan: "+e.getMessage());}
    }

    private void clearFields(){
        txtIdJanji.setText(""); txtIdPasien.setText(""); txtNamaPasien.setText("");
        spinnerTanggal.setValue(new Date()); spinnerWaktu.setValue(new Date());
        cmbStatus.setSelectedIndex(0);
        tableJanjiTemu.clearSelection();
    }

    private void alert(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    // Renderer custom: highlight <tr> berdasar status
    private static class StatusHighlightRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            String status = t.getModel().getValueAt(t.convertRowIndexToModel(r),5).toString();
            if("Menunggu".equals(status)) comp.setBackground(new Color(255,249,196));
            else if("Dilayani".equals(status)) comp.setBackground(new Color(196,255,196));
            else if("Batal".equals(status)) comp.setBackground(new Color(255,196,196));
            else comp.setBackground(Color.WHITE);
            return comp;
        }
    }
}
package view;

import config.koneksi;
import controller.JanjiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormEditJanji extends JPanel {
    // Fields UI
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtIdJanji, txtIdPasien, txtNamaPasien;
    private JSpinner spinnerTanggal, spinnerWaktu;
    private JComboBox<String> cmbStatus;
    private JTable tableJanjiTemu;
    private DefaultTableModel modelJanjiTemu;
    private JButton btnUpdate, btnDelete, btnUpdateStatus, btnSelesaikan;

    public FormEditJanji() {
        initUI();
        initEvents();
        loadJanjiData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header & Search
        JPanel north = new JPanel(new BorderLayout(10, 10));
        north.setBackground(new Color(63, 81, 181));
        north.setBorder(new EmptyBorder(20,20,20,20));
        JLabel lblTitle = new JLabel("✎ Edit / Hapus Janji Temu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        north.add(lblTitle, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.setToolTipText("Cari berdasarkan ID, Nama, atau Tanggal...");
        north.add(txtSearch, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // Left Form
        JPanel form = createFormPanel();
        add(form, BorderLayout.WEST);

        // Table
        modelJanjiTemu = new DefaultTableModel(
            new Object[]{"ID Janji","ID Pasien","Nama Pasien","Tanggal","Waktu","Status"},0) {
            public boolean isCellEditable(int r, int c){return false;}
        };
        tableJanjiTemu = new JTable(modelJanjiTemu);
        tableJanjiTemu.setRowHeight(28);
        tableJanjiTemu.setDefaultRenderer(Object.class, new StatusHighlightRenderer());
        sorter = new TableRowSorter<>(modelJanjiTemu);
        tableJanjiTemu.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(tableJanjiTemu);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Janji Temu"));
        add(scroll, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        south.setBackground(new Color(250,250,255));
        btnUpdate = createBtn("↺ Update Janji", new Color(46,204,113));
        btnDelete = createBtn("❌ Hapus Janji", new Color(231,76,60));
        btnUpdateStatus = createBtn("⬆️ Update Status", new Color(52,152,219));
        btnSelesaikan = createBtn("✔️ Selesaikan Janji", new Color(39,174,96));
        south.add(btnUpdate);
        south.add(btnDelete);
        south.add(btnUpdateStatus);
        south.add(btnSelesaikan);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Detail Janji Temu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;

        p.add(new JLabel("ID Janji:"), fieldConstraint(gbc,0,y));
        txtIdJanji = new JTextField(); txtIdJanji.setEditable(false);
        p.add(txtIdJanji, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("ID Pasien:"), fieldConstraint(gbc,0,y));
        txtIdPasien = new JTextField(); txtIdPasien.setEditable(false);
        p.add(txtIdPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Nama Pasien:"), fieldConstraint(gbc,0,y));
        txtNamaPasien = new JTextField(); txtNamaPasien.setEditable(false);
        p.add(txtNamaPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Tanggal:"), fieldConstraint(gbc,0,y));
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal,"yyyy‑MM‑dd"));
        p.add(spinnerTanggal, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Waktu:"), fieldConstraint(gbc,0,y));
        spinnerWaktu = new JSpinner(new SpinnerDateModel());
        spinnerWaktu.setEditor(new JSpinner.DateEditor(spinnerWaktu,"HH:mm"));
        p.add(spinnerWaktu, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Status:"), fieldConstraint(gbc,0,y));
        cmbStatus = new JComboBox<>(new String[]{"Menunggu","Dilayani","Batal"});
        p.add(cmbStatus, fieldConstraint(gbc,1,y++));
        return p;
    }

    private GridBagConstraints fieldConstraint(GridBagConstraints gbc, int x, int y){
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = x; c.gridy = y;
        return c;
    }

    private JButton createBtn(String text, Color bg){
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD,13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(160,36));
        b.setFocusPainted(false);
        return b;
    }

    private void initEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
            private void filter(){
                String t = txtSearch.getText().trim();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)"+t));
            }
        });

        tableJanjiTemu.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                populateFromTable();
            }
        });

        btnUpdate.addActionListener(e->updateJanji());
        btnDelete.addActionListener(e->deleteJanji());
        btnUpdateStatus.addActionListener(e->updateStatus());
        btnSelesaikan.addActionListener(e->selesaikanJanji());
    }

    private void loadJanjiData(){
        modelJanjiTemu.setRowCount(0);
        String sql = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien=p.id_pasien ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC";
        try(Connection conn=koneksi.getKoneksi(); Statement s=conn.createStatement(); ResultSet r=s.executeQuery(sql)){
            while(r.next()){
                modelJanjiTemu.addRow(new Object[]{
                    r.getInt("id_janji_temu"),
                    r.getString("id_pasien"),
                    r.getString("nama_pasien"),
                    r.getDate("tanggal_janji"),
                    r.getTime("waktu_janji"),
                    r.getString("status")
                });
            }
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(this, "Load error: "+ex.getMessage());
        }
    }

    private void populateFromTable(){
        int row = tableJanjiTemu.getSelectedRow();
        if(row<0) return;
        row = tableJanjiTemu.convertRowIndexToModel(row);
        txtIdJanji.setText(modelJanjiTemu.getValueAt(row,0).toString());
        txtIdPasien.setText(modelJanjiTemu.getValueAt(row,1).toString());
        txtNamaPasien.setText(modelJanjiTemu.getValueAt(row,2).toString());
        spinnerTanggal.setValue(modelJanjiTemu.getValueAt(row,3));
        spinnerWaktu.setValue(modelJanjiTemu.getValueAt(row,4));
        cmbStatus.setSelectedItem(modelJanjiTemu.getValueAt(row,5).toString());
    }

    private void updateJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"), tf=new SimpleDateFormat("HH:mm:ss");
            JanjiController.updateFull(Integer.parseInt(txtIdJanji.getText()),
                df.format(spinnerTanggal.getValue()),
                tf.format(spinnerWaktu.getValue()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields();
            alert("Berhasil diupdate");
        }catch(Exception e){alert("Update gagal: "+e.getMessage());}
    }

    private void deleteJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji"); return;}
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus?")==JOptionPane.OK_OPTION){
            try{
                JanjiController.delete(Integer.parseInt(txtIdJanji.getText()));
                loadJanjiData(); clearFields(); alert("Terhapus");
            }catch(Exception e){alert("Gagal hapus: "+e.getMessage());}
        }
    }

    private void updateStatus(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            JanjiController.updateStatus(Integer.parseInt(txtIdJanji.getText()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields(); alert("Status berubah");
        }catch(Exception e){alert("Gagal update status: "+e.getMessage());}
    }

    private void selesaikanJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        if(cmbStatus.getSelectedItem().equals("Dilayani")){alert("Sudah selesai"); return;}
        String diag = JOptionPane.showInputDialog(this,"Masukkan diagnosis:");
        if(diag==null||diag.isBlank()){alert("Diagnosis tidak boleh kosong");return;}
        // Anda bisa tambahkan more dialog fields
        try{
            JanjiController.selesaikan(Integer.parseInt(txtIdJanji.getText()), diag, "", "");
            loadJanjiData(); clearFields(); alert("Selesai!");
        }catch(Exception e){alert("Gagal menyelesaikan: "+e.getMessage());}
    }

    private void clearFields(){
        txtIdJanji.setText(""); txtIdPasien.setText(""); txtNamaPasien.setText("");
        spinnerTanggal.setValue(new Date()); spinnerWaktu.setValue(new Date());
        cmbStatus.setSelectedIndex(0);
        tableJanjiTemu.clearSelection();
    }

    private void alert(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    // Renderer custom: highlight <tr> berdasar status
    private static class StatusHighlightRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            String status = t.getModel().getValueAt(t.convertRowIndexToModel(r),5).toString();
            if("Menunggu".equals(status)) comp.setBackground(new Color(255,249,196));
            else if("Dilayani".equals(status)) comp.setBackground(new Color(196,255,196));
            else if("Batal".equals(status)) comp.setBackground(new Color(255,196,196));
            else comp.setBackground(Color.WHITE);
            return comp;
        }
    }
}
package view;

import config.koneksi;
import controller.JanjiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormEditJanji extends JPanel {
    // Fields UI
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtIdJanji, txtIdPasien, txtNamaPasien;
    private JSpinner spinnerTanggal, spinnerWaktu;
    private JComboBox<String> cmbStatus;
    private JTable tableJanjiTemu;
    private DefaultTableModel modelJanjiTemu;
    private JButton btnUpdate, btnDelete, btnUpdateStatus, btnSelesaikan;

    public FormEditJanji() {
        initUI();
        initEvents();
        loadJanjiData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header & Search
        JPanel north = new JPanel(new BorderLayout(10, 10));
        north.setBackground(new Color(63, 81, 181));
        north.setBorder(new EmptyBorder(20,20,20,20));
        JLabel lblTitle = new JLabel("✎ Edit / Hapus Janji Temu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        north.add(lblTitle, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.setToolTipText("Cari berdasarkan ID, Nama, atau Tanggal...");
        north.add(txtSearch, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // Left Form
        JPanel form = createFormPanel();
        add(form, BorderLayout.WEST);

        // Table
        modelJanjiTemu = new DefaultTableModel(
            new Object[]{"ID Janji","ID Pasien","Nama Pasien","Tanggal","Waktu","Status"},0) {
            public boolean isCellEditable(int r, int c){return false;}
        };
        tableJanjiTemu = new JTable(modelJanjiTemu);
        tableJanjiTemu.setRowHeight(28);
        tableJanjiTemu.setDefaultRenderer(Object.class, new StatusHighlightRenderer());
        sorter = new TableRowSorter<>(modelJanjiTemu);
        tableJanjiTemu.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(tableJanjiTemu);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Janji Temu"));
        add(scroll, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        south.setBackground(new Color(250,250,255));
        btnUpdate = createBtn("↺ Update Janji", new Color(46,204,113));
        btnDelete = createBtn("❌ Hapus Janji", new Color(231,76,60));
        btnUpdateStatus = createBtn("⬆️ Update Status", new Color(52,152,219));
        btnSelesaikan = createBtn("✔️ Selesaikan Janji", new Color(39,174,96));
        south.add(btnUpdate);
        south.add(btnDelete);
        south.add(btnUpdateStatus);
        south.add(btnSelesaikan);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Detail Janji Temu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;

        p.add(new JLabel("ID Janji:"), fieldConstraint(gbc,0,y));
        txtIdJanji = new JTextField(); txtIdJanji.setEditable(false);
        p.add(txtIdJanji, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("ID Pasien:"), fieldConstraint(gbc,0,y));
        txtIdPasien = new JTextField(); txtIdPasien.setEditable(false);
        p.add(txtIdPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Nama Pasien:"), fieldConstraint(gbc,0,y));
        txtNamaPasien = new JTextField(); txtNamaPasien.setEditable(false);
        p.add(txtNamaPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Tanggal:"), fieldConstraint(gbc,0,y));
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal,"yyyy‑MM‑dd"));
        p.add(spinnerTanggal, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Waktu:"), fieldConstraint(gbc,0,y));
        spinnerWaktu = new JSpinner(new SpinnerDateModel());
        spinnerWaktu.setEditor(new JSpinner.DateEditor(spinnerWaktu,"HH:mm"));
        p.add(spinnerWaktu, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Status:"), fieldConstraint(gbc,0,y));
        cmbStatus = new JComboBox<>(new String[]{"Menunggu","Dilayani","Batal"});
        p.add(cmbStatus, fieldConstraint(gbc,1,y++));
        return p;
    }

    private GridBagConstraints fieldConstraint(GridBagConstraints gbc, int x, int y){
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = x; c.gridy = y;
        return c;
    }

    private JButton createBtn(String text, Color bg){
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD,13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(160,36));
        b.setFocusPainted(false);
        return b;
    }

    private void initEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
            private void filter(){
                String t = txtSearch.getText().trim();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)"+t));
            }
        });

        tableJanjiTemu.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                populateFromTable();
            }
        });

        btnUpdate.addActionListener(e->updateJanji());
        btnDelete.addActionListener(e->deleteJanji());
        btnUpdateStatus.addActionListener(e->updateStatus());
        btnSelesaikan.addActionListener(e->selesaikanJanji());
    }

    private void loadJanjiData(){
        modelJanjiTemu.setRowCount(0);
        String sql = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien=p.id_pasien ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC";
        try(Connection conn=koneksi.getKoneksi(); Statement s=conn.createStatement(); ResultSet r=s.executeQuery(sql)){
            while(r.next()){
                modelJanjiTemu.addRow(new Object[]{
                    r.getInt("id_janji_temu"),
                    r.getString("id_pasien"),
                    r.getString("nama_pasien"),
                    r.getDate("tanggal_janji"),
                    r.getTime("waktu_janji"),
                    r.getString("status")
                });
            }
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(this, "Load error: "+ex.getMessage());
        }
    }

    private void populateFromTable(){
        int row = tableJanjiTemu.getSelectedRow();
        if(row<0) return;
        row = tableJanjiTemu.convertRowIndexToModel(row);
        txtIdJanji.setText(modelJanjiTemu.getValueAt(row,0).toString());
        txtIdPasien.setText(modelJanjiTemu.getValueAt(row,1).toString());
        txtNamaPasien.setText(modelJanjiTemu.getValueAt(row,2).toString());
        spinnerTanggal.setValue(modelJanjiTemu.getValueAt(row,3));
        spinnerWaktu.setValue(modelJanjiTemu.getValueAt(row,4));
        cmbStatus.setSelectedItem(modelJanjiTemu.getValueAt(row,5).toString());
    }

    private void updateJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"), tf=new SimpleDateFormat("HH:mm:ss");
            JanjiController.updateFull(Integer.parseInt(txtIdJanji.getText()),
                df.format(spinnerTanggal.getValue()),
                tf.format(spinnerWaktu.getValue()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields();
            alert("Berhasil diupdate");
        }catch(Exception e){alert("Update gagal: "+e.getMessage());}
    }

    private void deleteJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji"); return;}
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus?")==JOptionPane.OK_OPTION){
            try{
                JanjiController.delete(Integer.parseInt(txtIdJanji.getText()));
                loadJanjiData(); clearFields(); alert("Terhapus");
            }catch(Exception e){alert("Gagal hapus: "+e.getMessage());}
        }
    }

    private void updateStatus(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            JanjiController.updateStatus(Integer.parseInt(txtIdJanji.getText()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields(); alert("Status berubah");
        }catch(Exception e){alert("Gagal update status: "+e.getMessage());}
    }

    private void selesaikanJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        if(cmbStatus.getSelectedItem().equals("Dilayani")){alert("Sudah selesai"); return;}
        String diag = JOptionPane.showInputDialog(this,"Masukkan diagnosis:");
        if(diag==null||diag.isBlank()){alert("Diagnosis tidak boleh kosong");return;}
        // Anda bisa tambahkan more dialog fields
        try{
            JanjiController.selesaikan(Integer.parseInt(txtIdJanji.getText()), diag, "", "");
            loadJanjiData(); clearFields(); alert("Selesai!");
        }catch(Exception e){alert("Gagal menyelesaikan: "+e.getMessage());}
    }

    private void clearFields(){
        txtIdJanji.setText(""); txtIdPasien.setText(""); txtNamaPasien.setText("");
        spinnerTanggal.setValue(new Date()); spinnerWaktu.setValue(new Date());
        cmbStatus.setSelectedIndex(0);
        tableJanjiTemu.clearSelection();
    }

    private void alert(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    // Renderer custom: highlight <tr> berdasar status
    private static class StatusHighlightRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            String status = t.getModel().getValueAt(t.convertRowIndexToModel(r),5).toString();
            if("Menunggu".equals(status)) comp.setBackground(new Color(255,249,196));
            else if("Dilayani".equals(status)) comp.setBackground(new Color(196,255,196));
            else if("Batal".equals(status)) comp.setBackground(new Color(255,196,196));
            else comp.setBackground(Color.WHITE);
            return comp;
        }
    }
}
package view;

import config.koneksi;
import controller.JanjiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormEditJanji extends JPanel {
    // Fields UI
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtIdJanji, txtIdPasien, txtNamaPasien;
    private JSpinner spinnerTanggal, spinnerWaktu;
    private JComboBox<String> cmbStatus;
    private JTable tableJanjiTemu;
    private DefaultTableModel modelJanjiTemu;
    private JButton btnUpdate, btnDelete, btnUpdateStatus, btnSelesaikan;

    public FormEditJanji() {
        initUI();
        initEvents();
        loadJanjiData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header & Search
        JPanel north = new JPanel(new BorderLayout(10, 10));
        north.setBackground(new Color(63, 81, 181));
        north.setBorder(new EmptyBorder(20,20,20,20));
        JLabel lblTitle = new JLabel("✎ Edit / Hapus Janji Temu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        north.add(lblTitle, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.setToolTipText("Cari berdasarkan ID, Nama, atau Tanggal...");
        north.add(txtSearch, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // Left Form
        JPanel form = createFormPanel();
        add(form, BorderLayout.WEST);

        // Table
        modelJanjiTemu = new DefaultTableModel(
            new Object[]{"ID Janji","ID Pasien","Nama Pasien","Tanggal","Waktu","Status"},0) {
            public boolean isCellEditable(int r, int c){return false;}
        };
        tableJanjiTemu = new JTable(modelJanjiTemu);
        tableJanjiTemu.setRowHeight(28);
        tableJanjiTemu.setDefaultRenderer(Object.class, new StatusHighlightRenderer());
        sorter = new TableRowSorter<>(modelJanjiTemu);
        tableJanjiTemu.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(tableJanjiTemu);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Janji Temu"));
        add(scroll, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        south.setBackground(new Color(250,250,255));
        btnUpdate = createBtn("↺ Update Janji", new Color(46,204,113));
        btnDelete = createBtn("❌ Hapus Janji", new Color(231,76,60));
        btnUpdateStatus = createBtn("⬆️ Update Status", new Color(52,152,219));
        btnSelesaikan = createBtn("✔️ Selesaikan Janji", new Color(39,174,96));
        south.add(btnUpdate);
        south.add(btnDelete);
        south.add(btnUpdateStatus);
        south.add(btnSelesaikan);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Detail Janji Temu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;

        p.add(new JLabel("ID Janji:"), fieldConstraint(gbc,0,y));
        txtIdJanji = new JTextField(); txtIdJanji.setEditable(false);
        p.add(txtIdJanji, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("ID Pasien:"), fieldConstraint(gbc,0,y));
        txtIdPasien = new JTextField(); txtIdPasien.setEditable(false);
        p.add(txtIdPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Nama Pasien:"), fieldConstraint(gbc,0,y));
        txtNamaPasien = new JTextField(); txtNamaPasien.setEditable(false);
        p.add(txtNamaPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Tanggal:"), fieldConstraint(gbc,0,y));
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal,"yyyy‑MM‑dd"));
        p.add(spinnerTanggal, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Waktu:"), fieldConstraint(gbc,0,y));
        spinnerWaktu = new JSpinner(new SpinnerDateModel());
        spinnerWaktu.setEditor(new JSpinner.DateEditor(spinnerWaktu,"HH:mm"));
        p.add(spinnerWaktu, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Status:"), fieldConstraint(gbc,0,y));
        cmbStatus = new JComboBox<>(new String[]{"Menunggu","Dilayani","Batal"});
        p.add(cmbStatus, fieldConstraint(gbc,1,y++));
        return p;
    }

    private GridBagConstraints fieldConstraint(GridBagConstraints gbc, int x, int y){
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = x; c.gridy = y;
        return c;
    }

    private JButton createBtn(String text, Color bg){
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD,13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(160,36));
        b.setFocusPainted(false);
        return b;
    }

    private void initEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
            private void filter(){
                String t = txtSearch.getText().trim();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)"+t));
            }
        });

        tableJanjiTemu.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                populateFromTable();
            }
        });

        btnUpdate.addActionListener(e->updateJanji());
        btnDelete.addActionListener(e->deleteJanji());
        btnUpdateStatus.addActionListener(e->updateStatus());
        btnSelesaikan.addActionListener(e->selesaikanJanji());
    }

    private void loadJanjiData(){
        modelJanjiTemu.setRowCount(0);
        String sql = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien=p.id_pasien ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC";
        try(Connection conn=koneksi.getKoneksi(); Statement s=conn.createStatement(); ResultSet r=s.executeQuery(sql)){
            while(r.next()){
                modelJanjiTemu.addRow(new Object[]{
                    r.getInt("id_janji_temu"),
                    r.getString("id_pasien"),
                    r.getString("nama_pasien"),
                    r.getDate("tanggal_janji"),
                    r.getTime("waktu_janji"),
                    r.getString("status")
                });
            }
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(this, "Load error: "+ex.getMessage());
        }
    }

    private void populateFromTable(){
        int row = tableJanjiTemu.getSelectedRow();
        if(row<0) return;
        row = tableJanjiTemu.convertRowIndexToModel(row);
        txtIdJanji.setText(modelJanjiTemu.getValueAt(row,0).toString());
        txtIdPasien.setText(modelJanjiTemu.getValueAt(row,1).toString());
        txtNamaPasien.setText(modelJanjiTemu.getValueAt(row,2).toString());
        spinnerTanggal.setValue(modelJanjiTemu.getValueAt(row,3));
        spinnerWaktu.setValue(modelJanjiTemu.getValueAt(row,4));
        cmbStatus.setSelectedItem(modelJanjiTemu.getValueAt(row,5).toString());
    }

    private void updateJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"), tf=new SimpleDateFormat("HH:mm:ss");
            JanjiController.updateFull(Integer.parseInt(txtIdJanji.getText()),
                df.format(spinnerTanggal.getValue()),
                tf.format(spinnerWaktu.getValue()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields();
            alert("Berhasil diupdate");
        }catch(Exception e){alert("Update gagal: "+e.getMessage());}
    }

    private void deleteJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji"); return;}
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus?")==JOptionPane.OK_OPTION){
            try{
                JanjiController.delete(Integer.parseInt(txtIdJanji.getText()));
                loadJanjiData(); clearFields(); alert("Terhapus");
            }catch(Exception e){alert("Gagal hapus: "+e.getMessage());}
        }
    }

    private void updateStatus(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            JanjiController.updateStatus(Integer.parseInt(txtIdJanji.getText()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields(); alert("Status berubah");
        }catch(Exception e){alert("Gagal update status: "+e.getMessage());}
    }

    private void selesaikanJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        if(cmbStatus.getSelectedItem().equals("Dilayani")){alert("Sudah selesai"); return;}
        String diag = JOptionPane.showInputDialog(this,"Masukkan diagnosis:");
        if(diag==null||diag.isBlank()){alert("Diagnosis tidak boleh kosong");return;}
        // Anda bisa tambahkan more dialog fields
        try{
            JanjiController.selesaikan(Integer.parseInt(txtIdJanji.getText()), diag, "", "");
            loadJanjiData(); clearFields(); alert("Selesai!");
        }catch(Exception e){alert("Gagal menyelesaikan: "+e.getMessage());}
    }

    private void clearFields(){
        txtIdJanji.setText(""); txtIdPasien.setText(""); txtNamaPasien.setText("");
        spinnerTanggal.setValue(new Date()); spinnerWaktu.setValue(new Date());
        cmbStatus.setSelectedIndex(0);
        tableJanjiTemu.clearSelection();
    }

    private void alert(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    // Renderer custom: highlight <tr> berdasar status
    private static class StatusHighlightRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            String status = t.getModel().getValueAt(t.convertRowIndexToModel(r),5).toString();
            if("Menunggu".equals(status)) comp.setBackground(new Color(255,249,196));
            else if("Dilayani".equals(status)) comp.setBackground(new Color(196,255,196));
            else if("Batal".equals(status)) comp.setBackground(new Color(255,196,196));
            else comp.setBackground(Color.WHITE);
            return comp;
        }
    }
}
package view;

import config.koneksi;
import controller.JanjiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormEditJanji extends JPanel {
    // Fields UI
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtIdJanji, txtIdPasien, txtNamaPasien;
    private JSpinner spinnerTanggal, spinnerWaktu;
    private JComboBox<String> cmbStatus;
    private JTable tableJanjiTemu;
    private DefaultTableModel modelJanjiTemu;
    private JButton btnUpdate, btnDelete, btnUpdateStatus, btnSelesaikan;

    public FormEditJanji() {
        initUI();
        initEvents();
        loadJanjiData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header & Search
        JPanel north = new JPanel(new BorderLayout(10, 10));
        north.setBackground(new Color(63, 81, 181));
        north.setBorder(new EmptyBorder(20,20,20,20));
        JLabel lblTitle = new JLabel("✎ Edit / Hapus Janji Temu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        north.add(lblTitle, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.setToolTipText("Cari berdasarkan ID, Nama, atau Tanggal...");
        north.add(txtSearch, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // Left Form
        JPanel form = createFormPanel();
        add(form, BorderLayout.WEST);

        // Table
        modelJanjiTemu = new DefaultTableModel(
            new Object[]{"ID Janji","ID Pasien","Nama Pasien","Tanggal","Waktu","Status"},0) {
            public boolean isCellEditable(int r, int c){return false;}
        };
        tableJanjiTemu = new JTable(modelJanjiTemu);
        tableJanjiTemu.setRowHeight(28);
        tableJanjiTemu.setDefaultRenderer(Object.class, new StatusHighlightRenderer());
        sorter = new TableRowSorter<>(modelJanjiTemu);
        tableJanjiTemu.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(tableJanjiTemu);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Janji Temu"));
        add(scroll, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        south.setBackground(new Color(250,250,255));
        btnUpdate = createBtn("↺ Update Janji", new Color(46,204,113));
        btnDelete = createBtn("❌ Hapus Janji", new Color(231,76,60));
        btnUpdateStatus = createBtn("⬆️ Update Status", new Color(52,152,219));
        btnSelesaikan = createBtn("✔️ Selesaikan Janji", new Color(39,174,96));
        south.add(btnUpdate);
        south.add(btnDelete);
        south.add(btnUpdateStatus);
        south.add(btnSelesaikan);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Detail Janji Temu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;

        p.add(new JLabel("ID Janji:"), fieldConstraint(gbc,0,y));
        txtIdJanji = new JTextField(); txtIdJanji.setEditable(false);
        p.add(txtIdJanji, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("ID Pasien:"), fieldConstraint(gbc,0,y));
        txtIdPasien = new JTextField(); txtIdPasien.setEditable(false);
        p.add(txtIdPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Nama Pasien:"), fieldConstraint(gbc,0,y));
        txtNamaPasien = new JTextField(); txtNamaPasien.setEditable(false);
        p.add(txtNamaPasien, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Tanggal:"), fieldConstraint(gbc,0,y));
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal,"yyyy‑MM‑dd"));
        p.add(spinnerTanggal, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Waktu:"), fieldConstraint(gbc,0,y));
        spinnerWaktu = new JSpinner(new SpinnerDateModel());
        spinnerWaktu.setEditor(new JSpinner.DateEditor(spinnerWaktu,"HH:mm"));
        p.add(spinnerWaktu, fieldConstraint(gbc,1,y++));
        p.add(new JLabel("Status:"), fieldConstraint(gbc,0,y));
        cmbStatus = new JComboBox<>(new String[]{"Menunggu","Dilayani","Batal"});
        p.add(cmbStatus, fieldConstraint(gbc,1,y++));
        return p;
    }

    private GridBagConstraints fieldConstraint(GridBagConstraints gbc, int x, int y){
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = x; c.gridy = y;
        return c;
    }

    private JButton createBtn(String text, Color bg){
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD,13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(160,36));
        b.setFocusPainted(false);
        return b;
    }

    private void initEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
            private void filter(){
                String t = txtSearch.getText().trim();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)"+t));
            }
        });

        tableJanjiTemu.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                populateFromTable();
            }
        });

        btnUpdate.addActionListener(e->updateJanji());
        btnDelete.addActionListener(e->deleteJanji());
        btnUpdateStatus.addActionListener(e->updateStatus());
        btnSelesaikan.addActionListener(e->selesaikanJanji());
    }

    private void loadJanjiData(){
        modelJanjiTemu.setRowCount(0);
        String sql = "SELECT jt.id_janji_temu, jt.id_pasien, p.nama_pasien, jt.tanggal_janji, jt.waktu_janji, jt.status FROM janji_temu jt JOIN pasien p ON jt.id_pasien=p.id_pasien ORDER BY jt.tanggal_janji DESC, jt.waktu_janji DESC";
        try(Connection conn=koneksi.getKoneksi(); Statement s=conn.createStatement(); ResultSet r=s.executeQuery(sql)){
            while(r.next()){
                modelJanjiTemu.addRow(new Object[]{
                    r.getInt("id_janji_temu"),
                    r.getString("id_pasien"),
                    r.getString("nama_pasien"),
                    r.getDate("tanggal_janji"),
                    r.getTime("waktu_janji"),
                    r.getString("status")
                });
            }
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(this, "Load error: "+ex.getMessage());
        }
    }

    private void populateFromTable(){
        int row = tableJanjiTemu.getSelectedRow();
        if(row<0) return;
        row = tableJanjiTemu.convertRowIndexToModel(row);
        txtIdJanji.setText(modelJanjiTemu.getValueAt(row,0).toString());
        txtIdPasien.setText(modelJanjiTemu.getValueAt(row,1).toString());
        txtNamaPasien.setText(modelJanjiTemu.getValueAt(row,2).toString());
        spinnerTanggal.setValue(modelJanjiTemu.getValueAt(row,3));
        spinnerWaktu.setValue(modelJanjiTemu.getValueAt(row,4));
        cmbStatus.setSelectedItem(modelJanjiTemu.getValueAt(row,5).toString());
    }

    private void updateJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"), tf=new SimpleDateFormat("HH:mm:ss");
            JanjiController.updateFull(Integer.parseInt(txtIdJanji.getText()),
                df.format(spinnerTanggal.getValue()),
                tf.format(spinnerWaktu.getValue()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields();
            alert("Berhasil diupdate");
        }catch(Exception e){alert("Update gagal: "+e.getMessage());}
    }

    private void deleteJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji"); return;}
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus?")==JOptionPane.OK_OPTION){
            try{
                JanjiController.delete(Integer.parseInt(txtIdJanji.getText()));
                loadJanjiData(); clearFields(); alert("Terhapus");
            }catch(Exception e){alert("Gagal hapus: "+e.getMessage());}
        }
    }

    private void updateStatus(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        try{
            JanjiController.updateStatus(Integer.parseInt(txtIdJanji.getText()),
                cmbStatus.getSelectedItem().toString());
            loadJanjiData(); clearFields(); alert("Status berubah");
        }catch(Exception e){alert("Gagal update status: "+e.getMessage());}
    }

    private void selesaikanJanji(){
        if(txtIdJanji.getText().isEmpty()){alert("Pilih janji");return;}
        if(cmbStatus.getSelectedItem().equals("Dilayani")){alert("Sudah selesai"); return;}
        String diag = JOptionPane.showInputDialog(this,"Masukkan diagnosis:");
        if(diag==null||diag.isBlank()){alert("Diagnosis tidak boleh kosong");return;}
        // Anda bisa tambahkan more dialog fields
        try{
            JanjiController.selesaikan(Integer.parseInt(txtIdJanji.getText()), diag, "", "");
            loadJanjiData(); clearFields(); alert("Selesai!");
        }catch(Exception e){alert("Gagal menyelesaikan: "+e.getMessage());}
    }

    private void clearFields(){
        txtIdJanji.setText(""); txtIdPasien.setText(""); txtNamaPasien.setText("");
        spinnerTanggal.setValue(new Date()); spinnerWaktu.setValue(new Date());
        cmbStatus.setSelectedIndex(0);
        tableJanjiTemu.clearSelection();
    }

    private void alert(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }

    // Renderer custom: highlight <tr> berdasar status
    private static class StatusHighlightRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            String status = t.getModel().getValueAt(t.convertRowIndexToModel(r),5).toString();
            if("Menunggu".equals(status)) comp.setBackground(new Color(255,249,196));
            else if("Dilayani".equals(status)) comp.setBackground(new Color(196,255,196));
            else if("Batal".equals(status)) comp.setBackground(new Color(255,196,196));
            else comp.setBackground(Color.WHITE);
            return comp;
        }
    }
}
