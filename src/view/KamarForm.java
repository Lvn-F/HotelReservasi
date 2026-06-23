package view; // change to your package name

import config.DBConnection;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class KamarForm extends javax.swing.JFrame {

    private DefaultTableModel tableModel;
    public KamarForm() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Kelola Kamar - Hotel Reservasi");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setupTable();
        loadData();
    }
    private void setupTable() {
        tableModel = new DefaultTableModel(
            new String[]{"ID", "No Kamar", "Tipe", "Kapasitas", "Harga", "Status"}, 0
        ) {
            // Make table cells not editable by clicking
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblKamar.setModel(tableModel);
        tblKamar.getColumnModel().getColumn(0).setMinWidth(0);
        tblKamar.getColumnModel().getColumn(0).setMaxWidth(0); // hide ID column
        tblKamar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When user clicks a row, fill the form fields
        tblKamar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }
    private void loadData() {
        tableModel.setRowCount(0); // clear table first
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM kamar ORDER BY noKamar";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("idKamar"),
                    rs.getString("noKamar"),
                    rs.getString("tipeKamar"),
                    rs.getInt("kapasitas"),
                    rs.getDouble("harga"),
                    rs.getString("status")
                });
            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void fillFormFromTable() {
        int row = tblKamar.getSelectedRow();
        if (row == -1) return;

        txtNoKamar.setText(tableModel.getValueAt(row, 1).toString());
        cmbTipe.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        txtKapasitas.setText(tableModel.getValueAt(row, 3).toString());
        txtHarga.setText(tableModel.getValueAt(row, 4).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5).toString());
    }
    
    private boolean validateInput() {
        if (txtNoKamar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Kamar tidak boleh kosong!");
            return false;
        }
        if (txtKapasitas.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kapasitas tidak boleh kosong!");
            return false;
        }
        if (txtHarga.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harga tidak boleh kosong!");
            return false;
        }
        try {
            Integer.parseInt(txtKapasitas.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kapasitas harus berupa angka!");
            return false;
        }
        try {
            Double.parseDouble(txtHarga.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
            return false;
        }
        return true;
    }
    private void clearForm() {
        txtNoKamar.setText("");
        txtKapasitas.setText("");
        txtHarga.setText("");
        cmbTipe.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        tblKamar.clearSelection();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnHapus = new javax.swing.JButton();
        btnTambah = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        txtNoKamar = new javax.swing.JTextField();
        cmbTipe = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        txtKapasitas = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKamar = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        txtNoKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoKamarActionPerformed(evt);
            }
        });

        cmbTipe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Standard", "Deluxe", "Suite" }));

        jLabel1.setText("Tipe");

        txtKapasitas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKapasitasActionPerformed(evt);
            }
        });

        jLabel2.setText("Status");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Occupied", "Maintenance" }));

        tblKamar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblKamar);

        jLabel3.setText("Kapasitas");

        jLabel4.setText("Harga");

        jLabel5.setText("No Kamar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(27, 27, 27))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5))
                                    .addGap(23, 23, 23))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(27, 27, 27)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtHarga)
                            .addComponent(cmbTipe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNoKamar)
                            .addComponent(txtKapasitas, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(108, 108, 108))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNoKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbTipe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKapasitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(79, 79, 79)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEdit)
                    .addComponent(btnTambah)
                    .addComponent(btnHapus)
                    .addComponent(btnClear))
                .addGap(175, 175, 175))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        if (!validateInput()) return;

        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO kamar (noKamar, tipeKamar, kapasitas, harga, status) VALUES (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtNoKamar.getText().trim());
            ps.setString(2, cmbTipe.getSelectedItem().toString());
            ps.setInt(3, Integer.parseInt(txtKapasitas.getText().trim()));
            ps.setDouble(4, Double.parseDouble(txtHarga.getText().trim()));
            ps.setString(5, cmbStatus.getSelectedItem().toString());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Kamar berhasil ditambahkan!");
            clearForm();
            loadData();

            ps.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal menambahkan kamar!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        int row = tblKamar.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kamar yang ingin diedit!");
            return;
        }
        if (!validateInput()) return;

        int idKamar = (int) tableModel.getValueAt(row, 0);

        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE kamar SET noKamar=?, tipeKamar=?, kapasitas=?, harga=?, status=? WHERE idKamar=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtNoKamar.getText().trim());
            ps.setString(2, cmbTipe.getSelectedItem().toString());
            ps.setInt(3, Integer.parseInt(txtKapasitas.getText().trim()));
            ps.setDouble(4, Double.parseDouble(txtHarga.getText().trim()));
            ps.setString(5, cmbStatus.getSelectedItem().toString());
            ps.setInt(6, idKamar);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Kamar berhasil diupdate!");
            clearForm();
            loadData();

            ps.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate kamar!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int row = tblKamar.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kamar yang ingin dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus kamar ini?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int idKamar = (int) tableModel.getValueAt(row, 0);
            try {
                Connection con = DBConnection.getConnection();
                String sql = "DELETE FROM kamar WHERE idKamar=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, idKamar);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Kamar berhasil dihapus!");
                clearForm();
                loadData();

                ps.close();
                con.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus kamar!\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtNoKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoKamarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoKamarActionPerformed

    private void txtKapasitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKapasitasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKapasitasActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KamarForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KamarForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KamarForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KamarForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KamarForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> cmbTipe;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblKamar;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKapasitas;
    private javax.swing.JTextField txtNoKamar;
    // End of variables declaration//GEN-END:variables
}
