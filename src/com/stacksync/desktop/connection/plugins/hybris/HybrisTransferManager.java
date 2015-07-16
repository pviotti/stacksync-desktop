package com.stacksync.desktop.connection.plugins.hybris;

import com.stacksync.desktop.connection.plugins.AbstractTransferManager;
import com.stacksync.desktop.db.models.CloneWorkspace;
import com.stacksync.desktop.exceptions.LocalFileNotFoundException;
import com.stacksync.desktop.exceptions.RemoteFileNotFoundException;
import com.stacksync.desktop.exceptions.StorageConnectException;
import com.stacksync.desktop.exceptions.StorageException;
import com.stacksync.desktop.exceptions.StorageQuotaExcedeedException;
import com.stacksync.desktop.gui.tray.Tray;
import com.stacksync.desktop.repository.files.RemoteFile;
import com.stacksync.desktop.util.FileUtil;

import fr.eurecom.hybris.Hybris;
import fr.eurecom.hybris.HybrisException;
import fr.eurecom.hybris.kvs.drivers.Kvs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hybris Transfer Manager
 * @author EURECOM
 */
public class HybrisTransferManager extends AbstractTransferManager {

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HybrisTransferManager.class.getName());
    private Hybris hybris;
    private final Tray tray = Tray.getInstance();
    
    public HybrisTransferManager(HybrisConnection connection) {
        super(connection);
        try {
            hybris = new Hybris(connection.getHybrisPropertiesFile());
            logger.info("Hybris initialized.");
            tray.registerProcess(tray.getClass().getSimpleName());
        } catch (HybrisException ex) {
           logger.error(ex);
        }
    } 
    
    @Override
    public void connect() throws StorageConnectException {
        logger.info("Hybris connect (not implemented)");
    }

    @Override
    public void disconnect() throws StorageException {
        //logger.info("Hybris disconnect (not implemented)");
        //hybris.shutdown();
    }

    @Override
    public void download(RemoteFile remoteFile, File localFile, CloneWorkspace workspace) throws RemoteFileNotFoundException, StorageException {
        logger.info("Hybris download " + remoteFile.getName());
        File tempFile = null;
        byte[] is = null;
        
        try {
            is = hybris.get(remoteFile.getName());

            // Save to temp file
            tempFile = config.getCache().createTempFile(remoteFile.getName());
            FileUtil.writeFile(is, tempFile);

            FileUtil.copy(tempFile, localFile);

        } catch (Exception ex) {
            logger.error(ex);
            //RemoteLogs.getInstance().sendLog(ex);
            throw new StorageException("Unable to download file '" + remoteFile.getName(), ex);
        } finally {
                        
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
        
    }

    @Override
    public void upload(File localFile, RemoteFile remoteFile, CloneWorkspace workspace) throws LocalFileNotFoundException, StorageException, StorageQuotaExcedeedException {
        logger.info("Hybris upload " + localFile.getName());
        try {
            List<Kvs> clouds = hybris.put(localFile.getName(), getBytesFromFile(localFile));
            String notifyBody = localFile.getName() + " successfully stored on " + clouds;
            tray.notify("Upload", notifyBody, null);
        } catch (Exception ex) {
            logger.error(ex);
            throw new StorageException(ex);
        }
    }

    @Override
    public void delete(RemoteFile remoteFile, CloneWorkspace workspace) throws RemoteFileNotFoundException, StorageException {
        logger.info("Hybris delete " + remoteFile.getName());
        try {
            hybris.delete(remoteFile.getName());
        } catch (HybrisException ex) {
            logger.error(ex);
            throw new StorageException(ex);
        }
    }

    @Override
    public Map<String, RemoteFile> list(String namePrefix, CloneWorkspace workspace) throws StorageException {
        logger.info("Hybris list");
        try {
            ArrayList<String> objects = (ArrayList<String>) hybris.list();
            Map<String, RemoteFile> list = new HashMap<String, RemoteFile>();
                      
            while(objects.size() > 0){
                for (String obj : objects) {
                    list.put(obj, new RemoteFile(obj, 10, null));
                }
            }
            
            return list;
        } catch (Exception ex) {
            logger.error(ex);
            throw new StorageException(ex);
        }
    }

    @Override
    public void initStorage() throws StorageException {
        logger.info("Hybris initStorage (unsupported)");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getUser() {
        logger.info("Hybris getUser (unsupported)");
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
     public static byte[] getBytesFromFile(File file) throws IOException {
         InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    @Override
    public void upload(File localFile, RemoteFile remoteFile)
            throws LocalFileNotFoundException, StorageException,
            StorageQuotaExcedeedException {
        this.upload(localFile, remoteFile, null);
        
    }

    @Override
    public void delete(RemoteFile remoteFile)
            throws RemoteFileNotFoundException, StorageException {
        this.delete(remoteFile, null);        
    }

    @Override
    public Map<String, RemoteFile> list() throws StorageException {
        return this.list(null, null);
    }

    @Override
    public void download(RemoteFile remoteFile, File localFile)
            throws RemoteFileNotFoundException, StorageException {
        this.download(remoteFile, localFile, null);        
    }
}