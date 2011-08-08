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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.argoprint.persistence.TemplateMetaFile;

public class HTMLPreviewer extends JPanel implements TemplatePreviewer {
    
    private static final String[] exts = {"html","htm","xhtml","xml"};
    
    private static final Logger LOG = Logger.getLogger(HTMLPreviewer.class);
    
    private JPanel component = new JPanel();
    
    public HTMLPreviewer(){
        JLabel label = new JLabel("Previewing Document In Browser");
        component.add(label);
    }

    public JComponent getPreviewerComponent() {
        return component;
        
    }

    public String[] getSupportedFileExtensions() {
        return exts;
    }

    public void init(TemplateMetaFile template) {
        
        File outputFile = new File(System.getProperty("java.io.tmpdir"), template.getOutputFile());
        
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(outputFile.toURI());
        } catch (IOException e) {
            LOG.error("Exception", e);
        }

    }

}
