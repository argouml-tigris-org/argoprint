// $Id$
// Own copyright info to be added!

package org.argouml.ui.argoprint;

import org.argouml.application.api.*;
import org.argouml.uml.ui.*;

import org.argoprint.uml_interface.UMLInterface;
import org.argoprint.ui.*;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.ProjectBrowser;

import org.argouml.model.ModelFacade;

import java.awt.*;
import java.awt.event.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;


/**
 *  @author Mattias Danielsson
 *  @since  0.0.1
 */
public class ArgoPrintMenuPlugin extends UMLAction
    implements PluggableMenu {
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
	Argo.log.info("Starting ArgoPrint");

	// This is where the ArgoPrint GUI frame is created and displayed
	ArgoPrintJDialog argoPrintDialog = 
	    new ArgoPrintJDialog(new javax.swing.JFrame(), true);
	Argo.log.info("Setting Gui Log");
	argoPrintDialog.setLog(Argo.log);
	Argo.log.info("Showing ArgoPrint Dialog");
	argoPrintDialog.show();
    }

    public void setModuleEnabled(boolean v) { }
    
    public boolean initializeModule() {
        Argo.log.info ("+-----------------------------+");
        Argo.log.info ("| ArgoPrint initialized       |");
        Argo.log.info ("+-----------------------------+");
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





