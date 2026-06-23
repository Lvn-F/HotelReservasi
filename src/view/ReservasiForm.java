package view; // change to your package name

import config.DBConnection;
import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class ReservasiForm extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    public ReservasiForm() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Reservasi - Hotel Reservasi");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setupTable();
        loadComboTamu();
        loadComboKamar();
        loadData();
        txtNoReservasi.setEditable(false);
        txtNoReservasi.setText("Auto");
        txtTotal.setEditable(false);
    }
    private void loadComboTamu() {
        cmbTamu.removeAllItems();
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT idTamu, nama FROM tamu ORDER BY nama");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbTamu.addItem(
                    rs.getInt("idTamu") + " - " + rs.getString("nama"));
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data tamu!\n" + e.getMessage());
        }
    }
    private void loadComboKamar() {
        cmbKamar.removeAllItems();
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT idKamar, noKamar, tipeKamar, harga " +
                "FROM kamar WHERE status='Available' ORDER BY noKamar");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbKamar.addItem(
                    rs.getInt("idKamar") + " - " +
                    rs.getString("noKamar") + " (" +
                    rs.getString("tipeKamar") + ") - Rp" +
                    rs.getDouble("harga"));
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data kamar!\n" + e.getMessage());
        }
    }
    private void setupTable() {
        tableModel = new DefaultTableModel(
            new String[]{
                "ID", "Tamu", "Kamar",
                "Check In", "Check Out", "Total", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblReservasi.setModel(tableModel);
        tblReservasi.getColumnModel().getColumn(0).setMinWidth(0);
        tblReservasi.getColumnModel().getColumn(0).setMaxWidth(0);
        tblReservasi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblReservasi.getSelectionModel().addListSelectionListener(e -> {
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
                "SELECT r.idReservasi, t.nama, " +
                "k.noKamar, r.tglCheckIn, r.tglCheckOut, " +
                "r.TotalHarga, r.status " +
                "FROM reservasi r " +
                "JOIN tamu t ON r.idTamu = t.idTamu " +
                "JOIN kamar k ON r.idKamar = k.idKamar " +
                "ORDER BY r.tglCheckIn DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("idReservasi"),
                    rs.getString("nama"),
                    rs.getString("noKamar"),
                    rs.getString("tglCheckIn"),
                    rs.getString("tglCheckOut"),
                    rs.getDouble("TotalHarga"),
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
        int row = tblReservasi.getSelectedRow();
        if (row == -1) return;

        // shifted because noReservasi column is gone
        txtTotal.setText(tableModel.getValueAt(row, 5).toString());
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
    }
    private void hitungTotal() {
        try {
            // Get selected kamar harga
            String kamarItem = cmbKamar.getSelectedItem().toString();
            // Format: "id - noKamar (tipe) - RpHARGA"
            double harga = Double.parseDouble(
                kamarItem.split("Rp")[1].trim());

            // Get dates from text fields (format: yyyy-MM-dd)
            String checkInStr = txtCheckIn.getText().trim();
            String checkOutStr = txtCheckOut.getText().trim();

            if (!checkInStr.isEmpty() && !checkOutStr.isEmpty()) {
                LocalDate checkIn = LocalDate.parse(checkInStr);
                LocalDate checkOut = LocalDate.parse(checkOutStr);
                long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

                if (nights > 0) {
                    double total = nights * harga;
                    txtTotal.setText(String.valueOf(total));
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Tanggal check out harus setelah check in!");
                }
            }
        } catch (Exception e) {
            // silently ignore if fields not filled yet
        }
    }
    private boolean validateInput() {
        if (cmbTamu.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih tamu!");
            return false;
        }
        if (cmbKamar.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih kamar!");
            return false;
        }
        if (txtCheckIn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tanggal check in tidak boleh kosong! (Format: yyyy-MM-dd)");
            return false;
        }
        if (txtCheckOut.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tanggal check out tidak boleh kosong! (Format: yyyy-MM-dd)");
            return false;
        }
        if (txtJumlahTamu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Jumlah tamu tidak boleh kosong!");
            return false;
        }
        return true;
    }
    private void clearForm() {
        txtNoReservasi.setText("Auto");
        cmbTamu.setSelectedIndex(0);
        cmbKamar.setSelectedIndex(0);
        txtCheckIn.setText("");
        txtCheckOut.setText("");
        txtJumlahTamu.setText("");
        txtCatatan.setText("");
        txtTotal.setText("");
        cmbStatus.setSelectedIndex(0);
        tblReservasi.clearSelection();
        loadComboKamar(); // refresh available rooms
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservasi = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtNoReservasi = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cmbTamu = new javax.swing.JComboBox<>();
        cmbKamar = new javax.swing.JComboBox<>();
        txtCheckIn = new javax.swing.JTextField();
        txtCheckOut = new javax.swing.JTextField();
        txtJumlahTamu = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtCatatan = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblReservasi.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblReservasi);

        jLabel1.setText("No Reservasi");

        txtNoReservasi.setEditable(false);

        jLabel2.setText("Tamu");

        jLabel3.setText("Kamar");

        jLabel4.setText("Tgl Check In");

        jLabel5.setText("Tgl Check Out");

        jLabel6.setText("Jumlah Tamu");

        jLabel7.setText("Catatan");

        jLabel8.setText("Total Harga");

        jLabel9.setText("Status");

        cmbKamar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        txtCheckOut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCheckOutKeyReleased(evt);
            }
        });

        jScrollPane2.setViewportView(txtCatatan);

        txtTotal.setEditable(false);

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "Confirmed", "CheckedIn", "CheckedOut", "Cancelled" }));

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotal)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbTamu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbKamar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCheckIn)
                            .addComponent(txtCheckOut)
                            .addComponent(txtJumlahTamu)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNoReservasi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(btnSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)))
                .addGap(56, 56, 56))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNoReservasi, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTamu, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJumlahTamu, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus)
                    .addComponent(btnClear))
                .addGap(108, 108, 108))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        if (!validateInput()) return;
        hitungTotal();
        if (txtTotal.getText().isEmpty()) return;

        int idTamu = Integer.parseInt(
            cmbTamu.getSelectedItem().toString().split(" - ")[0]);
        int idKamar = Integer.parseInt(
            cmbKamar.getSelectedItem().toString().split(" - ")[0]);

        try {
            Connection con = DBConnection.getConnection();

            // noReservasi removed, TotalHarga fixed
            String sql =
                "INSERT INTO reservasi (idTamu, idKamar, tglReservasi, " +
                "tglCheckIn, tglCheckOut, jumlahTamu, catatan, " +
                "TotalHarga, status) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idTamu);
            ps.setInt(2, idKamar);
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now())); // tglReservasi = today
            ps.setString(4, txtCheckIn.getText().trim());
            ps.setString(5, txtCheckOut.getText().trim());
            ps.setInt(6, Integer.parseInt(txtJumlahTamu.getText().trim()));
            ps.setString(7, txtCatatan.getText().trim());
            ps.setDouble(8, Double.parseDouble(txtTotal.getText()));
            ps.setString(9, cmbStatus.getSelectedItem().toString());
            ps.executeUpdate();

            // Update kamar status to Occupied
            PreparedStatement psKamar = con.prepareStatement(
                "UPDATE kamar SET status='Occupied' WHERE idKamar=?");
            psKamar.setInt(1, idKamar);
            psKamar.executeUpdate();

            JOptionPane.showMessageDialog(this, "Reservasi berhasil disimpan!");
            clearForm();
            loadData();

            ps.close();
            psKamar.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal menyimpan reservasi!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        int row = tblReservasi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih reservasi yang ingin diedit!");
            return;
        }

        int idReservasi = (int) tableModel.getValueAt(row, 0);

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE reservasi SET status=? WHERE idReservasi=?");
            ps.setString(1, cmbStatus.getSelectedItem().toString());
            ps.setInt(2, idReservasi);
            ps.executeUpdate();

            // If checked out, set kamar back to Available
            if (cmbStatus.getSelectedItem().toString().equals("CheckedOut")) {
                int idKamar = Integer.parseInt(
                    tableModel.getValueAt(row, 3).toString().split(" - ")[0]);
                PreparedStatement psKamar = con.prepareStatement(
                    "UPDATE kamar SET status='Available' WHERE noKamar=?");
                psKamar.setString(1,
                    tableModel.getValueAt(row, 3).toString());
                psKamar.executeUpdate();
                psKamar.close();
            }

            JOptionPane.showMessageDialog(this,
                "Status reservasi berhasil diupdate!");
            clearForm();
            loadData();

            ps.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate reservasi!\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int row = tblReservasi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih reservasi yang ingin dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus reservasi ini?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int idReservasi = (int) tableModel.getValueAt(row, 0);
            try {
                Connection con = DBConnection.getConnection();

                // Set kamar back to Available first
                PreparedStatement psKamar = con.prepareStatement(
                    "UPDATE kamar SET status='Available' " +
                    "WHERE noKamar=?");
                psKamar.setString(1,
                    tableModel.getValueAt(row, 3).toString());
                psKamar.executeUpdate();
                psKamar.close();

                // Delete reservasi
                PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM reservasi WHERE idReservasi=?");
                ps.setInt(1, idReservasi);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Reservasi berhasil dihapus!");
                clearForm();
                loadData();

                ps.close();
                con.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus reservasi!\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtCheckOutKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCheckOutKeyReleased
        hitungTotal();
    }//GEN-LAST:event_txtCheckOutKeyReleased

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
            java.util.logging.Logger.getLogger(ReservasiForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReservasiForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReservasiForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReservasiForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReservasiForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cmbKamar;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> cmbTamu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblReservasi;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtCheckIn;
    private javax.swing.JTextField txtCheckOut;
    private javax.swing.JTextField txtJumlahTamu;
    private javax.swing.JTextField txtNoReservasi;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
