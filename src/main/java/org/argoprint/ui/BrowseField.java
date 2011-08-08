// $Id$
// Copyright (c) 2011 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argoprint.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.argouml.cognitive.Translator;

/**
 * This class provides a simple JPanel that allows you to select a file or
 * directory.  This class can be configured to use a default directory.
 * 
 * @author mfortner
 */
@SuppressWarnings("serial")
public class BrowseField extends JPanel {

    /** Determines whether or not to use the default directory */
    private boolean useDefaultDirectory = true;

    /** The default directory */
    private String defaultDirectory = null;

    /** The label that appears to the left of the text field */
    private JLabel fieldLabel = new JLabel();

    /** The text field that contains the selected file or directory location */
    private JTextField textFld = new JTextField();

    /** The browse button */
    private JButton browseBtn = new JButton();

    /** The label text */
    private String labelText = null;

    /** The file chooser used to select files and directories */
    JFileChooser chooser = new JFileChooser();

    /**
     * Constructor
     */
    public BrowseField() {

    }

    /**
     * Constructor
     * 
     * @param useDefaultDirectory Determines whether or not to use the default
     *            directory
     * @param labelText  The text of the label
     */
    public BrowseField(boolean useDefaultDirectory, String labelText,
            String name) {
        this.useDefaultDirectory = useDefaultDirectory;
        this.labelText = labelText;
        this.setName(name);
        init();
    }

    /**
     * Constructor
     * 
     * @param defaultDirectory The default directory location.
     * @param labelText The text of the label.
     * @param name The name of the component. This is used as a key to save the
     *            preferences.
     */
    public BrowseField(String defaultDirectory, String labelText, String name) {
        this.defaultDirectory = defaultDirectory;
        this.labelText = labelText;
        this.setName(name);
        init();
    }

    private void init() {

        chooser.setMultiSelectionEnabled(false);

        // create the browseBtn
        browseBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.browse")) {

            public void actionPerformed(ActionEvent e) {
                int state = chooser.showOpenDialog(BrowseField.this);
                if (state == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();

                    String oldValue = textFld.getText();
                    String newValue = selectedFile.getAbsolutePath();

                    textFld.setText(newValue);

                    // update the preferences for this.
                    Preferences defaultDir = Preferences.userRoot()
                            .userNodeForPackage(BrowseField.class);
                    defaultDir.put(BrowseField.this.getName(), newValue);

                    firePropertyChange("location", oldValue, newValue);
                }

            }
        });

        if (this.useDefaultDirectory) {
            Preferences defaultDir = Preferences.userRoot().userNodeForPackage(
                    BrowseField.class);
            String dir = defaultDir.get(this.getName(), this.defaultDirectory);

            File defDir = new File(dir);
            chooser.setSelectedFile(defDir);
            this.textFld.setText(defDir.getAbsolutePath());
        }

        fieldLabel.setText(this.labelText);

        // add items to the panel
        this.add(fieldLabel);
        this.add(textFld);
        this.add(browseBtn);

    }

    /**
     * Sets the default directory used by the browse field. This sets the
     * directory location as well as the textField value, and the chooser's
     * default location.
     * 
     * @param defaultDirectory
     */
    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
        File defDir = new File(defaultDirectory);
        this.chooser.setSelectedFile(defDir);
        this.textFld.setText(defDir.getAbsolutePath());
    }

    /**
     * Determines whether or not file chooser should accept files, directories
     * or both.
     * 
     * @param mode one of the JFileChooser constants.
     */
    public void setFileChooserMode(int mode) {
        this.chooser.setFileSelectionMode(mode);
    }

    /**
     * This method gets the selected directory
     * 
     * @return
     */
    public String getSelectedFile() {
        return this.textFld.getText();
    }
    
    /**
     * Set the title used in the chooser dialog.
     * @param title     the title of the chooser dialog
     */
    public void setChooserDialogTitle(String title){
        this.chooser.setDialogTitle(title);
    }

}
