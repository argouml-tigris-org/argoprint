package org.argoprint.uml_interface;

import org.argoprint.*;

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
import org.argouml.kernel.ProjectMember;

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
import java.lang.Boolean;

import org.tigris.gef.base.*;
import org.tigris.gef.persistence.*;

/** 
 * The class ArgoPrint uses to interface to the ArgoUML model.
 * It primarily communicates with ModelFacade using java.lang.reflect.*
 * To save diagrams gef is called. //TODO: fix bug that causes only
 * the open diagram to be saved correctly.
 */
public class UMLInterface 
    implements ArgoPrintDataSource{
    public static final String separator = "/";

    /**
     * The ArgoUML project that ArgoPrint is applied to. Must be
     * set prior to use by using the setProject(..) method
     */
    private Project project;

    /**
     * The ArgoUML projectbrowser that ArgoPrint is applied to. Must be
     * set prior to use by using the setProjectBrowser(..) method
     */
    private ProjectBrowser projectBrowser;
    
    /**
     * A reference to the ModelFacade
     */
    private ModelFacade facade; 

    /**
     * A reference to the ArgoUML Logger. (Uses 
     */
    private Logger log;
	
    /**
     * The ArgoPrint output dir. Used when saving diagrams as pictures. Must be set prior to use.
     */
    String outputPath;

    ////////////////////////////////////////////////////////////////
    // constructors
    public UMLInterface() {
	//super( "action.save-graphics", NO_ICON);
	facade = ModelFacade.getFacade();
    }

    ////////////////////////////////////////////////////////////////
    // setters

    /**
     * Sets the logger (log) to logger
     */
    public void setLog(Logger logger){ log = logger; }
    
    /**
     * Sets the projectBrowswer to browser
     */
    public void setProjectBrowser(ProjectBrowser browser){
	projectBrowser = browser;
    }
    
    /**
     * Sets the project to proj
     */
    public void setProject(Project proj){ project = proj; }

    /**
     * Sets the outputPath to path
     */
    public void setOutputPath(String path){ outputPath = new String(path); }

    ////////////////////////////////////////////////////////////////
    // main methods
    
    /**
     * Checks if ModelFacade has a method named method. Depracated!
     */
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
    
    /**
     * Testing function used for testing during development 
     */
    public void testGetMember(){
	Vector memberVector = project.getMembers();
	int memberVectorSize = memberVector.size();

	Object model = project.getModel();
	
	if(facade.isAModel(model)){
	    log.info("is a model");
	} else {
	    log.info("is not a model");
	}	
	
	Collection elementCollection = facade.getOwnedElements(model);
	Iterator elementIterator = elementCollection.iterator();

	while(elementIterator.hasNext()){
	    Object element = elementIterator.next();
	    if(facade.isAClass(element)){
		log.info("class name: " + facade.getName(element));
	    } else if(facade.isAActor(element)) {
		log.info("actor name: " + facade.getName(element));
	    } else if(facade.isAUseCase(element)) {
		log.info("usecase name: " + facade.getName(element));
	    } else {
		log.info("element name: " + facade.getName(element));
	    }
	}
	
	for(int i = 0; i < memberVectorSize; i++){
	    log.info("member name " + 
		     ((ProjectMember) memberVector.elementAt(i)).getName()); 
	}
    }

    /**
     * Calls method named call in ModelFacade 
     * returns Object, which often is String or Collection
     */
    public Object caller(String call, Object iteratorObject)
	throws Exception{
	
	Class c = facade.getClass();
	Method[] theMethods = c.getMethods();   
	
	Object args[] = new Object[1];
	args[0] = iteratorObject;

	if(call.endsWith(new String("()"))){
	    int callLength = call.length()-2;
	    
	    String callName = new String(call.substring(0, callLength));
	    //log.info(callName);
	    for (int i = 0; i < theMethods.length; i++) {
		
		if(callName.equals(theMethods[i].getName())){
		    //log.info("method hit " + callName);
		    try{
			Object obj = theMethods[i].invoke(null, args);
			//log.info("object: " + obj.toString());
			return obj; //theMethods[i].invoke(null, args);
			//break;
		    }
		    catch (IllegalAccessException e){
			log.info("Crash" + e.getMessage());
		    }
		    catch (IllegalArgumentException e){
			log.info("Crash" + e.getMessage());
		    }
		    catch (InvocationTargetException e){
			log.info("Crash" + e.getMessage());
		    }
		    catch (NullPointerException e){
			log.info("Crash" + e.getMessage());
		    }
		    catch (ExceptionInInitializerError e){
			log.info("Crash" + e.getMessage());
		    }		   
		}	    
	    }
	}
	 //should throw exception
	return new String("Not a known method");
    }
    
    public Object caller(String call)
	throws Exception{
	
	Class c = facade.getClass();
	Method[] theMethods = c.getMethods();

	if(call.endsWith(new String("()"))){
	    int callLength = call.length()-2;
	    
	    String callName = new String(call.substring(0, callLength - 1));
	    
	    for (int i = 0; i < theMethods.length; i++) {
		if(callName.equals(theMethods[i].getName())){
		    try{
			return theMethods[i].invoke(null, null);
		    }
		    catch (IllegalAccessException ignore ){
		    }
		    catch (IllegalArgumentException ignore ){
		    }
		    catch (InvocationTargetException ignore ){
		    }
		    catch (NullPointerException ignore ){
		    }
		    catch (ExceptionInInitializerError ignore ){
		    }		   
		}	    
	    }
	} else if(call.endsWith(new String(")"))){
	    
	    
	    int callLength = call.indexOf((int) '(') + 1;
	    String callName = new String(call.substring(0, callLength - 1));
	    String arg = 
		new String(call.substring(callLength, call.length() - 1));
	    //log.info("call name: " + callName + "call arg: " + arg);
	    
	    for (int i = 0; i < theMethods.length; i++) {
		if(callName.equals(theMethods[i].getName())){
		    try{
			if(arg.equals(new String("model"))){		    
			    Object args[] = new Object[1];
			    args[0] = project.getModel(); 
			    return theMethods[i].invoke(null, 
							args);
			}
		    }
		    catch (IllegalAccessException ignore ){
		    }
		    catch (IllegalArgumentException ignore ){
		    }
		    catch (InvocationTargetException ignore ){
		    }
		    catch (NullPointerException ignore ){
		    }
		    catch (ExceptionInInitializerError ignore ){
		    }		   
		}	    
	    }
	    
	    return null;
	}
	return new String("Illegal method call");
    }
    
    /**
     * Calls method named call in ModelFacade 
     * returns boolean, caller(..) can be used instead but
     * then Booolean.booleanValue() must be used 
     */
    public boolean booleanCaller(String call, Object args[]){
	if(hasMethod(call)){
	    Class c = facade.getClass();
	    Method[] theMethods = c.getMethods();   
	    
	    for (int i = 0; i < theMethods.length; i++) {
		if(call.equals(theMethods[i].getName())){
		    try{
			Object answer = theMethods[i].invoke(null, args);
			return ((Boolean)answer).booleanValue();
			//break;
		    }
		    catch(IllegalAccessException ignore ){
			
		    }
		    catch(IllegalArgumentException ignore ){
		       
		    }
		    catch(InvocationTargetException ignore ){
			
		    }
		    catch(NullPointerException ignore ){
			
		    }
		    catch(ExceptionInInitializerError ignore ){
			
		    }		    
		}		
	    }
	}
	//should be throw exeption
	return false;
	//return new String("Not a known method");
    }

    /**
     * Tests saving of diagrams as gif-files.
     * TODO: Solve bug that causes only open diagram to
     * be saved. 
     */
    public boolean trySaveAllDiagrams( boolean overwrite ) {
	
	log.info("trySaveAllDiagrams started");

	Vector diagramVector =
	    project.getDiagrams();
	
	int diagramVectorSize = diagramVector.size();
		
	for(int i = 0; i < diagramVectorSize; i++){
	    Object target = diagramVector.elementAt(i); 
	    
	    if( target instanceof Diagram ) {
		String defaultName = ((Diagram) target).getName();
		log.info("active diagram" + 
			 project.getActiveDiagram().getName());
		project.setActiveDiagram((ArgoDiagram) target);
		log.info("active diagram" + 
			 project.getActiveDiagram().getName());

		defaultName = Util.stripJunk(defaultName);

		log.info("diagram name " + defaultName);

		try {
		    File defFile = 
			new File("/home/pum3/danielsson/" + 
				 defaultName + "."
				 + FileFilters.GIFFilter._suffix);

		    log.info("diagram filename " + defaultName + "."
			     + FileFilters.GIFFilter._suffix);

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
			
    			projectBrowser.showStatus( "Writing " + path + name + "..." );
			log.info( "Writing " + path + name + "..." );    

			if ( defFile.exists() && !overwrite ) {
			    String t = "Overwrite " + path + name;
			    int response =
				JOptionPane.showConfirmDialog(projectBrowser,
							      t, t,
							      JOptionPane.YES_NO_OPTION);
			    if (response == JOptionPane.NO_OPTION) return false;
			}
			    
			FileOutputStream fo = new FileOutputStream( defFile );
			cmd.setStream(fo);
			cmd.doIt();
			fo.close();
			projectBrowser.showStatus( "Wrote " + path + name );
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
   
} /* end class UMLInteface */







