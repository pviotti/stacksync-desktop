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
package com.stacksync.desktop.watch.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import com.stacksync.desktop.Constants;
import com.stacksync.desktop.config.Config;
import com.stacksync.desktop.config.Folder;
import com.stacksync.desktop.config.profile.Profile;
import com.stacksync.desktop.connection.plugins.TransferManager;
import com.stacksync.desktop.db.DatabaseHelper;
import com.stacksync.desktop.db.models.CloneChunk;
import com.stacksync.desktop.db.models.CloneChunk.CacheStatus;
import com.stacksync.desktop.db.models.CloneFile;
import com.stacksync.desktop.db.models.CloneFile.Status;
import com.stacksync.desktop.db.models.CloneFile.SyncStatus;
import com.stacksync.desktop.exceptions.CouldNotApplyUpdateException;
import com.stacksync.desktop.exceptions.StorageException;
import com.stacksync.desktop.gui.server.Desktop;
import com.stacksync.desktop.gui.tray.Tray;
import com.stacksync.desktop.chunker.Chunker;
import com.stacksync.desktop.chunker.ChunkEnumeration;
import com.stacksync.desktop.db.models.CloneWorkspace;
import com.stacksync.desktop.logging.RemoteLogs;
import com.stacksync.desktop.repository.Update;
import com.stacksync.desktop.repository.Uploader;
import com.stacksync.desktop.repository.files.RemoteFile;
import com.stacksync.desktop.util.FileUtil;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Philipp C. Heckel
 */
public class ChangeManager {
    
    private final Logger logger = Logger.getLogger(ChangeManager.class.getName());
    private final Chunker chunker = new Chunker();
    
    private final Config config =  Config.getInstance();
    private final DatabaseHelper db = DatabaseHelper.getInstance();
    private final Tray tray = Tray.getInstance();
    
    private final int INTERVAL = 5000;    
    
    // cp start()
    private final LinkedBlockingQueue<Update> queue;
    private boolean processingFiles;
    private Profile profile;
    private Timer timer;
    private TransferManager transfer;
    private EntityManager em;
    
    // deps
    private Desktop desktop;
    private Uploader uploader;

    public ChangeManager(Profile profile) {

        this.profile = profile;
        this.queue = new LinkedBlockingQueue<Update>();
        this.processingFiles = false;

        // cmp. start()
        this.timer = null;
    }

