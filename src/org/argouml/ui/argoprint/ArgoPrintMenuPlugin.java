// $Id$
// Copyright (c) 1996-2002 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

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
import java.lang.reflect.*;
import java.lang.*;

import java.util.*;

import javax.swing.*;


/**
 *  @author Mattias Danielsson
 *  @since  0.0.1
 */
public class ArgoPrintMenuPlugin extends UMLAction
    implements PluggableMenu 
{
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
	Argo.log.info("User clicked on '" + event.getActionCommand() + "'");
	Argo.log.info("Starting ArgoPrint");
	UMLInterface umlIf = new UMLInterface();
	
	//setting argopprint for current project
	ProjectBrowser pb = ProjectBrowser.getInstance();
	Project p =  ProjectManager.getManager().getCurrentProject();
	umlIf.setLog(Argo.log);
	umlIf.setProject(p);
	umlIf.setProjectBrowser(pb);
	
	//testing "simulated" template
	Object args[] = new Object[1];
	args[0] = p.getModel();
	
	Object response = umlIf.caller(new String("getOwnedElements"), args);
	
	if(response instanceof Collection){
	    Iterator elementIterator = ((Collection) response).iterator();

	    while(elementIterator.hasNext()){
		Object element = elementIterator.next();
		args[0] = element;
		Object response2 = 
		    umlIf.caller(new String("isAClass"), args);
		
		if((response2 instanceof Boolean) && 
		   (((Boolean)response2).booleanValue())){
		    
		    Argo.log.info("Class name: " + 
				  ModelFacade.getFacade().getName(element));
		    
		    Object response3 = 
			umlIf.caller(new String("getOperations"), args);
		    
		    if(response3 instanceof Collection){
			Iterator operationIterator = 
			    ((Collection) response3).iterator(); 
			while(operationIterator.hasNext()){
			    Object operation = operationIterator.next();
			    Argo.log.info("operation name: " + 
				  ModelFacade.getFacade().getName(operation));
			}
		    }
		}
	    }
	}
	// This is where the ArgoPrint GUI frane is created and displayed
	new ArgoPrintJDialog(new javax.swing.JFrame(), true).show();

	//umlIf.testGetMember();

	//umlIf.trySaveAllDiagrams(true);
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
