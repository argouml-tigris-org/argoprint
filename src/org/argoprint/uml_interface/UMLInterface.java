package org.argoprint.uml_interface;

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;

import org.argouml.ui.ArgoDiagram;
import org.argouml.ui.ProjectBrowser;

import org.argouml.util.FileFilters;
import org.argouml.util.SuffixFilter;
import org.argouml.util.osdep.OsUtil;

import org.argouml.model.ModelFacade;
import org.argouml.application.api.*;

import org.tigris.gef.base.CmdSaveEPS;
import org.tigris.gef.base.CmdSaveGIF;
import org.tigris.gef.base.CmdSaveGraphics;
import org.tigris.gef.base.CmdSavePS;
import org.tigris.gef.base.CmdSaveSVG;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.util.Util;

import java.util.*;
import java.io.*;
import java.awt.Rectangle;

import java.lang.reflect.*;
import java.lang.Class;

import org.tigris.gef.base.*;
import org.tigris.gef.persistence.*;


/** 
 *
 */

public class UMLInterface {
    public static final String separator = "/";

    private Project project;
    private ProjectBrowser projectBrowser;
    
    private ModelFacade facade; 

    //private Logger logg;
	
    ////////////////////////////////////////////////////////////////
    // constructors
    public UMLInterface() {
	//Argo.log.info("GraphConstructor");
	//super( "action.save-graphics", NO_ICON);
	facade = ModelFacade.getFacade(); 
	//logg = org.argouml.application.api.Notation.getLogger();
    }


    ////////////////////////////////////////////////////////////////
    // main methods
    public boolean hasMethod(String method){
	Class c = facade.getClass();
	Method[] theMethods = c.getMethods();
      
	for (int i = 0; i < theMethods.length; i++) {
	    if(method.equals(theMethods[i].getName())){
		return true;
	    }
	}
	return false;
    }


    public boolean trySaveAllDiagrams( boolean overwrite, 
				       ProjectBrowser pb, 
				       Project p, 
				       Logger log) {
	
	//fundering: RemoveElement.. kanske dödar orginalet så att det e 
	//därför det inte funkar andra gången!

	log.info("trySaveAllDiagrams");
	//Object target;
	Vector diagramVector =
	    p.getDiagrams();
	
	int diagramVectorSize = diagramVector.size();
	
	//while(!diagramVector.isEmpty()){
	for(int i = 0; i < diagramVectorSize; i++){
	    //Object target = diagramVector.firstElement(); 
	    //diagramVector.removeElementAt(0);
	    
	    Object target = diagramVector.elementAt(i); 
	    
	    if ( target instanceof Diagram ) {
		String defaultName = ((Diagram) target).getName();
		log.info("active diagram" + p.getActiveDiagram().getName());
		p.setActiveDiagram((ArgoDiagram) target);
		log.info("active diagram" + p.getActiveDiagram().getName());

		defaultName = Util.stripJunk(defaultName);

		log.info("diagram name " + defaultName);

		// FIX - It's probably worthwhile to abstract and factor
		// this chooser and directory stuff. More file handling is
		// coming, I'm sure.

		
		try {
		    //JFileChooser chooser = null;
		    File defFile = 
			new File("/home/pum3/danielsson/" + 
				 defaultName + "."
				 + FileFilters.GIFFilter._suffix);

		    log.info("diagram filename " + defaultName + "."
			     + FileFilters.GIFFilter._suffix);

		    //int retval = chooser.showSaveDialog( pb );
		    //if ( retval == 0 ) {
		    //File theFile = chooser.getSelectedFile();
		    
		    if (defFile != null) {
			String path = defFile.getParent();
			log.info("diagram path " + path); 

			String name = defFile.getName();
			log.info("diagram name " + name);

			String extension = SuffixFilter.getExtension(defFile);
			log.info("diagram ext " + extension);
     
			CmdSaveGraphics cmd = null;
			    
			cmd = new CmdSaveGIF();
			    

			if ( !path.endsWith( separator ) ) {
			    path += separator;
			}
			
    
			pb.showStatus( "Writing " + path + name + "..." );
			log.info( "Writing " + path + name + "..." );    

			if ( defFile.exists() && !overwrite ) {
			    String t = "Overwrite " + path + name;
			    int response =
				JOptionPane.showConfirmDialog(pb, t, t,
							      JOptionPane.YES_NO_OPTION);
			    if (response == JOptionPane.NO_OPTION) return false;
			}
			    
			FileOutputStream fo = new FileOutputStream( defFile );
			cmd.setStream(fo);
			cmd.doIt();
			fo.close();
			pb.showStatus( "Wrote " + path + name );
			log.info( "Wrote " + path + name + "..." );
			//return true;
		    }
		    
		}
		catch ( FileNotFoundException ignore )
		    {
			//cat.error("got a FileNotFoundException", ignore);
		    }
		catch ( IOException ignore )
		    {
			//cat.error("got an IOException", ignore);
		    }
		
		//diagramVector.removeElementAt(0);
	    }

	    //return false;
	}
	return true;
    }/*end of method save all diags */
   
} /* end class ActionSaveGraphics */
