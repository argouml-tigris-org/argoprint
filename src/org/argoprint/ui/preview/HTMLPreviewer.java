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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.util.FileUtil;
import org.xhtmlrenderer.simple.XHTMLPanel;

public class HTMLPreviewer extends JPanel implements TemplatePreviewer {
    
    private static final Logger LOG = Logger.getLogger(HTMLPreviewer.class);
    
    private static final String[] exts = new String[]{"html","htm","xml","xhtml","txt"};
    
    private static Map<String, String> mimeTypeMap = new HashMap<String, String>();
    
    static {
        mimeTypeMap.put("html", "text/html");
        mimeTypeMap.put("htm", "text/html");
        mimeTypeMap.put("xml", "text/xml");
        mimeTypeMap.put("txt", "text/plain");
        mimeTypeMap.put("xhtml", "text/xml");		
    }
    
    //private JEditorPane editor = new JEditorPane();
    private XHTMLPanel editor = new XHTMLPanel();
    
    /**
     * Constructor
     */
    public HTMLPreviewer(){
        //editor.setEditable(false);
        editor.setEnabled(true);
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(editor);
        this.setLayout(new BorderLayout());
        this.add(scroller, BorderLayout.CENTER);
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
        //editor.setContentType(type);
        
        File outputFile = new File(System.getProperty("java.io.tmpdir"), template.getOutputFile());
        String contents = "";
//        try {
//            contents = FileUtil.readTextFile(outputFile);
//        } catch (FileNotFoundException e) {
//            LOG.error("Exception", e);
//        }
        //editor.setText(contents);
        try {
            editor.setDocument(outputFile);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }

    }

}
