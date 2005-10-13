// $Id$
//Copyright (c) 2005 Linus Tolke
//Copyright (c) 2003, Mikael Albertsson, Mattias Danielsson, Per Engström, 
//Fredrik Gröndahl, Martin Gyllensten, Anna Kent, Anders Olsson, 
//Mattias Sidebäck.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without 
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//  this list of conditions and the following disclaimer.
// 
//* Redistributions in binary form must reproduce the above copyright 
//  notice, this list of conditions and the following disclaimer in the 
//  documentation and/or other materials provided with the distribution.
//
//* Neither the name of the University of Linköping nor the names of its 
//  contributors may be used to endorse or promote products derived from 
//  this software without specific prior written permission. 
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.

package org.argouml.ui.argoprint;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.argoprint.ui.ArgoPrintJDialog;
import org.argouml.moduleloader.ModuleInterface;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.cmd.GenericArgoMenuBar;
import org.argouml.uml.ui.UMLAction;


/**
 * @author Mattias Danielsson
 * @since  0.0.1
 */
public class ArgoPrintMenuPlugin extends UMLAction
    implements ModuleInterface {
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
     * The constructor.
     */
    public ArgoPrintMenuPlugin() {
	super("Plugin ArgoPrintMenu entry", false);

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
	LOG.info("Starting ArgoPrint");

	// This is where the ArgoPrint GUI frame is created and displayed
	ArgoPrintJDialog argoPrintDialog =
	    new ArgoPrintJDialog(new JFrame());
	LOG.info("Setting Gui Log");
	LOG.info("Showing ArgoPrint Dialog");
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
}





