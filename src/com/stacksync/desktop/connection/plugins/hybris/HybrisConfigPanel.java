/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stacksync.desktop.connection.plugins.hybris;

import java.io.File;

import com.stacksync.desktop.connection.plugins.ConfigPanel;
import com.stacksync.desktop.util.FileUtil;

import fr.eurecom.hybris.Config;

/**
 * Hybris configuration panel
 * @author EURECOM
 */
public class HybrisConfigPanel extends ConfigPanel {

	private static final long serialVersionUID = 1L;
	private Config hConf;
	
    public HybrisConfigPanel() {
        super(null);
        initComponents();
        hConf = Config.getInstance();
        load();
    }

    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        spinFaultyClouds = new javax.swing.JSpinner();
        cbEncryption = new javax.swing.JCheckBox();
        cbGarbageCollection = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        cbErasureCoding = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        spinErasureCoding = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtRmdsAdd = new javax.swing.JTextField();
        txtCloudAccountFile = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        cbCaching = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        txtMemcachedAddr = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        comboCachingPolicy = new javax.swing.JComboBox<String>();
        spinCachingExp = new javax.swing.JSpinner();

        jLabel1.setText("Cloud fault tolerance");
        spinFaultyClouds.setValue(1);

        cbEncryption.setText("Encryption");

        cbGarbageCollection.setText("Garbage collection");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Erasure coding"));
        cbErasureCoding.setText("Enabled");
        cbErasureCoding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbErasureCodingActionPerformed(evt);
            }
        });
        jLabel2.setText("k");
        jLabel2.setToolTipText("number of coding chunks");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spinErasureCoding, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(cbErasureCoding)
                    .addGap(0, 0, Short.MAX_VALUE))
            );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbErasureCoding)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinErasureCoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(32, 32, 32))
        );

        jLabel3.setText("RMDS address");
        jLabel4.setText("Cloud account file");
        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	btnBrowseActionPerformed(evt);
            }
        });
        
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Caching"));
        cbCaching.setText("Enabled");
        cbCaching.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	cbCachingActionPerformed(evt);
            }
        });
        jLabel5.setText("Memcached address");
        jLabel6.setText("Expiration period");
        jLabel7.setText("Caching policy");

        comboCachingPolicy.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "on write", "on read" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(cbCaching)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(68, 68, 68)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMemcachedAddr)
                    .addComponent(spinCachingExp)
                    .addComponent(comboCachingPolicy, 0, 145, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(cbCaching)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(txtMemcachedAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addComponent(jLabel6)
                            .addGap(16, 16, 16))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(spinCachingExp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(8, 8, 8)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(comboCachingPolicy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(18, Short.MAX_VALUE))
            );


        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbEncryption)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(spinFaultyClouds, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCloudAccountFile, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtRmdsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbGarbageCollection)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(30, 30, 30)))
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(spinFaultyClouds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtRmdsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cbEncryption)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(btnBrowse)
                            .addComponent(txtCloudAccountFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(cbGarbageCollection)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
            );
    }                   

    private void cbErasureCodingActionPerformed(java.awt.event.ActionEvent evt) {                                           
    	 if (cbErasureCoding.isSelected())
    		spinErasureCoding.setEnabled(true);
         else
         	spinErasureCoding.setEnabled(false);
    }        
    
    private void cbCachingActionPerformed(java.awt.event.ActionEvent evt) {                                           
    	if (cbCaching.isSelected()) {
    		comboCachingPolicy.setEnabled(true);
        	spinCachingExp.setEnabled(true);
        	txtMemcachedAddr.setEnabled(true);
        } else {
        	comboCachingPolicy.setEnabled(false);
        	spinCachingExp.setEnabled(false);
        	txtMemcachedAddr.setEnabled(false);
        }
    } 
    
    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {                                           
    	 File file = FileUtil.showBrowseFileDialog();
         if (file != null && !file.getPath().equals("")) {
             txtCloudAccountFile.setText(file.getAbsolutePath());
         }
    } 

    private javax.swing.JButton btnBrowse;
    private javax.swing.JCheckBox cbEncryption;
    private javax.swing.JCheckBox cbGarbageCollection;
    private javax.swing.JCheckBox cbErasureCoding;
    private javax.swing.JCheckBox cbCaching;
    private javax.swing.JComboBox<String> comboCachingPolicy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner spinFaultyClouds;
    private javax.swing.JSpinner spinErasureCoding;
    private javax.swing.JSpinner spinCachingExp;
    private javax.swing.JTextField txtRmdsAdd;
    private javax.swing.JTextField txtCloudAccountFile;
    private javax.swing.JTextField txtMemcachedAddr;

    @Override
    public void clean() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load() {
        cbCaching.setSelected(Boolean.parseBoolean(hConf.getProperty(Config.CACHE_ENABLED)));
        cbEncryption.setSelected(Boolean.parseBoolean(hConf.getProperty(Config.HS_CRYPTO)));
        cbErasureCoding.setSelected(Boolean.parseBoolean(hConf.getProperty(Config.ECODING)));
        cbGarbageCollection.setSelected(Boolean.parseBoolean(hConf.getProperty(Config.HS_GC)));
        
        spinFaultyClouds.setValue(Integer.parseInt(hConf.getProperty(Config.HS_F)));
        
        if (cbErasureCoding.isSelected())
        	spinErasureCoding.setValue(Integer.parseInt(hConf.getProperty(Config.ECODING_K)));
        else
        	spinErasureCoding.setEnabled(false);
        
        if (cbCaching.isSelected()) {
	        spinCachingExp.setValue(Integer.parseInt(hConf.getProperty(Config.CACHE_EXP)));
	        if ("onwrite".equalsIgnoreCase(hConf.getProperty(Config.CACHE_POLICY)))
	        	comboCachingPolicy.setSelectedItem(0);
	        else if ("onread".equalsIgnoreCase(hConf.getProperty(Config.CACHE_POLICY)))
	        	comboCachingPolicy.setSelectedItem(1);
	        txtMemcachedAddr.setText(hConf.getProperty(Config.CACHE_ADDRESS));  
        } else {
        	comboCachingPolicy.setEnabled(false);
        	spinCachingExp.setEnabled(false);
        	txtMemcachedAddr.setEnabled(false);
        }
        
        txtRmdsAdd.setText(hConf.getProperty(Config.MDS_ADDR));
        txtCloudAccountFile.setText(hConf.getProperty(Config.KVS_ACCOUNTSFILE));
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean check() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}