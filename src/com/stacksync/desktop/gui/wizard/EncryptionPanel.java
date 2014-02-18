/*
 * Syncany, www.syncany.org
 * Copyright (C) 2011 Philipp C. Heckel <philipp.heckel@gmail.com> 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stacksync.desktop.gui.wizard;

import org.apache.log4j.Logger;
import com.stacksync.desktop.config.Encryption;
import com.stacksync.desktop.config.profile.Profile;
import com.stacksync.desktop.config.Repository;
import com.stacksync.desktop.exceptions.ConfigException;
import com.stacksync.desktop.gui.error.ErrorMessage;
import com.stacksync.desktop.gui.settings.SettingsPanel;
import com.stacksync.desktop.logging.RemoteLogs;

/**
 *
 * @author pheckel
 */
public class EncryptionPanel extends SettingsPanel {

    private final Logger logger = Logger.getLogger(EncryptionPanel.class.getName());

    /**
     * Creates new form EncryptionPanel
     */
    public EncryptionPanel(Profile profile) {
        initComponents();

        pnlAdvancedSettings.setVisible(false);
        this.profile = profile;

        /// setting text ///                
        lblTitle.setText(resourceBundle.getString("encryp_title"));
        lblIntro1.setText(resourceBundle.getString("encryp_intro1"));
        lblIntro2.setText(resourceBundle.getString("encryp_intro2"));
        lblEncryption.setText(resourceBundle.getString("encryp_encryption_type"));
        lblWarning1.setText(resourceBundle.getString("encryp_warning_cse_disabled1"));
        lblWarning2.setText(resourceBundle.getString("encryp_warning_cse_disabled2"));
        lblPassword.setText(resourceBundle.getString("encryp_password"));
        lblPasswordConfirm.setText(resourceBundle.getString("encryp_password_confirm"));
        lblAdvancedSettings.setText(resourceBundle.getString("encryp_advanced_settings"));
        lblKeyLength.setText(resourceBundle.getString("encryp_key_length"));
        lblChunkSize.setText(resourceBundle.getString("encryp_chunk_size"));

        cmbCipher.addItem(resourceBundle.getString("encryp_disabled"));
        cmbCipher.addItem("AES");

        
        setDefaultsValues();
    }

