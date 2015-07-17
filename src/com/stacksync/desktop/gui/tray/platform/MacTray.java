package com.stacksync.desktop.gui.tray.platform;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import com.stacksync.desktop.Constants;
import com.stacksync.desktop.config.Config;
import com.stacksync.desktop.config.Folder;
import com.stacksync.desktop.config.profile.Profile;
import com.stacksync.desktop.exceptions.ConfigException;
import com.stacksync.desktop.exceptions.InitializationException;
import com.stacksync.desktop.gui.linux.UpdateStatusTextRequest;
import com.stacksync.desktop.gui.tray.Tray;
import com.stacksync.desktop.gui.tray.TrayEvent;
import com.stacksync.desktop.gui.tray.TrayEventListener;
import com.stacksync.desktop.gui.tray.TrayIconStatus;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;

public class MacTray extends Tray {
    
    private final ResourceBundle resourceBundle = Config.getInstance().getResourceBundle();
    private SystemTray tray;
    private PopupMenu menu;
    private TrayIcon icon;
    private MenuItem itemStatus, itemPreferences, itemWebsite, itemWebsite2, itemQuit, itemShare, itemFolder;
    
    private TrayIconStatus status;  
    private JFrame frame;
    
    public MacTray() {
        super();

        this.menu = null;
        this.status = new TrayIconStatus(new TrayIconStatus.TrayIconStatusListener() {
            @Override
            public void trayIconUpdated(String filename) {
                if (config != null) {
                    setIcon(new File(config.getResDir()+File.separator+
                         Constants.TRAY_DIRNAME+File.separator+filename));   
                }
            }
        });
    }
    
    @Override
    public void notify(String summary, String body, File imageFile) {        
        
        frame = new JFrame();
        frame.setSize(400,80);
        frame.setUndecorated(true);
        
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();// size of the screen
        Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());// height of the task bar
        frame.setLocation(scrSize.width - frame.getWidth(), toolHeight.top + toolHeight.bottom);

        
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0f;
        constraints.weighty = 1.0f;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;
        
