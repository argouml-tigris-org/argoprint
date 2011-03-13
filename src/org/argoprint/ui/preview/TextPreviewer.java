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

package org.argoprint.ui.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.util.FileUtil;

public class TextPreviewer extends JPanel implements TemplatePreviewer {
    
    private static final Logger LOG = Logger.getLogger(TextPreviewer.class);
    
    private static final String[] exts = new String[]{"txt"};
    
    private static Map<String, String> mimeTypeMap = new HashMap<String, String>();
    
    static {        
        mimeTypeMap.put("txt", "text/plain");
    }
    
    private JEditorPane editor = new JEditorPane();
    
    /**
     * Constructor
     */
    public TextPreviewer(){
        editor.setEditable(false);
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(editor);
        this.setLayout(new BorderLayout());
        this.add(scroller, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(500, 400));
        
    }

    public JComponent getPreviewerComponent() {
        return this;
    }

    public String[] getSupportedFileExtensions() {
        return exts;
    }

    public void init(TemplateMetaFile template) {
        String type = mimeTypeMap.get(template.getTemplateFileExtension());
        type = (type == null)?"text/plain":type;
        editor.setContentType(type);
        
        final File outputFile = new File(System.getProperty("java.io.tmpdir"), template.getOutputFile());
        String contents = "";
        try {
            contents = FileUtil.readTextFile(outputFile);
        } catch (FileNotFoundException e) {
            LOG.error("Exception", e);
        }
        editor.setText(contents);
        


    }

}