    private void setDefaultsValues() {
        cmbCipher.setSelectedItem(0);

        txtPassword.setText("");
        txtPassword1.setText("");

        cmbKeylength.setSelectedItem("128");
        spnChunksize.setValue(512);

        pnlAdvancedSettings.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAdvancedSettings = new javax.swing.JPanel();
        cmbKeylength = new javax.swing.JComboBox();
        lblKeyLength = new javax.swing.JLabel();
        spnChunksize = new javax.swing.JSpinner();
        lblChunkSize = new javax.swing.JLabel();
        lblPasswordConfirm = new javax.swing.JLabel();
        txtPassword1 = new javax.swing.JPasswordField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblAdvancedSettings = new javax.swing.JLabel();
        lblIntro2 = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblIntro1 = new javax.swing.JLabel();
        lblEncryption = new javax.swing.JLabel();
        cmbCipher = new javax.swing.JComboBox();
        lblWarning1 = new javax.swing.JLabel();
        lblWarning2 = new javax.swing.JLabel();

        pnlAdvancedSettings.setName("pnlAdvancedSettings"); // NOI18N

        cmbKeylength.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "64", "128", "192", "256" }));
        cmbKeylength.setEnabled(false);
        cmbKeylength.setName("cmbKeylength"); // NOI18N

        lblKeyLength.setText("__Key length:");
        lblKeyLength.setName("lblKeyLength"); // NOI18N

        spnChunksize.setModel(new javax.swing.SpinnerNumberModel(512, 32, 2048, 32));
        spnChunksize.setEnabled(false);
        spnChunksize.setName("spnChunksize"); // NOI18N

        lblChunkSize.setText("__Chunk size (KB):");
        lblChunkSize.setName("lblChunkSize"); // NOI18N

        lblPasswordConfirm.setText("__Confirm:");
        lblPasswordConfirm.setName("lblPasswordConfirm"); // NOI18N

        txtPassword1.setName("txtPassword1"); // NOI18N

        lblPassword.setText("__Password:");
        lblPassword.setName("lblPassword"); // NOI18N

        txtPassword.setName("txtPassword"); // NOI18N

        lblAdvancedSettings.setText("__Advanced Encryption Settings");
        lblAdvancedSettings.setName("lblAdvancedSettings"); // NOI18N

        javax.swing.GroupLayout pnlAdvancedSettingsLayout = new javax.swing.GroupLayout(pnlAdvancedSettings);
        pnlAdvancedSettings.setLayout(pnlAdvancedSettingsLayout);
        pnlAdvancedSettingsLayout.setHorizontalGroup(
            pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAdvancedSettingsLayout.createSequentialGroup()
                .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAdvancedSettingsLayout.createSequentialGroup()
                        .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblKeyLength)
                            .addComponent(lblChunkSize)
                            .addComponent(lblPasswordConfirm)
                            .addComponent(lblPassword))
                        .addGap(37, 37, 37)
                        .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword)
                            .addComponent(txtPassword1)
                            .addGroup(pnlAdvancedSettingsLayout.createSequentialGroup()
                                .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(spnChunksize, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(cmbKeylength, javax.swing.GroupLayout.Alignment.LEADING, 0, 90, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(pnlAdvancedSettingsLayout.createSequentialGroup()
                        .addComponent(lblAdvancedSettings)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlAdvancedSettingsLayout.setVerticalGroup(
            pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAdvancedSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPasswordConfirm)
                    .addComponent(txtPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(lblAdvancedSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbKeylength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKeyLength))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAdvancedSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnChunksize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblChunkSize))
                .addContainerGap())
        );

        lblIntro2.setText("__before it is transmitted and stored in the servers.");
        lblIntro2.setName("lblIntro2"); // NOI18N

        lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | java.awt.Font.BOLD, lblTitle.getFont().getSize()+3));
        lblTitle.setText("__Choose a password to encrypt your data");
        lblTitle.setName("lblTitle"); // NOI18N

        lblIntro1.setText("__Stacksync lets users decide whether to encrypt their data ");
        lblIntro1.setName("lblIntro1"); // NOI18N

        lblEncryption.setText("__Client-side encryption:");
        lblEncryption.setName("lblEncryption"); // NOI18N

        cmbCipher.setName("cmbCipher"); // NOI18N
        cmbCipher.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCipherItemStateChanged(evt);
            }
        });
        cmbCipher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCipherActionPerformed(evt);
            }
        });

        lblWarning1.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        lblWarning1.setText("__Please, be aware that it will be impossible to recover your");
        lblWarning1.setName("lblWarning1"); // NOI18N

        lblWarning2.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        lblWarning2.setText("__password (and then your data) in case you forget it.");
        lblWarning2.setName("lblWarning2"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnlAdvancedSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblEncryption)
                                .addGap(71, 71, 71)
                                .addComponent(cmbCipher, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(49, 49, 49))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIntro1)
                            .addComponent(lblIntro2)
                            .addComponent(lblWarning1)
                            .addComponent(lblWarning2))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblIntro1)
                .addGap(4, 4, 4)
                .addComponent(lblIntro2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEncryption)
                    .addComponent(cmbCipher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblWarning1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblWarning2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAdvancedSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbCipherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCipherItemStateChanged
        int indexItem = cmbCipher.getSelectedIndex();

        if (indexItem == 0) {
            //disabled
            pnlAdvancedSettings.setVisible(false);
            lblWarning1.setText(resourceBundle.getString("encryp_warning_cse_disabled1"));
            lblWarning2.setText(resourceBundle.getString("encryp_warning_cse_disabled2"));
        } else {
            //enabled
            pnlAdvancedSettings.setVisible(true);
            lblWarning1.setText(resourceBundle.getString("encryp_warning_cse_enabled1"));
            lblWarning2.setText(resourceBundle.getString("encryp_warning_cse_enabled2"));

            cmbKeylength.setSelectedItem("128");
        }
    }//GEN-LAST:event_cmbCipherItemStateChanged

    private void cmbCipherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCipherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCipherActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbCipher;
    private javax.swing.JComboBox cmbKeylength;
    private javax.swing.JLabel lblAdvancedSettings;
    private javax.swing.JLabel lblChunkSize;
    private javax.swing.JLabel lblEncryption;
    private javax.swing.JLabel lblIntro1;
    private javax.swing.JLabel lblIntro2;
    private javax.swing.JLabel lblKeyLength;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPasswordConfirm;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWarning1;
    private javax.swing.JLabel lblWarning2;
    private javax.swing.JPanel pnlAdvancedSettings;
    private javax.swing.JSpinner spnChunksize;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPassword1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void load() {
    }

    @Override
    public void save() {
        // Encryption
        Encryption encryption = new Encryption();

        encryption.setPassword(new String(txtPassword.getPassword()));
        encryption.setKeylength(Integer.parseInt(cmbKeylength.getSelectedItem().toString()));
        String cipherStr = "none";
        if (cmbCipher.getSelectedIndex() != 0) {
            cipherStr = "aes";
        }
        encryption.setCipherStr(cipherStr);

        try {
            encryption.init(); // TODO do this differently
        } catch (ConfigException ex) {
            logger.error(ex);
            RemoteLogs.getInstance().sendLog(ex);
        }

        // Repo
        Repository repository = profile.getRepository();

        repository.setChunkSize((Integer) spnChunksize.getValue());
        repository.setEncryption(encryption);
    }

    @Override
    public void clean() {
        setDefaultsValues();
    }

    @Override
    public boolean check() {
        boolean check = true;

        if (cmbCipher.getSelectedIndex() > 0) {

            String password = new String(txtPassword.getPassword());
            String password1 = new String(txtPassword1.getPassword());

            if (password.isEmpty() || password1.isEmpty()) {
                ErrorMessage.showMessage(this, "Error", "The password is empty.");
                check = false;
            }

            if (password.compareTo(password1) != 0) {
                ErrorMessage.showMessage(this, "Error", "The passwords do not match.");
                check = false;
            }

            // TODO check the password length

            Encryption encryption = new Encryption();
            encryption.setPassword(new String(txtPassword.getPassword()));
            encryption.setKeylength(Integer.parseInt(cmbKeylength.getSelectedItem().toString()));
            encryption.setCipherStr(cmbCipher.getSelectedItem().toString());

            try {
                encryption.init(); // TODO do this differently
            } catch (ConfigException ex) {
                ErrorMessage.showMessage(this, "Error", ex.getMessage());
                check = false;
            }
        }

        return check;
    }
}