        JLabel headingLabel = new JLabel(summary + ": " + body);
        if(imageFile != null){
            BufferedImage img;
            try {
                img = ImageIO.read(imageFile);
                Image scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImg);
                headingLabel.setIcon(scaledIcon);
            } catch (IOException ex) { }
            
        }
        headingLabel.setOpaque(false);
        frame.add(headingLabel, constraints);
        
        constraints.gridx++;
        constraints.weightx = 0f;
        constraints.weighty = 0f;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTH;
        
        JButton cloesButton = new JButton(new AbstractAction("X") {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    frame.dispose();
                }
        });

        cloesButton.setMargin(new Insets(1, 4, 1, 4));
        cloesButton.setFocusable(false);
        
        frame.add(cloesButton, constraints);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
              
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3500); // time after which pop up will be disappeared.
                    frame.dispose();
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            };
        }.start();
    }

    @Override
    public void updateUI() {
        addItemFolder();
    }
    
    @Override
    public void updateStatusText() {
                
        UpdateStatusTextRequest menu = new UpdateStatusTextRequest(processesText);
        synchronized (itemStatus) {
            itemStatus.setLabel(menu.getStatusText());
        }
    }    

    @Override
    public void setStatusIconPlatform(StatusIcon s) {
        status.setIcon(s);
    }
    
    private void setIcon(File file) {
        icon.setImage(Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath()));
    }    

    @Override
    public void init(String initialMessage) throws InitializationException {
        initMenu(initialMessage);
        initIcon();               
    }

    @Override
    public void destroy() {
        // Nothing.
    }
    
    private void initMenu(String initialMessage) {
        // Create
        menu = new PopupMenu();

        // Status
        itemStatus = new MenuItem(initialMessage);
        itemStatus.setEnabled(false);

        menu.add(itemStatus);

        // Profile and folders
        menu.addSeparator();

        /*Profile profile = config.getProfile();

        final Folder folder = profile.getFolder();
        if (folder != null && folder.isActive() && folder.getLocalFile() != null) {

            MenuItem itemFolder = new MenuItem(folder.getLocalFile().getName());

            itemFolder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    fireTrayEvent(new TrayEvent(TrayEvent.EventType.OPEN_FOLDER, folder.getLocalFile().getAbsolutePath()));
                }
            });

            menu.add(itemFolder);
        }*/
        
        
        itemFolder = new MenuItem(resourceBundle.getString("tray_folder"));
        itemFolder.setEnabled(false);
        menu.add(itemFolder);
        menu.addSeparator();

        // Preferences
        itemPreferences = new MenuItem("Preferences ...");
        itemPreferences.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                fireTrayEvent(new TrayEvent(TrayEvent.EventType.PREFERENCES));
            }
        });

        menu.add(itemPreferences);
        
        itemShare = new MenuItem(resourceBundle.getString("tray_share_folder"));
        itemShare.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                fireTrayEvent(new TrayEvent(TrayEvent.EventType.SHARE));
            }
        });

        menu.add(itemShare);
        
        menu.addSeparator();
        itemWebsite = new MenuItem(resourceBundle.getString("tray_stacksync_website"));
        itemWebsite.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                fireTrayEvent(new TrayEvent(TrayEvent.EventType.WEBSITE));
            }
        });
        menu.add(itemWebsite);
        
        itemWebsite2 = new MenuItem(resourceBundle.getString("tray_ast_website"));
        itemWebsite2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                fireTrayEvent(new TrayEvent(TrayEvent.EventType.WEBSITE2));
            }
        });
        menu.add(itemWebsite2);
               
        menu.addSeparator();

        // Quit
        itemQuit = new MenuItem(resourceBundle.getString("tray_exit"));
        itemQuit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                fireTrayEvent(new TrayEvent(TrayEvent.EventType.QUIT));
            }
        });

        menu.add(itemQuit);
    }
    
    private void addItemFolder() {
        
        try {
            Profile profile = config.getProfile();
            final Folder folder = profile.getFolder();
            if (folder != null && folder.isActive() && folder.getLocalFile() != null) {

                itemFolder.setLabel(folder.getLocalFile().getName());
                itemFolder.setEnabled(true);
                itemFolder.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        fireTrayEvent(new TrayEvent(TrayEvent.EventType.OPEN_FOLDER, folder.getLocalFile().getAbsolutePath()));
                    }
                });
            }
        } catch(Exception e){
            
        }
    }
    
    private void initIcon() throws InitializationException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new InitializationException("Unable to set look and feel for tray icon", e);
        }

        tray = SystemTray.getSystemTray();        
        File defaultIconFile = new File(config.getResDir()+File.separator+
                Constants.TRAY_DIRNAME+File.separator+Constants.TRAY_FILENAME_DEFAULT);

        Image image = Toolkit.getDefaultToolkit().getImage(defaultIconFile.getAbsolutePath());

        icon = new TrayIcon(image, "Stacksync", menu);
        icon.setImageAutoSize(true);        

        try {
            tray.add(icon);
        } catch (AWTException e) {
            throw new InitializationException("Unable to add tray icon.", e);
        }
    }
        
    
    public static void main(String[] args) throws ConfigException, InitializationException, InterruptedException {
        System.out.println("STARTED");

        config.load();
        Tray tray = Tray.getInstance();
        tray.registerProcess(tray.getClass().getSimpleName());
        tray.init("Everything is up to date.");

        //File imageFile = null;
        File imageFile = new File(config.getResDir() + File.separator + "logo48.png");
        tray.notify("hello asdas dasd dasd asd ", "test asdsad sd asd sa", imageFile);
        //tray.setStatus(Status.UPDATING);
        tray.addTrayEventListener(new TrayEventListener() {

            @Override
            public void trayEventOccurred(TrayEvent event) {
                System.out.println(event);
            }
        });
        tray.setStatusIcon(tray.getClass().getSimpleName(), StatusIcon.UPDATING);
        //System.out.println(FileUtil.showBrowseDirectoryDialog());
        
        Thread.sleep(5000);
    }

}
