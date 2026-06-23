package view; // change to your package name

import config.DBConnection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PembayaranForm extends javax.swing.JFrame {

    private DefaultTableModel tableModel;
    public PembayaranForm() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Pembayaran - Hotel Reservasi");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setupTable();
        loadComboReservasi();
        loadData();
        // Set default tanggal bayar to today
        txtTglBayar.setText(LocalDate.now().toString());
        txtJumlah.setEditable(false);
    }
    private void loadComboReservasi() {
        cmbReservasi.removeAllItems();
        try {
            Connection con = DBConnection.getConnection();
            String sql =
                "SELECT r.idReservasi, t.nama, k.noKamar, r.TotalHarga " +
                "FROM reservasi r " +
                "JOIN tamu t ON r.idTamu = t.idTamu " +
                "JOIN kamar k ON r.idKamar = k.idKamar " +
                "WHERE r.status IN ('Confirmed','CheckedIn') " +
                "AND r.idReservasi NOT IN " +
                "(SELECT idReservasi FROM pembayaran) " +
                "ORDER BY t.nama";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbReservasi.addItem(
                    rs.getInt("idReservasi") + " | " +
                    rs.getString("nama") + " - " +
                    rs.getString("noKamar") + " | Rp" +
                    rs.getDouble("TotalHarga"));
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data reservasi!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void setupTable() {
        tableModel = new DefaultTableModel(
            new String[]{
                "ID", "Reservasi", "Tgl Bayar",
                "Jumlah", "Metode", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblPembayaran.setModel(tableModel);
        tblPembayaran.getColumnModel().getColumn(0).setMinWidth(0);
        tblPembayaran.getColumnModel().getColumn(0).setMaxWidth(0);
        tblPembayaran.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblPembayaran.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }
    private void loadData() {
        tableModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String sql =
                "SELECT p.idPembayaran, " +
                "CONCAT(t.nama, ' - ', k.noKamar) as reservasiInfo, " +
                "p.tglBayar, p.jumlahBayar, p.metode, p.status " +
                "FROM pembayaran p " +
                "JOIN reservasi r ON p.idReservasi = r.idReservasi " +
                "JOIN tamu t ON r.idTamu = t.idTamu " +
                "JOIN kamar k ON r.idKamar = k.idKamar " +
                "ORDER BY p.tglBayar DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("idPembayaran"),
                    rs.getString("reservasiInfo"),
                    rs.getString("tglBayar"),
                    rs.getDouble("jumlahBayar"),
                    rs.getString("metode"),
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
        int row = tblPembayaran.getSelectedRow();
        if (row == -1) return;
        txtTglBayar.setText(tableModel.getValueAt(row, 2).toString());
        txtJumlah.setText(tableModel.getValueAt(row, 3).toString());
        cmbMetode.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5).toString());
    }
    private String generateNoInvoice() {
        String date = new SimpleDateFormat("yyyyMMdd")
            .format(new java.util.Date());
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM invoice WHERE noInvoice LIKE ?");
            ps.setString(1, "INV-" + date + "%");
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1) + 1;
            rs.close();
            ps.close();
            con.close();
            return String.format("INV-%s-%03d", date, count);
        } catch (SQLException e) {
            return "INV-" + date + "-001";
        }
    }
    private boolean validateInput() {
        if (cmbReservasi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Pilih reservasi!");
            return false;
        }
        if (txtTglBayar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tanggal bayar tidak boleh kosong! (Format: yyyy-MM-dd)");
            return false;
        }
        if (txtJumlah.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Jumlah tidak boleh kosong!");
            return false;
        }
        return true;
    }
    private void clearForm() {
        loadComboReservasi();
        txtTglBayar.setText(LocalDate.now().toString());
        txtJumlah.setText("");
        cmbMetode.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        tblPembayaran.clearSelection();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblPembayaran = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbReservasi = new javax.swing.JComboBox<>();
        txtTglBayar = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        cmbMetode = new javax.swing.JComboBox<>();
        cmbStatus = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblPembayaran.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblPembayaran);

        jLabel2.setText("Reservasi");

        jLabel3.setText("Tgl Bayar");

        jLabel4.setText("Jumlah");

        jLabel5.setText("Metode");

        jLabel6.setText("Status");

        cmbReservasi.setPreferredSize(new java.awt.Dimension(72, 24));
        cmbReservasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbReservasiActionPerformed(evt);
            }
        });

        txtTglBayar.setText("yyyy-mm-dd");
        txtTglBayar.setMinimumSize(new java.awt.Dimension(64, 24));

        txtJumlah.setEditable(false);
        txtJumlah.setMinimumSize(new java.awt.Dimension(64, 24));

        cmbMetode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "Transfer", "Credit Card", "Debit" }));
        cmbMetode.setPreferredSize(new java.awt.Dimension(72, 24));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "Paid", "Refunded" }));
        cmbStatus.setPreferredSize(new java.awt.Dimension(72, 24));

        btnSimpan.setText("Simpan & Buat Invoice");
        btnSimpan.setPreferredSize(new java.awt.Dimension(75, 24));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.setPreferredSize(new java.awt.Dimension(75, 24));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.setPreferredSize(new java.awt.Dimension(75, 24));
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.setPreferredSize(new java.awt.Dimension(75, 24));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtJumlah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtTglBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbMetode, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbReservasi, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(105, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(189, 189, 189)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbReservasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTglBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMetode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        if (!validateInput()) return;

        int idReservasi = Integer.parseInt(
            cmbReservasi.getSelectedItem().toString().split(" \\| ")[0].trim());

        try {
            Connection con = DBConnection.getConnection();

            // 1. Insert pembayaran
            String sqlBayar =
                "INSERT INTO pembayaran " +
                "(idReservasi, tglBayar, jumlahBayar, metode, status) " +
                "VALUES (?,?,?,?,?)";
            PreparedStatement psBayar = con.prepareStatement(
                sqlBayar, Statement.RETURN_GENERATED_KEYS);
            psBayar.setInt(1, idReservasi);
            psBayar.setString(2, txtTglBayar.getText().trim());
            psBayar.setDouble(3, Double.parseDouble(txtJumlah.getText().trim()));
            psBayar.setString(4, cmbMetode.getSelectedItem().toString());
            psBayar.setString(5, cmbStatus.getSelectedItem().toString());
            psBayar.executeUpdate();

            // Get generated idPembayaran
            ResultSet generatedKeys = psBayar.getGeneratedKeys();
            int idPembayaran = 0;
            if (generatedKeys.next()) {
                idPembayaran = generatedKeys.getInt(1);
            }

            // 2. Auto-create invoice
            String noInvoice = generateNoInvoice();
            String sqlInvoice =
                "INSERT INTO invoice " +
                "(idPembayaran, noInvoice, tglInvoice, total, status) " +
                "VALUES (?,?,?,?,?)";
            PreparedStatement psInvoice = con.prepareStatement(sqlInvoice);
            psInvoice.setInt(1, idPembayaran);
            psInvoice.setString(2, noInvoice);
            psInvoice.setString(3, LocalDate.now().toString());
            psInvoice.setDouble(4,
                Double.parseDouble(txtJumlah.getText().trim()));
            psInvoice.setString(5, "Generated");
            psInvoice.executeUpdate();

            // 3. Update reservasi status to CheckedOut
            PreparedStatement psRes = con.prepareStatement(
                "UPDATE reservasi SET status='CheckedOut' " +
                "WHERE idReservasi=?");
            psRes.setInt(1, idReservasi);
            psRes.executeUpdate();

            // 4. Set kamar back to Available
            PreparedStatement psKamar = con.prepareStatement(
                "UPDATE kamar SET status='Available' " +
                "WHERE idKamar = " +
                "(SELECT idKamar FROM reservasi WHERE idReservasi=?)");
            psKamar.setInt(1, idReservasi);
            psKamar.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Pembayaran berhasil disimpan!\n" +
                "Invoice " + noInvoice + " telah dibuat.");
            clearForm();
            loadData();

            psBayar.close();
            psInvoice.close();
            psRes.close();
            psKamar.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal menyimpan pembayaran!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        int row = tblPembayaran.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih pembayaran yang ingin diedit!");
            return;
        }

        int idPembayaran = (int) tableModel.getValueAt(row, 0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE pembayaran SET status=? WHERE idPembayaran=?");
            ps.setString(1, cmbStatus.getSelectedItem().toString());
            ps.setInt(2, idPembayaran);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Status pembayaran berhasil diupdate!");
            clearForm();
            loadData();

            ps.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate pembayaran!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int row = tblPembayaran.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih pembayaran yang ingin dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus pembayaran ini?\n" +
            "Invoice terkait juga akan dihapus!",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int idPembayaran = (int) tableModel.getValueAt(row, 0);
            try {
                Connection con = DBConnection.getConnection();

                // Delete invoice first (FK constraint)
                PreparedStatement psInv = con.prepareStatement(
                    "DELETE FROM invoice WHERE idPembayaran=?");
                psInv.setInt(1, idPembayaran);
                psInv.executeUpdate();
                psInv.close();

                // Then delete pembayaran
                PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM pembayaran WHERE idPembayaran=?");
                ps.setInt(1, idPembayaran);
                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this,
                    "Pembayaran berhasil dihapus!");
                clearForm();
                loadData();

                con.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus pembayaran!\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void cmbReservasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbReservasiActionPerformed
        if (cmbReservasi.getSelectedItem() == null) return;
        try {
            // Format: "id | nama - noKamar | RpTOTAL"
            String selected = cmbReservasi.getSelectedItem().toString();
            String totalStr = selected.split("Rp")[1].trim();
            txtJumlah.setText(totalStr);
        } catch (Exception e) {
            txtJumlah.setText("");
        }
    }//GEN-LAST:event_cmbReservasiActionPerformed

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
            java.util.logging.Logger.getLogger(PembayaranForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PembayaranForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PembayaranForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PembayaranForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PembayaranForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cmbMetode;
    private javax.swing.JComboBox<String> cmbReservasi;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPembayaran;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtTglBayar;
    // End of variables declaration//GEN-END:variables
}
