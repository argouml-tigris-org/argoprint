/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    phidias
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2003-2005, Linus Tolke, Mikael Albertsson, Mattias Danielsson,
// Per Engstrom, Fredrik Grundahl, Martin Gyllensten, Anna Kent, Anders Olsson,
// Mattias Sidebeck.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright
//   notice, this list of conditions and the following disclaimer in the
//   documentation and/or other materials provided with the distribution.
//
// * Neither the name of the University of Linkoping nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.

package org.argouml.ui.argoprint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.argoprint.ui.ArgoPrintDialog;
import org.argouml.moduleloader.ModuleInterface;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.cmd.GenericArgoMenuBar;


/**
 * This menu plugin adds support for the ArgoPrint module.
 * 
 * @author Mattias Danielsson
 * @author mfortner
 * @since  0.0.2
 */
public class ArgoPrintMenuPlugin
    implements ModuleInterface, ActionListener {
    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(ArgoPrintMenuPlugin.class);

    /**
     * The menu item.
     */
    private JMenuItem menuItem;

    /**
     * The dialog used
     */
    private ArgoPrintDialog argoPrintDialog;

    /**
     * The constructor.
     */
    public ArgoPrintMenuPlugin() {
        menuItem = new JMenuItem("ArgoPrint");
	menuItem.addActionListener(this);
    }

    ////////////////////////////////////////////////////////////////
    // Main methods.

    /**
     * @see org.argouml.uml.ui.UMLAction#actionPerformed(java.awt.event.ActionEvent)
     *
     * Just let the tester know that we got executed.
     */
    public void actionPerformed(ActionEvent event) {
	LOG.info("Showing ArgoPrint Dialog");

        if (argoPrintDialog == null) {
            argoPrintDialog = new ArgoPrintDialog();
            argoPrintDialog.setLocationRelativeTo(ProjectBrowser.getInstance());
            argoPrintDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }       
	argoPrintDialog.setVisible(true);
    }


    /**
     * @see org.argouml.moduleloader.ModuleInterface#enable()
     */
    public boolean enable() {
        // Register into the Tools menu.
        GenericArgoMenuBar menubar =
            (GenericArgoMenuBar) ProjectBrowser.getInstance().getJMenuBar();
        menubar.getTools().add(menuItem);
	return true;
    }

    /**
     * @see org.argouml.moduleloader.ModuleInterface#disable()
     */
    public boolean disable() {
	GenericArgoMenuBar menubar =
	    (GenericArgoMenuBar) ProjectBrowser.getInstance().getJMenuBar();
	menubar.getTools().remove(menuItem);
	return true;
    }

    /**
     * @see org.argouml.moduleloader.ModuleInterface#getName()
     */
    public String getName() {
        return "ArgoPrint";
    }

    /**
     * @see org.argouml.moduleloader.ModuleInterface#getInfo(int)
     */
    public String getInfo(int type) {
        switch (type) {
        case DESCRIPTION:
            return "This module is a report generator.";
        case AUTHOR:
            return "Mattias Danielsson";
        case VERSION:
            return "0.0.1";
        default:
            return null;
        }
    }

    /**
     * The UID.
     */
    private static final long serialVersionUID = -2718955931189047175L;
}
