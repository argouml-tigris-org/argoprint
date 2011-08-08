/* $Id$
 *******************************************************************************
 * Copyright (c) 2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mfortner
 *******************************************************************************
 */

package org.argoprint.ui.edit.velocity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.ui.edit.TemplateEditor;
import org.argoprint.ui.preview.TemplatePreviewerDialog;

@SuppressWarnings("serial")
public class VelocityTemplateEditor extends JPanel implements TemplateEditor {

    private static final Logger LOG = Logger
            .getLogger(VelocityTemplateEditor.class);

    private static final String[] extensions = new String[] { "vm" };

    private JEditorPane editorPane = new JEditorPane();

    /**
     * Constructor
     */
    public VelocityTemplateEditor() {
        super();
        this.setLayout(new BorderLayout());
        editorPane.setContentType("text/plain");
        editorPane.setPreferredSize(new Dimension(500, 400));
        editorPane.setEditable(true);
        editorPane.setBackground(Color.WHITE);
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(editorPane);
        this.add(scroller, BorderLayout.CENTER);
    }

    public String[] getSupportedFileExtensions() {
        return extensions;
    }

    public void init(TemplateMetaFile template) {
        editorPane.setText(template.getTemplateAsString());
    }

    public void preview(TemplateMetaFile template) {
        TemplatePreviewerDialog dialog = new TemplatePreviewerDialog(template);
        dialog.setVisible(true);
        
    }

    public void save(TemplateMetaFile template) {
        try {
            FileWriter writer = new FileWriter(new File(new URI(template.getTemplateFile())));
            writer.write(editorPane.getText());
            writer.flush();
            writer.close();
            
        } catch (IOException e) {
            LOG.error("Exception", e);
        } catch (URISyntaxException e) {
           LOG.error("Exception", e);
        }
    }

    public JComponent getEditorComponent() {
        return this;
    }

}
