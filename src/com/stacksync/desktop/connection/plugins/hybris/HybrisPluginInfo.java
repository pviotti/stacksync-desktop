package com.stacksync.desktop.connection.plugins.hybris;

import com.stacksync.desktop.config.Config;
import com.stacksync.desktop.connection.plugins.Connection;
import com.stacksync.desktop.connection.plugins.PluginInfo;
import java.util.ResourceBundle;

/**
 * Hybris plugin info class.
 * @author EURECOM
 */
public class HybrisPluginInfo extends PluginInfo {

    public static final String ID = "hybris";
    private ResourceBundle resourceBundle;
    
    public HybrisPluginInfo() { 
        resourceBundle = Config.getInstance().getResourceBundle();
    }
    
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return resourceBundle.getString("hybris_name");
    }

    @Override
    public Integer[] getVersion() {
        return new Integer[] { 1, 0 };
    }

    @Override
    public String getDescripton() {
        return resourceBundle.getString("hybris_description");
    }

    @Override
    public Connection createConnection() {
        return new HybrisConnection();
    }
}