// $Id$
// Copyright (c) 2010 The Regents of the University of California. All
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

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.persistence.TemplateMetaFileSerializer;
import org.argouml.i18n.Translator;

/**
 * This class creates a panel that lists all of the available templates.
 * 
 * @author mfortner
 */
@SuppressWarnings("serial")
public class TemplateTablePanel extends JPanel {
    private TemplateTable table = null;

    private TemplateTableModel model = null;
    
    private String outputDir = null;

    /**
     * Constructor
     */
    public TemplateTablePanel() {
        setBorder(new TitledBorder(null, Translator
                .localize("argoprint.label.templates"), TitledBorder.LEADING,
                TitledBorder.TOP, null, null));        
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        model = createTableModel();
        table = new TemplateTable(model);
        
        scrollPane.getViewport().add(table);
        
        BrowseField browseFld = new BrowseField(System.getProperty("user.home"), Translator.localize("argoprint.label.output"),"browseField");
        browseFld.setFileChooserMode(JFileChooser.DIRECTORIES_ONLY);
        setOutputDir(browseFld.getSelectedFile());
        browseFld.addPropertyChangeListener(new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("location")){
                    setOutputDir((String)evt.getNewValue());
                }
                
            }});
        add(browseFld, BorderLayout.SOUTH);
        
    }
    
    /**
     * Adds a template to the table.
     * @param template  The template to be added.
     */
    public void addTemplate(TemplateMetaFile template){
        model.addTemplate(template);
    }
    
    /**
     * Removes a template from the table.
     * @param template  The template to be removed.
     */
    public void removeTemplate(TemplateMetaFile template){
        model.removeTemplate(template);
    }
    
    /**
     * Adds a list selection listener to the table.
     * @param listener  The listener to be added.
     */
    public void addListSelectionListener(ListSelectionListener listener){
        table.getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * @param layout
     */
    public TemplateTablePanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * @param isDoubleBuffered
     */
    public TemplateTablePanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public TemplateTablePanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public List<TemplateMetaFile> getSelectedTemplates() {
        return (this.model == null) ? null : this.model.getSelectedTemplates();
    }

    /**
     * This method creates a table model and populates it with the
     * TemplateMetaFile records.
     * 
     * @return a TemplateTableModel
     */
    private TemplateTableModel createTableModel() {
        List<TemplateMetaFile> templates = TemplateMetaFileSerializer.getAllTemplates();
        TemplateTableModel model = (templates.isEmpty())? new TemplateTableModel():new TemplateTableModel(templates);
        return model;
    }

    /**
     * Gets the table model.
     * @return
     */
    public TemplateTableModel getModel() {
        return this.model;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

 

}
