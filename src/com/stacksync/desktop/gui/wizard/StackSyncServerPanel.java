package com.stacksync.desktop.gui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.stacksync.desktop.Environment;
import com.stacksync.desktop.config.Folder;
import com.stacksync.desktop.config.profile.Account;
import com.stacksync.desktop.config.profile.Profile;
import com.stacksync.desktop.gui.error.ErrorMessage;
import com.stacksync.desktop.gui.settings.SettingsPanel;
import com.stacksync.desktop.util.FileUtil;

public class StackSyncServerPanel extends SettingsPanel {
    
    private String password = "demo1";
    private String email = "demo1@stacksync.org";
	
    public StackSyncServerPanel(Profile profile) {
        this.profile = profile;
        initComponents();	
        
        /// setting text ///                        
        lblEmail.setText(resourceBundle.getString("cp_simple_email"));
        lblPassword.setText(resourceBundle.getString("cp_simple_password"));  
        
        txtEmail.requestFocus();
    }

    @Override
    public void load() {
        txtEmail.setText(this.email);
        txtPassword.setText(this.password);
        
        Folder folder = new Folder(profile);
        folder.setLocalFile(new File(Environment.getInstance().getDefaultUserHome() + "hybris.properties"));
        profile.setFolder(folder);
        txtFolderPath.setText(Environment.getInstance().getDefaultUserHome() + "hybris.properties");
    }

    @Override
    public void save() {
        this.email = getEmail();
        this.password = getPassword();
        
        Account account = profile.getAccount();
        account.setEmail(email);
        account.setPassword(password);
        
        profile.setUseHybris(chkHybris.isSelected());
        profile.setHybrisPropertiesFile(txtFolderPath.getText());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtEmail = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        lblEmail = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        
        chkHybris = new javax.swing.JCheckBox();
        chkHybris.setText("use Hybris");
        chkHybris.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (chkHybris.isSelected()) {
                    btnBrowse.setVisible(true);
                    lblFolderPath.setVisible(true);
                    txtFolderPath.setVisible(true);
                } else {
                    btnBrowse.setVisible(false);
                    lblFolderPath.setVisible(false);
                    txtFolderPath.setVisible(false);
                }
            }
        });
        
        lblFolderPath = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        txtFolderPath = new javax.swing.JTextField();
        btnBrowse.setVisible(false);
        lblFolderPath.setVisible(false);
        txtFolderPath.setVisible(false);

        lblFolderPath.setText("Hybris configuration file:");
        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        txtFolderPath.setEditable(false);
        txtFolderPath.setName("txtFolderPath"); // NOI18N
        
        txtEmail.setName("txtEmail"); // NOI18N

        txtPassword.setName("txtPassword"); // NOI18N
        
        lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblEmail.setText("__Email:");
        lblEmail.setName("lblEmail"); // NOI18N

        lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPassword.setText("__Password:");
        lblPassword.setName("lblPassword"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPassword)
                    .addComponent(lblEmail)
                    .addComponent(lblFolderPath))
                .addGap(72, 72, 72)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(txtEmail)
                    .addComponent(chkHybris)
                    .addComponent(txtFolderPath, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                    .addComponent(btnBrowse))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkHybris))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnBrowse)
                        .addComponent(lblFolderPath)
                        .addComponent(txtFolderPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(96, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JCheckBox chkHybris;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JLabel lblFolderPath;
    private javax.swing.JTextField txtFolderPath;
    // End of variables declaration//GEN-END:variables
    
    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        
        File file = FileUtil.showBrowseFileDialog();
        if (file != null && !file.getPath().equals("")) {
            this.txtFolderPath.setText(file.getAbsolutePath());
        }
    }

    @Override
    public void clean() {
        txtEmail.setText("");
        txtPassword.setText("");
        chkHybris.setSelected(false);
    }

    private String getEmail(){
        return txtEmail.getText().trim();
    }
    
    private String getPassword(){
        return new String(txtPassword.getPassword());
    }
        
    @Override
    public boolean check() {
        String tmpEmail = getEmail();
        String tmpPassword = getPassword();
        
        // check UserName
        if(tmpEmail.isEmpty()){
            ErrorMessage.showMessage(this, "Error", "The email is empty.");
            return false;
        } else{        
            if(!tmpEmail.contains("@") || !tmpEmail.contains(".") || tmpEmail.contains("/")
                    || tmpEmail.contains("\\")){
               ErrorMessage.showMessage(this, "Error", "Invalid email.");
               return false;
            }
        }

        // check Password
        if(tmpPassword.isEmpty()){            
            ErrorMessage.showMessage(this, "Error", "The password is empty.");
            return false;
        }
        
        String filePath = txtFolderPath.getText();
        File f = new File(filePath);            
        if (chkHybris.isSelected() && (!f.exists() || f.isDirectory())) {
            ErrorMessage.showMessage(this, "Error", "You need to select a Hybris configuration file."); 
            return false;
        }

        return true;
    }
}