    public synchronized void start() {
        
        em = config.getDatabase().getEntityManager();
        // Dependencies
        this.desktop = Desktop.getInstance();
        this.tray.registerProcess(this.getClass().getSimpleName());

        if (timer != null) {
            return;
        }
        
        transfer = profile.getRepository().getConnection().createTransferManager();
        uploader = profile.getUploader();

        timer = new Timer("ChangeMgr");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doProcessUpdates();
            }
        }, 0, INTERVAL);
    }

    public synchronized void stop() {
        if (timer == null) {
            return;
        }

        timer.cancel();
        timer = null;
    }
    
    public void queueUpdates(List<Update> ul){
        synchronized (queue) {
            queue.addAll(ul);
        }
    }
    
    private void doProcessUpdates() {
        // Note: Don't do this in the init method.
        //       This MUST happen in this thread!
        
        Update update = null;
        Map<Long, List<Update>> newUpdatesMap = new HashMap<Long, List<Update>>();
        Folder root;

        synchronized (queue) {
            this.processingFiles = true;
            if (!queue.isEmpty()) {
                tray.setStatusIcon(this.getClass().getSimpleName(), Tray.StatusIcon.UPDATING);
            }
        }

        while (null != (update = queue.poll())) {
            tray.setStatusText(this.getClass().getSimpleName(), "Changing " + (queue.size() + 1) +  " files...");
            logger.info("Processing update " + update);                    

            CloneFile existingVersion = db.getFileOrFolder(update.getFileId(), update.getVersion());            
            boolean isLocalConflict = isLocalConflict(existingVersion, update) | update.getConflicted();
            
            ///Existing version equals update -> skip: file is up-to-date!
            // TODO: I think that this is not necessary
            if (existingVersion != null && !isLocalConflict && (existingVersion.getSyncStatus()== SyncStatus.UPTODATE)) {
                logger.info("File " + update.getName() + "-" + update.getFileId() + ", version " + update.getVersion() + " is UP-TO-DATE. ");
                
                if(!existingVersion.getServerUploadedAck()){
                    existingVersion.setServerUploadedAck(true);
                    existingVersion.merge();
                }
                
                continue;
            }
            
            logger.info("Processing update: " + update);
            if (!newUpdatesMap.containsKey(update.getFileId())) {
                newUpdatesMap.put(update.getFileId(), new ArrayList<Update>());
            }

            newUpdatesMap.get(update.getFileId()).add(update);

            ///// 3. Handle all possible cases
            CloneFile localVersionById = db.getFileOrFolder(update.getFileId());
            
            if (localVersionById != null) { // A) I know the file ID
                if (isLocalConflict) { /// a) Conflict exists
                    logger.info("Aa) File ID " + update.getFileId() + " known, conflict found of " + existingVersion + " with updates " + update + ". Resolving conflict ...");
                    
                    if(update.isFolder()){
                        resolveFolderConflict(existingVersion, update);
                    } else {
                        try{
                            resolveConflict(existingVersion, update);
                        } catch (CouldNotApplyUpdateException ex) {
                            logger.error("Unable to download/assemble winning file!", ex);
                            RemoteLogs.getInstance().sendLog(ex);
                            // TODO Inifinite loop??
                            queue.add(update);
                        }
                    }

                } else { /// b) No conflict exists (only apply new versions)
                    logger.info("Ab) File ID " + update.getFileId() + " known. New update found. Applying ...");
                    applyUpdate(localVersionById, update);
                }
            } else { // B) I do not know the file ID

                root = profile.getFolder();

                String path = root.getLocalFile().getAbsolutePath() + File.separator;
                if (update.getParentFileId() != null) {
                    CloneFile parentCF = db.getFileOrFolder(update.getParentFileId());
                    path += parentCF.getPath() + File.separator;
                    path += parentCF.getName() + File.separator;
                } else if (!update.getWorkspace().getId().equals(db.getDefaultWorkspace().getId())) {
                    CloneFile parentCF = db.getWorkspaceRoot(update.getWorkspace().getId());
                    path += parentCF.getPath() + File.separator;
                    path += parentCF.getName() + File.separator;
                }
                path += update.getName();
                
                File localFileName = FileUtil.getCanonicalFile(new File(path));
                CloneFile localVersionByFilename = db.getFileOrFolder(root, localFileName); //update.getRootId(), update.getPath(), update.getName());

                // a) No local file (in DB) exists: This one must be new!
                if (localVersionByFilename == null) {
                    logger.info("Ba) File ID " + update.getFileId() + " NOT known. No conflicting filename found in DB. Applying updates of new file ...");
                    applyUpdate(null, update);
                } else { // b) Local file exists:
                    logger.info("Bb) File ID " + update.getFileId() + " NOT known. Conflicting file (same file path) FOUND in DB: " + localVersionByFilename);
                    
                    if(update.isFolder()){
                        resolveFolderConflict(localVersionByFilename, update);
                    } else {
                        
                        if(localVersionByFilename.getServerUploadedAck()){
                            try{
                                resolveConflict(localVersionByFilename, update);
                            } catch (CouldNotApplyUpdateException ex) {
                                logger.error("Unable to download/assemble winning file!", ex);
                                RemoteLogs.getInstance().sendLog(ex);
                                // TODO Inifinite loop??
                                queue.add(update);
                            }
                        } else{ 
                           List<CloneFile> previusVersions = localVersionByFilename.getPreviousVersions();
                            previusVersions.add(localVersionByFilename);
                            
                            CloneFile firstConflictVersion = null;
                            
                            for(CloneFile cf: previusVersions){
                                if(!cf.getServerUploadedAck()){ // set the first conflict version
                                    firstConflictVersion = cf;
                                    break;
                                }
                            }
                            
                            if(firstConflictVersion != null){
                                try{
                                    resolveConflict(localVersionByFilename, update);
                                } catch (CouldNotApplyUpdateException ex) {
                                    // TODO Inifinite loop??
                                    logger.error("Unable to download/assemble winning file!", ex);
                                    RemoteLogs.getInstance().sendLog(ex);
                                    queue.add(update);
                                }
                            }                            
                        }                        
                    }                                        
                }
            }
        }
        
        synchronized (queue) {
            this.processingFiles = false;
        }

        // Q empty!!
        tray.setStatusIcon(this.getClass().getSimpleName(), Tray.StatusIcon.UPTODATE);
        tray.setStatusText(this.getClass().getSimpleName(), "");

        if (!newUpdatesMap.isEmpty()) {
            showNotification(newUpdatesMap);
        }
    }

    private void applyUpdate(CloneFile lastMatchingVersion, Update newFileUpdate) {
    
        if (lastMatchingVersion != null) {
            this.desktop.touch(lastMatchingVersion.getAbsolutePath(), SyncStatus.SYNCING);
        }
        
        try{        
            // b) Rename
            if (newFileUpdate.getStatus() == Status.RENAMED) {
                CloneFile renamedFile = applyRenameOnlyChanges(lastMatchingVersion, newFileUpdate);
                this.desktop.touch(renamedFile.getAbsolutePath(), SyncStatus.UPTODATE);
                if (lastMatchingVersion != null) {
                    this.desktop.untouch(lastMatchingVersion.getAbsolutePath());
                }
                
                return;
            }

            // c) Simply delete the last file
            if (newFileUpdate.getStatus() == Status.DELETED) {
                applyDeleteChanges(lastMatchingVersion, newFileUpdate);
                if (lastMatchingVersion != null) {
                    this.desktop.untouch(lastMatchingVersion.getAbsolutePath());
                }
                return;
            }

            // d) Changed or new
            CloneFile newFile = applyChangeOrNew(lastMatchingVersion, newFileUpdate);
            if (lastMatchingVersion != null) {
                    this.desktop.touch(lastMatchingVersion.getAbsolutePath(), SyncStatus.UPTODATE);
            }
            this.desktop.touch(newFile.getAbsolutePath(), SyncStatus.UPTODATE);

        } catch (CouldNotApplyUpdateException ex) {
            // TODO Inifinite loop??
            logger.error("Warning: could not download/assemble " + newFileUpdate, ex);
            RemoteLogs.getInstance().sendLog(ex);            
            queue.add(newFileUpdate);
            if (lastMatchingVersion != null) {
                this.desktop.touch(lastMatchingVersion.getAbsolutePath(), SyncStatus.UNSYNC);
            }
        } catch (Exception ex) {
            logger.error("Warning: could not download/assemble " + newFileUpdate, ex);
            if (lastMatchingVersion != null) {
                this.desktop.touch(lastMatchingVersion.getAbsolutePath(), SyncStatus.UNSYNC);
            }
        }
    }

    private void resolveFolderConflict(CloneFile firstConflictingVersion, Update conflictUpdate) {
        ///// C. Add updates to DB
        logger.info("resolveFolderConflict: C. Adding updates to DB: " + conflictUpdate);
        CloneFile winningVersion = addToDB(conflictUpdate);
        winningVersion.setStatus(Status.RENAMED);
        
        ///// D. Create 'winning' file
        logger.info("resolveFolderConflict: D. Create/Download winning file ...");
        // TODO what if this is a rename-only history??? 

        File fileOldVersion = new File(firstConflictingVersion.getAbsolutePath());
        FileUtil.renameVia(fileOldVersion, winningVersion.getFile());// just in case!
        
        updateSyncStatus(winningVersion, SyncStatus.UPTODATE);
        em.merge(winningVersion);
    }
    
    private void resolveDeleteConflict(CloneFile firstConflictingVersion, Update conflictUpdate){
        firstConflictingVersion.setChecksum(conflictUpdate.getChecksum());
        
        firstConflictingVersion.setServerUploadedAck(conflictUpdate.getServerUploadedAck());
        firstConflictingVersion.setServerUploadedTime(conflictUpdate.getServerUploadedTime());
        
        firstConflictingVersion.setStatus(conflictUpdate.getStatus());
        firstConflictingVersion.setSyncStatus(SyncStatus.UPTODATE);
        
        firstConflictingVersion.merge();
    }
    
    
    private String generateConflictName(CloneFile conflictFile){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        
        String fileName = FileUtil.getBasename(conflictFile.getName());
        if (fileName.contains("conflicting copy,")) {
            fileName = fileName.substring(0, fileName.indexOf("(")).trim()
                    + FileUtil.getExtension(conflictFile.getName(), true);
        }

        // New filename
        String newFileName = fileName
                + " (" + config.getDeviceName()
                + (config.getDeviceName().endsWith("s") ? "'" : "'s")
                + " conflicting copy, "
                + dateFormat.format(conflictFile.getLastModified())
                + ")" + FileUtil.getExtension(conflictFile.getName(), true);
        
        return newFileName;
    }
    
    private void resolveConflict(CloneFile firstConflictingVersion, Update conflictUpdate) throws CouldNotApplyUpdateException {        
        if(firstConflictingVersion.getStatus().equals(Status.DELETED) && conflictUpdate.getStatus().equals(Status.DELETED)){
            resolveDeleteConflict(firstConflictingVersion, conflictUpdate);
            return;
        }        
        
        ///// A. Adjust local history (first conflicting version - last local version)
        logger.info("resolveConflict: A. Adjusting local history of " + firstConflictingVersion + " ...");

        // I lose! Adjust complete history
        List<CloneFile> nextVersions = firstConflictingVersion.getNextVersions();

        File oldConflictingLocalFile = (nextVersions.isEmpty()) ? firstConflictingVersion.getFile() : nextVersions.get(nextVersions.size() - 1).getFile();
        CloneFile newConflictingLocalFile = null;

        List<CloneFile> versionsToAdjust = new ArrayList<CloneFile>();
        versionsToAdjust.add(firstConflictingVersion);
        versionsToAdjust.addAll(nextVersions);

        // Remove old DB entries, and add new ones
        em.getTransaction().begin();

        long version = 1;
        long fileId = firstConflictingVersion.getNewRandom();        

        String newFileName = generateConflictName(firstConflictingVersion);

        for (CloneFile cf: versionsToAdjust) {
            logger.info("- Adding adjusted version " + cf + " from DB: " + cf.getAbsolutePath() + "");
            
            /// GGIPART /// Solved bug trying remove old version and insert new version            
            CloneFile cfclone = (CloneFile) cf.clone();

            // New file 
            if (version == 1) {
                cfclone.setStatus(Status.NEW);
            } else {
                cfclone.setStatus(Status.CHANGED);
            }
            cfclone.setName(newFileName);
            cfclone.setVersion(version);
            cfclone.setId(fileId);
            cfclone.setUsingTempId(true);
            cfclone.setServerUploadedAck(false);
            cfclone.setSyncStatus(SyncStatus.LOCAL);
            
            newConflictingLocalFile = cfclone;

            CloneFile toBeRemoved = (CloneFile)em.merge(cf);
            toBeRemoved.deleteFromDB();
            em.merge(cfclone);

            /// GGIENDPART ///            
            version++;
        }

        versionsToAdjust.clear();
        em.flush();
        em.clear();
        em.getTransaction().commit();

        ///// B. Rename last local file to 'conflicting copy'
        logger.info("resolveConflict: B. Renaming local file " + oldConflictingLocalFile + " to " + newConflictingLocalFile + "");
        FileUtil.renameVia(oldConflictingLocalFile, newConflictingLocalFile.getFile());

        // Q upload if name is legal
        if (!FileUtil.checkIllegalName(newConflictingLocalFile.getName())) {
            uploader.queue(newConflictingLocalFile);
        }

        ///// C. Add updates to DB	
        logger.info("resolveConflict: C. Adding updates to DB: " + conflictUpdate);
        CloneFile winningVersion = addToDB(conflictUpdate);

        if(winningVersion.getStatus() == Status.DELETED){            
            File tempDeleteFile = new File(winningVersion.getAbsoluteParentDirectory() + "/.ignore-delete-" + winningVersion.getName());
            FileUtil.deleteRecursively(tempDeleteFile); // just in case!

            winningVersion.getFile().renameTo(tempDeleteFile);
            FileUtil.deleteRecursively(tempDeleteFile);            
            
            updateSyncStatus(winningVersion, SyncStatus.UPTODATE);
        } else {
            updateSyncStatus(winningVersion, SyncStatus.REMOTE);
            ///// D. Create 'winning' file
            logger.info("resolveConflict: D. Create/Download winning file ...");
            // TODO what if this is a rename-only history??? 

            File tempWinningFile = new File(winningVersion.getFile().getParentFile().getAbsoluteFile() + File.separator + ".ignore-assemble-to-" + winningVersion.getFile().getName());
            FileUtil.deleteRecursively(tempWinningFile); // just in case!           

            // Download and assemble winning file
            downloadChunks(winningVersion);
            assembleFile(winningVersion, tempWinningFile);
            logger.info("resolveConflict: D2. Rename temp file to " + winningVersion.getFile() + " ...");
            tempWinningFile.renameTo(winningVersion.getFile());

            // Update DB
            updateSyncStatus(winningVersion, SyncStatus.UPTODATE);
        }

        em.merge(winningVersion);
    }

    private CloneFile addToDB(Update newFileUpdate) {        
        CloneFile existingVersion = db.getFileOrFolder(newFileUpdate.getFileId(), newFileUpdate.getVersion());
        if(existingVersion != null){
            logger.info("found clonefile in database " + existingVersion);
            existingVersion.setSyncStatus(SyncStatus.REMOTE);
            existingVersion.merge();
            return existingVersion; //Remote Version
        } else {
            logger.info("creating new clonefile in database.");
            return db.createFile(profile, newFileUpdate);
        }
    }

    private CloneFile updateSyncStatus(CloneFile newFileVersion, SyncStatus syncStatus) {
        newFileVersion.setSyncStatus(syncStatus);                    
        newFileVersion.setMimetype(FileUtil.getMimeType(newFileVersion.getFile()));
        
        newFileVersion.merge();
        return newFileVersion;
    }
    
    private boolean checkChunks(List<CloneChunk> l1, List<String> l2){
        boolean isEqual = true;
        
        if(l1.size() != l2.size()){
            isEqual = false;
        } else {
            for(int i=0; i < l1.size(); i++){
                CloneChunk c1 = l1.get(i);
                String c2 = l2.get(i);
                
                if(c1.getChecksum().compareTo(c2) != 0){
                    isEqual = false;
                    break;
                }
            }
        }
        
        return isEqual;
    }

    /**
     *
     * <p>Note: files and folders are handled the same (in this case!). When
     * updating this method, make sure to check if it works for both!
     *
     * @param lastMatchingVersion
     * @param newFileUpdates
     */
    private CloneFile applyRenameOnlyChanges(CloneFile lastMatchingVersion, Update newFileUpdate) throws CouldNotApplyUpdateException {
        if(lastMatchingVersion == null || !checkChunks(lastMatchingVersion.getChunks(), newFileUpdate.getChunks())){
            if(lastMatchingVersion == null){
                logger.warn("Error lastmatching version is nul");
            } else {
                logger.warn("Error file chunks not matching " + lastMatchingVersion.getId() + "v" + lastMatchingVersion.getVersion() + ": " + lastMatchingVersion.getRelativePath() + "; Trying to download the file ...");
            }

            CloneFile newFile = applyChangeOrNew(lastMatchingVersion, newFileUpdate);
            return newFile;
        }        
        
        if (!lastMatchingVersion.getFile().exists()) {
            logger.warn("Error while renaming file " + lastMatchingVersion.getId() + "v" + lastMatchingVersion.getVersion() + ": " + lastMatchingVersion.getRelativePath() + " does NOT exist; Trying to download the file ...");

            CloneFile newFile = applyChangeOrNew(lastMatchingVersion, newFileUpdate);
            return newFile;
        }                

        ///// A. Add to DB
        CloneFile newestVersion = addToDB(newFileUpdate);

        ///// B. Rename current local version to the last update version
        logger.info("- ChangeManager: Renaming file " + lastMatchingVersion.getFile() + " to " + newestVersion.getFile());

        if (newestVersion.getFile().exists()) {
            logger.warn("- ChangeManager: Unable to rename file. " + newestVersion.getFile() + " already exists.");
            logger.warn("TODO TODO TODO what do we do in this case???");
            
            //si esta renombrado comprobar el checksum i sino coincide aplicar changeornew
            //checkear si checksum coincide                    
            //si existe el temp-rename eliminar
            CloneFile newFile = applyChangeOrNew(lastMatchingVersion, newFileUpdate);
            return newFile; 
            
            //return newestVersion;
        }

        /// Do rename!
        File tempFile = new File(newestVersion.getAbsoluteParentDirectory() + "/.ignore-rename-to-" + newestVersion.getName());
        FileUtil.deleteRecursively(tempFile); // just in case!

        // No difference between folder and file !
        if (!lastMatchingVersion.getFile().renameTo(tempFile)) {
            logger.warn("ChangeManager Renaming NOT successful: from " + lastMatchingVersion.getFile() + " to " + tempFile + "");
            throw new CouldNotApplyUpdateException(new Exception("Renaming NOT successful"));
        }

        tempFile.setLastModified(lastMatchingVersion.getLastModified().getTime());

        if (!tempFile.renameTo(newestVersion.getFile())) {
            logger.warn("ChangeManager Renaming NOT successful: from " + tempFile + " to " + newestVersion.getFile() + "");
            throw new CouldNotApplyUpdateException(new Exception("Renaming NOT successful"));
        }

        // Update DB
        updateSyncStatus(newestVersion, SyncStatus.UPTODATE);
        em.merge(newestVersion);
        return newestVersion;
    }

    /**
     *
     * <p>Note: files and folders are handled the same (in this case!). When
     * updating this method, make sure to check if it works for both!
     *
     * @param lastMatchingVersion
     * @param newFileUpdates
     */
    private void applyDeleteChanges(CloneFile lastMatchingVersion, Update newFileUpdate) {
        logger.info("Deleting " + newFileUpdate.getName() + "");

        ///// A. Add to DB
        CloneFile deletedVersion = addToDB(newFileUpdate);
        
        ///// B. Delete newest local file
        File fileToDelete;
        if (lastMatchingVersion == null) {
            fileToDelete = deletedVersion.getFile();
        } else {
            fileToDelete = lastMatchingVersion.getFile();            
        }
        
        // No local version exists (weird!)
        if (!fileToDelete.exists()) {
            logger.warn("Error while deleting file " + deletedVersion.getId() + "v" + deletedVersion.getVersion() + ": " + deletedVersion.getRelativePath() + " does NOT exist.");                        
        } else {
            
            // No difference between folder and file !
            File tempDeleteFile = new File(fileToDelete.getParentFile().getAbsolutePath() + "/.ignore-delete-" + fileToDelete.getName());
            FileUtil.deleteRecursively(tempDeleteFile); // just in case!

            fileToDelete.renameTo(tempDeleteFile);
            FileUtil.deleteRecursively(tempDeleteFile);
        }
        
        // Update DB
        updateSyncStatus(deletedVersion, SyncStatus.UPTODATE);
        em.merge(deletedVersion);
    }

    
    private void downloadChangeOrNew(CloneFile lastMatchingVersion, CloneFile newestVersion) throws CouldNotApplyUpdateException{
        // Temp files
        File tempNewFile = new File(newestVersion.getAbsoluteParentDirectory() + File.separator + ".ignore-assemble-to-" + newestVersion.getName());
        File tempDeleteFile = new File(newestVersion.getAbsoluteParentDirectory() + File.separator + ".ignore-delete-" + newestVersion.getName());

        FileUtil.deleteRecursively(tempNewFile); // just in case!
        FileUtil.deleteRecursively(tempDeleteFile); // just in case!

        
        ///// B. Make folder
        if (newestVersion.isFolder()) {
            File newFolder = new File(newestVersion.getAbsoluteParentDirectory() + File.separator + newestVersion.getName());
            newFolder.mkdir();
            return;
        } 
        
        /// if path don't exist create!
        File filePath = newestVersion.getFile().getParentFile();
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        
        ///// C+D. Download and assemble file
        downloadChunks(newestVersion);
        assembleFile(newestVersion, tempNewFile);            
        
        ///// E. delete local version (if there is one)
        if (lastMatchingVersion != null && lastMatchingVersion.getFile().exists()) {
            lastMatchingVersion.getFile().renameTo(tempDeleteFile);
            FileUtil.deleteRecursively(tempDeleteFile);
        }

        ///// F. Move temp file to new file
        tempNewFile.setLastModified(newestVersion.getLastModified().getTime());
        tempNewFile.renameTo(newestVersion.getFile());
        FileUtil.deleteRecursively(tempNewFile);
    }
    
    /**
     * Steps: A. add new updates to DB
     *
     * if (isFolder): B. make folder to tempfile
     *
     * if (isFile): C. download chunks for the last update D. assemble chunks to
     * tempfile
     *
     * if (local version exists): E. delete the local version
     *
     * F. move temp file to last update.
     *
     *
     * @param lastMatchingVersion
     * @param newFileUpdates
     * @throws CouldNotApplyUpdateException
     */
    private CloneFile applyChangeOrNew(CloneFile lastMatchingVersion, Update newFileUpdate) throws CouldNotApplyUpdateException{
        ///// A. Add to DB
        CloneFile newestVersion = addToDB(newFileUpdate);
        logger.info("- ChangeManager: Downloading/Updating " + newestVersion.getFile() + "");

        // Skip conditions
        boolean unknownButFileExists = lastMatchingVersion == null && newestVersion.getFile().exists();
        if (unknownButFileExists) {
            logger.warn("File " + newestVersion.getFile() + " already exists.");
            
            if(!newestVersion.isFolder()){
                try {
                    //ChunkEnumeration chunks = chunker.createChunks(newestVersion.getFile(), profile.getRepository().getChunkSize());
                    ChunkEnumeration chunks = chunker.createChunks(newestVersion.getFile());
                    //long checksum = chunks.getFileChecksum();
                    long checksum = chunker.createFileChecksum(newestVersion.getFile());
                    chunks.closeStream();

                    if(checksum != newestVersion.getChecksum()){
                        logger.info("resolveConflict: renaming localfile " + newestVersion + " ...");
                        String newFileName = generateConflictName(newestVersion);
        
                        File conflictedCopy = new File(newestVersion.getAbsoluteParentDirectory() + File.separator + newFileName);
                        newestVersion.getFile().renameTo(conflictedCopy);
                        
                        downloadChangeOrNew(lastMatchingVersion, newestVersion);
                    }
                } catch (FileNotFoundException ex) {
                    logger.warn("Cannot createChunks, File not exists:" + ex.getMessage() + " but ¿¿¿already checked before???");
                    throw new CouldNotApplyUpdateException(ex);
                }
            }
        } else {
            downloadChangeOrNew(lastMatchingVersion, newestVersion); 
        }

        // Update DB
        updateSyncStatus(newestVersion, SyncStatus.UPTODATE);
        em.merge(newestVersion);
        
        return newestVersion;
    }

    private void downloadChunks(CloneFile file) throws CouldNotApplyUpdateException {
        logger.info("Downloading file " + file.getRelativePath() + " ...");  

        int chunkNum = 1;
        for (CloneChunk chunk: file.getChunks()) {
            File chunkCacheFile = config.getCache().getCacheChunk(chunk);

            if (chunk.getCacheStatus() == CacheStatus.CACHED && chunkCacheFile.exists()) {
                logger.info("- Chunk " + chunk + " found in local cache.");
                continue;
            }

            try {
                logger.info("- Downloading chunk (" + chunkNum + "/" + file.getChunks().size() + ") " + chunk + " ...");

                String fileName = chunk.getName();
                CloneWorkspace workspace = file.getWorkspace();
                transfer.download(new RemoteFile(fileName), chunkCacheFile, workspace);     

                // Change DB state of chunk
                chunk.setCacheStatus(CacheStatus.CACHED);
                chunk.merge();
                chunkNum++;
            } catch (StorageException e) {
                logger.warn("- ERR: Chunk " + chunk + " not found (or something else)", e);
                throw new CouldNotApplyUpdateException(e);
            }
        }

        logger.info("- File " + file.getRelativePath() + " downloaded; Assembling ...");
    }


    private void assembleFile(CloneFile cf, File tempFile) throws CouldNotApplyUpdateException {
        
        FileOutputStream fos = null;       
        try {
            fos = new FileOutputStream(tempFile, false);
            logger.info("- Decrypting chunks to temp file  " + tempFile.getAbsolutePath() + " ...");

            int chunkNum = 1;
            for (CloneChunk chunk: cf.getChunks()) {
                logger.info("Chunk (" + chunkNum + File.separator + cf.getChunks().size() + ")" + config.getCache().getCacheChunk(chunk));

                // Read file to buffer
                File chunkFile = config.getCache().getCacheChunk(chunk);

                byte[] packed = FileUtil.readFileToByteArray(chunkFile);
                byte[] unpacked = FileUtil.unpack(packed, cf.getProfile().getEncryption(cf.getWorkspace().getId()));

                // Write decrypted chunk to file
                fos.write(unpacked);
                chunkNum++;
            }

            fos.close();
        } catch (Exception e) {
            throw new CouldNotApplyUpdateException(e);
        } finally {
            try {
                if(fos != null){
                    fos.close();
                }                
            } catch (IOException ex) {
                logger.error("Exception: ", ex);
            }
        }

        logger.info("- File " + cf.getRelativePath() + " downloaded");
    }

    /**
     * Returns true if the local client loses the conflict.
     */
    private boolean isLocalConflict(CloneFile existingVersion, Update update) {
        // Test different positive cases.
        // Please note, that the order of the IF-tests is important!

        if (existingVersion == null) {
            return false;
        }

        if (existingVersion.getStatus() == Status.DELETED && update.getStatus() == Status.DELETED) {
            return false;
        }
        
        Long evParentId = null;
        if (existingVersion.getParent() != null) {
            evParentId = existingVersion.getParent().getId();
        }
        
        Long updateParentId = null;
        if (update.getParentFileId() != null){
            updateParentId = update.getParentFileId();
        }
        
        if (existingVersion.getStatus() == Status.RENAMED && update.getStatus() == Status.RENAMED
                && isSameParent(evParentId, updateParentId)
                && existingVersion.getName().equals(update.getName())) {

            return false;
        }
        

        if (existingVersion.getStatus() == Status.NEW && update.getStatus() == Status.NEW
                && existingVersion.getSize() == update.getFileSize()
                && existingVersion.getChecksum() == update.getChecksum()
                && isSameParent(evParentId, updateParentId)
                && existingVersion.getName().equals(update.getName())) {

            return false;
        }

        if (existingVersion.getStatus() == Status.CHANGED && update.getStatus() == Status.CHANGED
                && existingVersion.getSize() == update.getFileSize()
                && existingVersion.getChecksum() == update.getChecksum()
                && isSameParent(evParentId, updateParentId)
                && existingVersion.getName().equals(update.getName())) {

            return false;
        }

        if(existingVersion.getSyncStatus() == SyncStatus.REMOTE && existingVersion.getVersion() == update.getVersion()){
            logger.info("Reapply the update again: " + update);
            return false;
        }

        // Conflict, I lose!
        return true;
    }
    
    public boolean isSameParent(Long parentId1, Long parentId2) {
        
        if ( parentId1 == null && parentId2 == null ) {
            return true;
        }
        
        if ( parentId1 == null || parentId2 == null ) {
            return false;
        }
        
        if ( parentId1.equals(parentId2) ) {
            return true;
        }
        
        return false;
        
    }

    public void showNotification(Map<Long, List<Update>> appliedUpdates) {
        tray.setStatusIcon(this.getClass().getSimpleName(), Tray.StatusIcon.UPTODATE);

        // Skip notification
        if (appliedUpdates.isEmpty()) {
            return;
        }

        // Poke updated files
        for (List<Update> updates : appliedUpdates.values()) {
            Update lastUpdate = updates.get(updates.size() - 1);

            CloneFile file = db.getFileOrFolder(lastUpdate.getFileId(), lastUpdate.getVersion());

            if (file != null) {
                desktop.touch(file.getFile());
            }
        }

        // TODO: Insert here special notifications
        File imageFile = new File(config.getResDir() + File.separator + "logo48.png");
        tray.notify(Constants.APPLICATION_NAME, appliedUpdates.size() + " file(s) updated", imageFile);
    }

    /// GGIPART ///
    public void restoreVersion(CloneFile newestVersion) throws CouldNotApplyUpdateException {

        // Temp files
        File tempNewFile = new File(newestVersion.getAbsoluteParentDirectory() + File.separator + ".ignore-assemble-to-" + newestVersion.getName());
        File tempDeleteFile = new File(newestVersion.getAbsoluteParentDirectory() + File.separator + ".ignore-delete-" + newestVersion.getName());

        FileUtil.deleteRecursively(tempNewFile); // just in case!
        FileUtil.deleteRecursively(tempDeleteFile); // just in case!


        ///// B. Make folder
        if (newestVersion.isFolder()) {
            tempNewFile.mkdirs();
        } ///// C+D. Download and assemble file
        else {

            /// GGIPART ///
            /// if path don't exist create!
            File filePath = newestVersion.getFile().getParentFile();
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            downloadChunks(newestVersion);
            assembleFile(newestVersion, tempNewFile);
        }

        ///// E. delete local version         
        newestVersion.getFile().renameTo(tempDeleteFile);
        FileUtil.deleteRecursively(tempDeleteFile);

        ///// F. Move temp file tonew file
        tempNewFile.setLastModified(newestVersion.getLastModified().getTime());
        tempNewFile.renameTo(newestVersion.getFile());
        FileUtil.deleteRecursively(tempNewFile);

        // Update DB
        updateSyncStatus(newestVersion, SyncStatus.UPTODATE);

        /// GGIPART ///
        em.merge(newestVersion);
        /// GGIENDPART ///        
    }
    
    public boolean queuesUpdatesIsWorking() {
        boolean empty;
        boolean processingFile;
        
        synchronized (queue) {
            empty = queue.isEmpty();
            processingFile = this.processingFiles;
        }
        
        return !empty | processingFile;
    }
    
    /// GGIENDPART ///
}
