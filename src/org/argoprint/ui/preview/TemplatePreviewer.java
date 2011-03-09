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

import javax.swing.JComponent;

import org.argoprint.persistence.TemplateMetaFile;

/**
 * This interface is used to preview the contents of a template as it's bound to
 * a project.
 * 
 * @author mfortner
 */
public interface TemplatePreviewer {

    /**
     * This method gets the file extensions supported by the previewer.
     * 
     * @return A list of file extensions supported by the previewer.
     */
    public String[] getSupportedFileExtensions();

    /**
     * This method initializes the contents of the previewer.
     * 
     * @param template The template whose contents you want to preview.
     */
    public void init(TemplateMetaFile template);
    
    /**
     * Gets the previewer component.
     * @return
     */
    public JComponent getPreviewerComponent();

}
