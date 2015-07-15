package com.stacksync.desktop.connection.plugins.hybris;

import com.stacksync.desktop.config.ConfigNode;
import com.stacksync.desktop.connection.plugins.ConfigPanel;
import com.stacksync.desktop.connection.plugins.Connection;
import com.stacksync.desktop.connection.plugins.PluginInfo;
import com.stacksync.desktop.connection.plugins.Plugins;
import com.stacksync.desktop.connection.plugins.TransferManager;
import com.stacksync.desktop.exceptions.ConfigException;

/**
 *
 * @author EURECOM
 */
public class HybrisConnection implements Connection {

    private static final long serialVersionUID = 1L;
    private String propertiesFile;
    
    public HybrisConnection(String hybrisPropertiesFile) {
        this.propertiesFile = hybrisPropertiesFile;
    }
    
    public HybrisConnection() {
    }
    
    public String getHybrisPropertiesFile() {
        return propertiesFile;
    }
    
    @Override
    public TransferManager createTransferManager() {
        return new HybrisTransferManager(this);
    }

    @Override
    public ConfigPanel createConfigPanel() {
        return new HybrisConfigPanel(this);
    }

    @Override
    public PluginInfo getPluginInfo() {
        return Plugins.get(HybrisPluginInfo.ID);
    }

    @Override
    public String getHost() {
        return "hybris-host";
    }

    @Override
    public String getUsername() {
        return "hybris-user";
    }

    @Override
    public String getPassword() {
         return "hybris-password";
    }

    @Override
    public void load(ConfigNode node) throws ConfigException {
        propertiesFile = node.getProperty("properties");
    }

    @Override
    public void save(ConfigNode node) {
        node.setAttribute("type", getPluginInfo().getId());
        node.setProperty("properties", propertiesFile);
    }
}