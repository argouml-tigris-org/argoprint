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

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.ProjectBrowser;

import org.argouml.model.ModelFacade;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;


/**
 *  @author Thierry Lach
 *  @since  0.9.4
 */
public class ArgoPrintMenuPlugin extends UMLAction
    implements PluggableMenu 
{
    /**
     * This is not publicly creatable.
     */
    protected ArgoPrintMenuPlugin() {
	super("Plugin menu test entry", false);
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
	
	ProjectBrowser pb = ProjectBrowser.getInstance();
	Project p =  ProjectManager.getManager().getCurrentProject();
	umlIf.setLog(Argo.log);
	umlIf.setProject(p);
	umlIf.setProjectBrowser(pb);
	
	
	String query = new String("getOwnedElements");
	if(umlIf.hasMethod(query)){
	    Argo.log.info("Method exists: " + query);
	} else {
	    Argo.log.info("Method does not exist: " + query);
	}

	Object response = umlIf.caller(query);

	if(response instanceof Collection){
	    Iterator elementIterator = ((Collection) response).iterator();

	    while(elementIterator.hasNext()){
		Object element = elementIterator.next();
		
		Argo.log.info("element name: " + ModelFacade.getFacade().getName(element));
		
	    }
	}
	
	//umlIf.testGetMember();

	//umlIf.trySaveAllDiagrams(true);
        	
	// This is where the ArgoPrint GUI frane is created and displayed.
        //
        //
        
        //JFrame myFrame = new JFrame("My JFrame");
    	//myFrame.setSize(400, 600);
    	//myFrame.setLocation(300,200);
    	//myFrame.getContentPane().add("Center", new JTextArea(10, 40));
    	//myFrame.show();
    	
     	/* Commented out by danielsson for testing
	JFrame myFrame = new JFrame("My Frame");
    	myFrame.setSize(500, 600);
    	
    	// center the frame on teh screen
    	Toolkit kit = myFrame.getToolkit();
    	Dimension screenSize = kit.getScreenSize();
    	int screenWidth = screenSize.width;
    	int screenHeight = screenSize.height;
    	Dimension windowSize = myFrame.getSize();
    	int windowWidth = windowSize.width;
    	int windowHeight = windowSize.height;
    	int upperLeftX = (screenWidth - windowWidth)/2;
    	int upperLeftY = (screenHeight - windowHeight)/2;
    
    	myFrame.setLocation(upperLeftX, upperLeftY);
    	//myFrame.setLocation(300,200);
    	myFrame.getContentPane().add("Center", new JTextArea(10, 40));
    	myFrame.show();
  	
	*/
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
