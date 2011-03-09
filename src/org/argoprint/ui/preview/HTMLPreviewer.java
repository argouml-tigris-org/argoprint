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

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

import org.argoprint.persistence.TemplateMetaFile;

public class HTMLPreviewer implements TemplatePreviewer {
    
    private static final String[] exts = new String[]{"html","htm","xml","xhtml","txt"};
    
    private static Map<String, String> mimeTypeMap = new HashMap<String, String>();
    
    static {
        mimeTypeMap.put("html", "text/html");
        mimeTypeMap.put("htm", "text/html");
        mimeTypeMap.put("xml", "text/xml");
        mimeTypeMap.put("txt", "text/plain");
        mimeTypeMap.put("xhtml", "text/xml");		
    }
    
    private JEditorPane editor = new JEditorPane();
    
    public HTMLPreviewer(){
        editor.setEditable(true);
    }

    public JComponent getPreviewerComponent() {
        return editor;
    }

    public String[] getSupportedFileExtensions() {
        return exts;
    }

    public void init(TemplateMetaFile template) {
        String type = mimeTypeMap.get(template.getTemplateFileExtension());
        type = (type == null)?"text/plain":type;
        editor.setContentType(type);
        
        System.getProperty("java.io.tmpdir");

    }

}
