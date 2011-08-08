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

package org.argoprint.ui.edit;

import javax.swing.JComponent;

import org.argoprint.persistence.TemplateMetaFile;

/**
 * This interface specifies the functionality needed for any template editor.
 * 
 * @author mfortner
 */
public interface TemplateEditor {

    /**
     * Gets the supported file extensions for the editor. This should be the
     * file extension for the template.
     * 
     * @return
     */
    public String[] getSupportedFileExtensions();

    /**
     * Initializes the editor with data from the template meta file.
     * 
     * @param template The template meta file.
     */
    public void init(TemplateMetaFile template);

    /**
     * Saves the template metafile and the updated template file
     * 
     * @param template
     */
    public void save(TemplateMetaFile template);

    /**
     * Binds the template to the project and outputs the results to the temp
     * directory specified in the System property "java.io.tmpdir"
     * 
     * @param template
     */
    public void preview(TemplateMetaFile template);
    
    /**
     * Gets the component used by the editor.
     * @return
     */
    public JComponent getEditorComponent();

}
