// $Id$
//Copyright (c) 2003, Mikael Albertsson, Mattias Danielsson, Per Engstr�m, 
//Fredrik Gr�ndahl, Martin Gyllensten, Anna Kent, Anders Olsson, 
//Mattias Sideb�ck.
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
//* Neither the name of the University of Link�ping nor the names of its 
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
import java.util.Vector;

import javax.swing.JMenuItem;

import org.argoprint.ui.ArgoPrintJDialog;
import org.argouml.application.api.PluggableMenu;
import org.argouml.uml.ui.UMLAction;

import org.apache.log4j.Logger;


/**
 *  @author Mattias Danielsson
 *  @since  0.0.1
 */
public class ArgoPrintMenuPlugin extends UMLAction
    implements PluggableMenu {
    private static final Logger LOG = 
        Logger.getLogger(ArgoPrintMenuPlugin.class);
    /**
     * This is not publicly creatable.
     */
    protected ArgoPrintMenuPlugin() {
	super("Plugin ArgoPrintMenu entry", false);
    }

    ////////////////////////////////////////////////////////////////
    // Main methods.

    /**
     * Just let the tester know that we got executed.
     */
    public void actionPerformed(ActionEvent event) {
	LOG.info("Starting ArgoPrint");

	// This is where the ArgoPrint GUI frame is created and displayed
	ArgoPrintJDialog argoPrintDialog = 
	    new ArgoPrintJDialog(new javax.swing.JFrame(), true);
	LOG.info("Setting Gui Log");
	argoPrintDialog.setLog(LOG);
	LOG.info("Showing ArgoPrint Dialog");
	argoPrintDialog.show();
    }

    public void setModuleEnabled(boolean v) { }
    
    public boolean initializeModule() {
        LOG.info ("+-----------------------------+");
        LOG.info ("| ArgoPrint initialized       |");
        LOG.info ("+-----------------------------+");
   	return true;
    }

    public Object[] buildContext(JMenuItem a, String b) {
        return new Object[] {
	    a, b
	};
    }

    public boolean inContext(Object[] o) {
        if (o.length < 2) return false;
       
	if ((o[0] instanceof JMenuItem) && ("Tools".equals(o[1]))) {
	    return true;
	}
        return false;
    }
    public boolean isModuleEnabled() { return true; }
    public Vector getModulePopUpActions(Vector v, Object o) { return null; }
    public boolean shutdownModule() { return true; }

    public String getModuleName() { return "ArgoPrintMenuPlugin"; }
    public String getModuleDescription() { return "Menu Item for ArgoPrint"; }
    public String getModuleAuthor() { return "Mattias Danielsson"; }
    public String getModuleVersion() { return "0.0.1"; }
    public String getModuleKey() { return "module.argoprint.menu.plugins"; }

    public JMenuItem getMenuItem(JMenuItem mi, String s) {
        return getMenuItem(buildContext(mi, s));
    }

    public JMenuItem getMenuItem(Object [] context) {
        if (!inContext(context)) {
	    return null;
	}

        JMenuItem _menuItem = new JMenuItem("ArgoPrint");
	_menuItem.addActionListener(this);
        return _menuItem;
    }
}





