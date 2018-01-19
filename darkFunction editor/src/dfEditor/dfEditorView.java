/*
 *  Copyright 2012 Samuel Taylor
 *
 *  This file is part of darkFunction Editor
 *
 *  darkFunction Editor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  darkFunction Editor is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with darkFunction Editor.  If not, see <http://www.gnu.org/licenses/>.
 */


package dfEditor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;

import com.googlecode.jfilechooserbookmarks.DefaultBookmarksPanel;

import dfEditor.animation.AnimationController;
import dfEditor.command.CommandManager;
import dfEditor.io.AnimationSetReader;
import dfEditor.io.CustomFilter;
import dfEditor.io.SpritesheetReader;
import dfEditor.io.Utils;

/**
 * The application's main frame.
 */
public class dfEditorView extends FrameView implements TaskChangeListener, org.jdesktop.application.Application.ExitListener
{
    private JFileChooser fileChooser;
    public static BufferedImage weaponImage = null;
    public static BufferedImage swooshImage = null;

    public dfEditorView(dfEditorApp app) {
        super(app);

        fileChooser = createFileChooser();

        initComponents();

        helpLabel.setText("http://www.darkfunction.com");

        java.net.URL imgURL = this.getClass().getResource("resources/main_icons/Star.png");
        ImageIcon ii = new ImageIcon(imgURL);
        this.getFrame().setIconImage(ii.getImage());

        this.getFrame().addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                dfEditorApp.getApplication().exit();
            }
        });

    }
    
    private static JFileChooser createFileChooser()
	{
		JFileChooser jfc = new JFileChooser();
		DefaultBookmarksPanel panel = new DefaultBookmarksPanel();
		panel.setOwner(jfc);
		jfc.setAccessory(panel);
		jfc.setPreferredSize(new Dimension(800, 600));
		return jfc;
	}

    @Override
	public void willExit(java.util.EventObject aObj)
    {

    }

    @Override
	public boolean canExit(java.util.EventObject aObj)
    {
        for (int i=0; i<tabbedPane.getTabCount(); ++i)
        {
            dfEditorTask tab = (dfEditorTask)(tabbedPane.getComponentAt(i));

            if (!tab.hasBeenModified())
            {
                continue;
            }

            String[] choices = {" Save ", " Discard ", " Cancel "};

            String msg = "You have not saved " + tab.getName() + ". Would you like to save it now?";
            int choice = JOptionPane.showOptionDialog(
                               this.getFrame()                   // Center in window.
                             , msg                          // Message
                             , "Save changes?"                  // Title in titlebar
                             , JOptionPane.YES_NO_OPTION    // Option type
                             , JOptionPane.WARNING_MESSAGE    // messageType
                             , null                         // Icon (none)
                             , choices                      // Button text as above.
                             , " Save "      // Default button's label
                           );
            switch(choice)
            {
                case 0:
                    if (tab.save())
                        tabbedPane.remove(i);
                    else
                    {
                        return false;
                    }
                    break;
                case 1:
                    tabbedPane.remove(i);
                    i--;
                    break;
                case 2:
                    return false;
                default:
                    return false;
            }
        }
        return true;
    }


    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = dfEditorApp.getApplication().getMainFrame();
            aboutBox = new dfEditorAboutBoxFree(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        dfEditorApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        newSpritesheetItem = new javax.swing.JMenuItem();
        newAnimationItem = new javax.swing.JMenuItem();
        loadMenuItem = new javax.swing.JMenuItem();
        menuItemSave = new javax.swing.JMenuItem();
        menuItemSaveAs = new javax.swing.JMenuItem();
        // splinchedExport = new javax.swing.JMenuItem();
        setWeapon = new javax.swing.JMenuItem();
        setSwoosh = new javax.swing.JMenuItem();
        setBackgroundImage = new JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        helpLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setMnemonic('F');
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(dfEditor.dfEditorApp.class).getContext().getResourceMap(dfEditorView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenu2.setMnemonic('N');
        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        newSpritesheetItem.setText(resourceMap.getString("newSpritesheetItem.text")); // NOI18N
        newSpritesheetItem.setName("newSpritesheetItem"); // NOI18N
        newSpritesheetItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSpritesheetItemActionPerformed(evt);
            }
        });
        jMenu2.add(newSpritesheetItem);

        newAnimationItem.setText(resourceMap.getString("newAnimationItem.text")); // NOI18N
        newAnimationItem.setName("newAnimationItem"); // NOI18N
        newAnimationItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAnimationItemActionPerformed(evt);
            }
        });
        jMenu2.add(newAnimationItem);

        fileMenu.add(jMenu2);

        loadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        loadMenuItem.setMnemonic('O');
        loadMenuItem.setText(resourceMap.getString("loadMenuItem.text")); // NOI18N
        loadMenuItem.setName("loadMenuItem"); // NOI18N
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(loadMenuItem);

        menuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSave.setMnemonic('S');
        menuItemSave.setText(resourceMap.getString("menuItemSave.text")); // NOI18N
        menuItemSave.setEnabled(false);
        menuItemSave.setName("menuItemSave"); // NOI18N
        menuItemSave.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSaveActionPerformed(evt);
            }
        });
        fileMenu.add(menuItemSave);

        menuItemSaveAs.setMnemonic('a');
        menuItemSaveAs.setText(resourceMap.getString("menuItemSaveAs.text")); // NOI18N
        menuItemSaveAs.setEnabled(false);
        menuItemSaveAs.setName("menuItemSaveAs"); // NOI18N
        menuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSaveAsActionPerformed(evt);
            }
        });
        fileMenu.add(menuItemSaveAs);

        setWeapon.setText("Set Weapon Image"); // NOI18N
        setWeapon.setEnabled(true);
        setWeapon.setName("Set Weapon Image"); // NOI18N
        setWeapon.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSetWeaponActionPerformed(evt);
            }
        });
        fileMenu.add(setWeapon);
        
        setSwoosh.setText("Set Swoosh Image"); // NOI18N
        setSwoosh.setEnabled(true);
        setSwoosh.setName("Set Swoosh Image"); // NOI18N
        setSwoosh.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSetSwooshActionPerformed(evt);
            }
        });
        fileMenu.add(setSwoosh);
        
        setBackgroundImage.setText("Set Background Image"); // NOI18N
        setBackgroundImage.setEnabled(true);
        setBackgroundImage.setName("Set Background Image"); // NOI18N
        setBackgroundImage.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSetBackgroundImageActionPerformed(evt);
            }
        });
        fileMenu.add(setBackgroundImage);

        /*
        splinchedExport.setText("Splinched Export"); // NOI18N
        splinchedExport.setEnabled(true);
        splinchedExport.setName("splunchedExport"); // NOI18N
        splinchedExport.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSplinchedExportActionPerformed(evt);
            }
        });
        fileMenu.add(splinchedExport);
        */

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(dfEditor.dfEditorApp.class).getContext().getActionMap(dfEditorView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText(resourceMap.getString("editMenu.text")); // NOI18N
        editMenu.setName("editMenu"); // NOI18N

        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem.setMnemonic('U');
        undoMenuItem.setText(resourceMap.getString("undoMenuItem.text")); // NOI18N
        undoMenuItem.setEnabled(false);
        undoMenuItem.setName("undoMenuItem"); // NOI18N
        undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(undoMenuItem);

        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem.setMnemonic('R');
        redoMenuItem.setText(resourceMap.getString("redoMenuItem.text")); // NOI18N
        redoMenuItem.setEnabled(false);
        redoMenuItem.setName("redoMenuItem"); // NOI18N
        redoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(redoMenuItem);

        menuBar.add(editMenu);

        helpMenu.setAction(actionMap.get("showAboutBox")); // NOI18N
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(800, 20));

        helpLabel.setText(resourceMap.getString("helpLabel.text")); // NOI18N
        helpLabel.setName("helpLabel"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(helpLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
        );

        helpLabel.getAccessibleContext().setAccessibleName(resourceMap.getString("helpLabel.AccessibleContext.accessibleName")); // NOI18N

        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setMinimumSize(new java.awt.Dimension(5, 5));
        mainPanel.setName("mainPanel"); // NOI18N

        tabbedPane.setName("tabbedPane"); // NOI18N
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents




    private void menuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveAsActionPerformed
        dfEditorTask currentTab = (dfEditorTask)tabbedPane.getSelectedComponent();

        if (currentTab != null)
        {
            if (currentTab.saveAs())
            {
                java.io.File file = currentTab.getSavedFile();
                if (file != null && file.exists())
                {
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
                }
            }
        }
    }//GEN-LAST:event_menuItemSaveAsActionPerformed



    private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuItemActionPerformed
        ((dfEditorTask)tabbedPane.getSelectedComponent()).undo();
    }//GEN-LAST:event_undoMenuItemActionPerformed

    private void redoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoMenuItemActionPerformed
        ((dfEditorTask)tabbedPane.getSelectedComponent()).redo();
    }//GEN-LAST:event_redoMenuItemActionPerformed

    private void addTab(java.awt.Component c)
    {
        tabbedPane.add(c);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(c), new TabComponent(tabbedPane));
        tabbedPane.setSelectedComponent(c);
    }

    private void newSpritesheetItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSpritesheetItemActionPerformed
        //Custom button text

        JFrame frame = this.getFrame();
        SingleOrMultiDialog dialog = new SingleOrMultiDialog(frame, true);
        dialog.setLocationRelativeTo(frame);

        int result = dialog.showDialog();

        dfEditorPanel panel = null;
        switch (result)
        {
            case 0:
            {
                panel = new SpritesheetController(new CommandManager(undoMenuItem, redoMenuItem), true, helpLabel, this, fileChooser);
                break;
            }
            case 1:
            {
                panel = new SpriteImageController(new CommandManager(undoMenuItem, redoMenuItem), helpLabel, this, fileChooser);
                break;
            }
        }

        if (panel != null)
            addTab(panel);

}//GEN-LAST:event_newSpritesheetItemActionPerformed

    private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMenuItemActionPerformed
        load();
    }//GEN-LAST:event_loadMenuItemActionPerformed

    public void load()
    {
        JFileChooser chooser = fileChooser;

        CustomFilter filter = new CustomFilter();
        filter.addExtension(CustomFilter.EXT_ANIM);
        filter.addExtension(CustomFilter.EXT_SPRITE);
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Load a spritesheet / animation set");
        JFrame mainFrame = dfEditorApp.getApplication().getMainFrame();
        int returnVal = chooser.showOpenDialog(mainFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = chooser.getSelectedFile();

            if (selectedFile != null && selectedFile.exists())
            {
                java.awt.Component task = null;

                boolean bLoaded = false;
                if (Utils.getExtension(selectedFile).equals(CustomFilter.EXT_ANIM)) // meh
                {
                    AnimationController animController = new AnimationController(new CommandManager(undoMenuItem, redoMenuItem), false, helpLabel, this, fileChooser);
                    try {
                        AnimationSetReader reader = new AnimationSetReader(selectedFile);
                        bLoaded = animController.load(reader);
                        task = animController;
                        if (helpLabel != null)
                            helpLabel.setText("Loaded animations " + selectedFile.toString());
                    } catch (Exception e) {
                        javax.swing.JOptionPane.showMessageDialog(null, "Could not load animation file!", "File error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if (Utils.getExtension(selectedFile).equals(CustomFilter.EXT_SPRITE))
                {
                    JFrame frame = this.getFrame();
                    SingleOrMultiDialog dialog = new SingleOrMultiDialog(frame, true);
                    dialog.setLocationRelativeTo(frame);

                    SpritesheetReader reader = new SpritesheetReader(selectedFile);
                    String imagePath = reader.getImagePath();
                    DefaultTreeModel model = reader.getTreeModel();

                    int result = dialog.showDialog();

                    switch (result)
                    {
                        case 0:
                        {
                            SpritesheetController spriteSheet = new SpritesheetController(new CommandManager(undoMenuItem, redoMenuItem), false, helpLabel, this, fileChooser);
                            bLoaded = spriteSheet.load(imagePath, model);
                            task = spriteSheet;
                            break;
                        }
                        case 1:
                        {
                            SpriteImageController spriteSheet = new SpriteImageController(new CommandManager(undoMenuItem, redoMenuItem), helpLabel, this, fileChooser);
                            bLoaded = spriteSheet.load(imagePath, model);
                            task = spriteSheet;
                            break;
                        }
                    }

                    if (helpLabel != null)
                        helpLabel.setText("Loaded spritesheet " + selectedFile.toString());
                }

                if (bLoaded && task != null)
                {
                    addTab(task);
                    ((dfEditorTask)task).setSavedFile(selectedFile);
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), selectedFile.getName());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(
                   null,
                   "No such file exists",
                   "File not found",
                   JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        updateMenuBar();
    }//GEN-LAST:event_tabbedPaneStateChanged

    private void newAnimationItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAnimationItemActionPerformed
        AnimationController animationSet = new AnimationController(new CommandManager(undoMenuItem, redoMenuItem), true, this.helpLabel, this, fileChooser);
        addTab(animationSet);
    }//GEN-LAST:event_newAnimationItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        showAboutBox();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void menuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveActionPerformed
        dfEditorTask currentTab = (dfEditorTask)tabbedPane.getSelectedComponent();

        if (currentTab != null)
        {
            if (currentTab.save())
            {
                java.io.File file = currentTab.getSavedFile();

                if (file != null && file.exists())
                {
                    String saveName = file.getName();
                    if (saveName != null && saveName.length() > 0)
                    {
                        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), saveName);
                    }
                }
            }
        }
    }//GEN-LAST:event_menuItemSaveActionPerformed

    private void menuItemSetWeaponActionPerformed(java.awt.event.ActionEvent evt)
    {
    	BufferedImage image = loadCustomImage("Load a custom weapon image");
    	if (image != null) {
    		// Set new weapon
			dfEditorView.weaponImage = image;
			JOptionPane.showMessageDialog(null, "You will need to reload your animation file for the weapon images to become visible");
    	}
    }
    
    private void menuItemSetSwooshActionPerformed(java.awt.event.ActionEvent evt)
    {
    	BufferedImage image = loadCustomImage("Load a custom swoosh image");
    	if (image != null) {
    		// Set new weapon
			dfEditorView.swooshImage = image;
			JOptionPane.showMessageDialog(null, "You will need to reload your animation file for the swoosh images to become visible");
    	}
    }

	private BufferedImage loadCustomImage(String fileSelectText) {
		JFileChooser chooser = fileChooser;

        CustomFilter filter = new CustomFilter();
        filter.addExtension(CustomFilter.EXT_PNG);
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(fileSelectText);
        JFrame mainFrame = dfEditorApp.getApplication().getMainFrame();
        int returnVal = chooser.showOpenDialog(mainFrame);

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            java.io.File selectedFile = chooser.getSelectedFile();
            try {
				BufferedImage image = ImageIO.read(selectedFile);

				return image;

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Unable to load image: " + selectedFile.getName());
				e.printStackTrace();
			}
        }
        
        return null;
	}
    
    private void menuSetBackgroundImageActionPerformed(java.awt.event.ActionEvent evt) {
    	dfEditorTask currentTab = (dfEditorTask)tabbedPane.getSelectedComponent();

        if (currentTab != null)
        {
        	BufferedImage image = loadCustomImage("Load a background image");
        	if (image != null) 
        		((AnimationController) currentTab).setBackgroundImage(image);
        } else {
        	JOptionPane.showMessageDialog(null, "Please open an animation before selecting a background image");
        }
    }

    private void menuItemSplinchedExportActionPerformed(java.awt.event.ActionEvent evt)
    {
    	 dfEditorTask currentTab = (dfEditorTask)tabbedPane.getSelectedComponent();

         if (currentTab != null)
         {
             ((AnimationController) currentTab).export(false);
         }
    }

    private void updateMenuBar()
    {
        dfEditorTask selectedTask = (dfEditorTask)(tabbedPane.getSelectedComponent());

        if (selectedTask != null)
        {
            String savedName = null;
            if (selectedTask.getSavedFile() != null)
                savedName = selectedTask.getSavedFile().getName();

            if (selectedTask.hasBeenModified())
            {
                if (savedName != null)
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(),  "*" + savedName);

                menuItemSaveAs.setEnabled(true);
                menuItemSave.setEnabled(true);
            }
            else
            {
                menuItemSaveAs.setEnabled(false);
                menuItemSave.setEnabled(false);
            }

            selectedTask.refreshCommandManagerButtons();
        }
    }

    @Override
	public void taskChanged(dfEditorTask aTask)
    {
        // TODO: this ignores arg and uses current task
        updateMenuBar();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JLabel helpLabel;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem loadMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuItemSave;
    private javax.swing.JMenuItem menuItemSaveAs;
    private javax.swing.JMenuItem setWeapon;
    private javax.swing.JMenuItem setSwoosh;
    private javax.swing.JMenuItem setBackgroundImage;
    // private javax.swing.JMenuItem splinchedExport;
    private javax.swing.JMenuItem newAnimationItem;
    private javax.swing.JMenuItem newSpritesheetItem;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JMenuItem undoMenuItem;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;



}



