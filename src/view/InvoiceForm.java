package view; // change to your package name

import config.DBConnection;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class InvoiceForm extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    public InvoiceForm() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Invoice - Hotel Reservasi");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setupTable();
        loadData();
        txtDetail.setEditable(false);
    }
    private void setupTable() {
        tableModel = new DefaultTableModel(
            new String[]{
                "ID", "No Invoice", "Tamu", "Kamar",
                "Tgl Invoice", "Total", "Metode", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblInvoice.setModel(tableModel);
        tblInvoice.getColumnModel().getColumn(0).setMinWidth(0);
        tblInvoice.getColumnModel().getColumn(0).setMaxWidth(0);
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When row clicked, show detail and fill status
        tblInvoice.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
                showDetail();
            }
        });
    }
    private void loadData() {
        loadDataWithFilter("");
    }
    private void loadDataWithFilter(String keyword) {
        tableModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String sql =
                "SELECT i.idInvoice, i.noInvoice, t.nama, " +
                "k.noKamar, i.tglInvoice, i.total, " +
                "p.metode, i.status " +
                "FROM invoice i " +
                "JOIN pembayaran p ON i.idPembayaran = p.idPembayaran " +
                "JOIN reservasi r ON p.idReservasi = r.idReservasi " +
                "JOIN tamu t ON r.idTamu = t.idTamu " +
                "JOIN kamar k ON r.idKamar = k.idKamar " +
                "WHERE i.noInvoice LIKE ? OR t.nama LIKE ? " +
                "ORDER BY i.tglInvoice DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("idInvoice"),
                    rs.getString("noInvoice"),
                    rs.getString("nama"),
                    rs.getString("noKamar"),
                    rs.getString("tglInvoice"),
                    rs.getDouble("total"),
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
        int row = tblInvoice.getSelectedRow();
        if (row == -1) return;
        cmbStatus.setSelectedItem(
            tableModel.getValueAt(row, 7).toString());
    }
    private void showDetail() {
        int row = tblInvoice.getSelectedRow();
        if (row == -1) return;

        int idInvoice = (int) tableModel.getValueAt(row, 0);

        try {
            Connection con = DBConnection.getConnection();
            String sql =
                "SELECT i.noInvoice, i.tglInvoice, i.total, " +
                "i.status, p.metode, p.tglBayar, " +
                "t.nama, t.alamat, t.noHP, t.email, " +
                "k.noKamar, k.tipeKamar, k.harga, " +
                "r.tglCheckIn, r.tglCheckOut, r.jumlahTamu, " +
                "r.catatan, r.TotalHarga " +
                "FROM invoice i " +
                "JOIN pembayaran p ON i.idPembayaran = p.idPembayaran " +
                "JOIN reservasi r ON p.idReservasi = r.idReservasi " +
                "JOIN tamu t ON r.idTamu = t.idTamu " +
                "JOIN kamar k ON r.idKamar = k.idKamar " +
                "WHERE i.idInvoice=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idInvoice);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder detail = new StringBuilder();
                detail.append("================================\n");
                detail.append("         HOTEL RESERVASI        \n");
                detail.append("================================\n");
                detail.append("No Invoice  : ").append(rs.getString("noInvoice")).append("\n");
                detail.append("Tgl Invoice : ").append(rs.getString("tglInvoice")).append("\n");
                detail.append("Status      : ").append(rs.getString("status")).append("\n");
                detail.append("--------------------------------\n");
                detail.append("DETAIL TAMU\n");
                detail.append("Nama        : ").append(rs.getString("nama")).append("\n");
                detail.append("Alamat      : ").append(rs.getString("alamat")).append("\n");
                detail.append("No HP       : ").append(rs.getString("noHP")).append("\n");
                detail.append("Email       : ").append(rs.getString("email")).append("\n");
                detail.append("--------------------------------\n");
                detail.append("DETAIL KAMAR\n");
                detail.append("No Kamar    : ").append(rs.getString("noKamar")).append("\n");
                detail.append("Tipe        : ").append(rs.getString("tipeKamar")).append("\n");
                detail.append("Harga/malam : Rp").append(rs.getDouble("harga")).append("\n");
                detail.append("Check In    : ").append(rs.getString("tglCheckIn")).append("\n");
                detail.append("Check Out   : ").append(rs.getString("tglCheckOut")).append("\n");
                detail.append("Jml Tamu    : ").append(rs.getInt("jumlahTamu")).append("\n");
                detail.append("Catatan     : ").append(rs.getString("catatan") != null ?
                    rs.getString("catatan") : "-").append("\n");
                detail.append("--------------------------------\n");
                detail.append("PEMBAYARAN\n");
                detail.append("Metode      : ").append(rs.getString("metode")).append("\n");
                detail.append("Tgl Bayar   : ").append(rs.getString("tglBayar")).append("\n");
                detail.append("--------------------------------\n");
                detail.append("TOTAL       : Rp").append(rs.getDouble("TotalHarga")).append("\n");
                detail.append("================================\n");

                txtDetail.setText(detail.toString());
                txtDetail.setCaretPosition(0); // txtDetail to top
            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat detail!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tblInvoice = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        btnUpdateStatus = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();
        txtDetail = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblInvoice.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblInvoice);

        txtSearch.setPreferredSize(new java.awt.Dimension(73, 24));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });

        btnSearch.setText("Cari");
        btnSearch.setMinimumSize(new java.awt.Dimension(75, 24));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jLabel1.setText("Status");
        jLabel1.setPreferredSize(new java.awt.Dimension(32, 24));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Generated", "Printed", "Sent\t" }));

        btnUpdateStatus.setText("Update");
        btnUpdateStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateStatusActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        txtDetail.setColumns(20);
        txtDetail.setRows(5);
        scroll.setViewportView(txtDetail);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 609, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnUpdateStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnClear))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(103, 103, 103))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(94, 94, 94))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdateStatus)
                    .addComponent(btnClear))
                .addGap(31, 31, 31))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadDataWithFilter(txtSearch.getText().trim());
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnSearchActionPerformed(null);
        }
    }//GEN-LAST:event_txtSearchKeyPressed

    private void btnUpdateStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusActionPerformed
        int row = tblInvoice.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih invoice yang ingin diupdate!");
            return;
        }

        int idInvoice = (int) tableModel.getValueAt(row, 0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE invoice SET status=? WHERE idInvoice=?");
            ps.setString(1, cmbStatus.getSelectedItem().toString());
            ps.setInt(2, idInvoice);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Status invoice berhasil diupdate!");
            loadData();
            txtDetail.setText("");

            ps.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate status!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateStatusActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtSearch.setText("");
        txtDetail.setText("");
        cmbStatus.setSelectedIndex(0);
        tblInvoice.clearSelection();
        loadData();
    }//GEN-LAST:event_btnClearActionPerformed

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
            java.util.logging.Logger.getLogger(InvoiceForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InvoiceForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InvoiceForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InvoiceForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InvoiceForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdateStatus;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblInvoice;
    private javax.swing.JTextArea txtDetail;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
