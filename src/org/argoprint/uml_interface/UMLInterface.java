//$Id$
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

import org.argouml.uml.diagram.ui.*;

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
 * To save diagrams gef is used. TODO: fix bug that causes only
 * the open diagram to be saved correctly.
 *
 * @author matda701, Mattias Danielsson
 */
public class UMLInterface 
    implements ArgoPrintDataSource{
    public static final String separator = "/";

    /**
     * The ArgoUML project that ArgoPrint is applied to. Must be
     * set prior to use by using the setProject(..) method
     */
    private Project _project;

    /**
     * The ArgoUML projectbrowser that ArgoPrint is applied to. Must be
     * set prior to use by using the setProjectBrowser(..) method
     */
    private ProjectBrowser _projectBrowser;
    
    /**
     * A reference to the ModelFacade
     */
    private ModelFacade _facade; 

    /**
     * A reference to the ArgoUML Logger. (Uses log4java) 
     */
    private Logger _log;
	
    /**
     * The ArgoPrint output dir. Used when saving diagrams as pictures. 
     * Must be set prior to use.
     */
    String _outputDir;

    ////////////////////////////////////////////////////////////////
    // constructors
    
    /**
     * Constructor
     */
    public UMLInterface() {
	//super( "action.save-graphics", NO_ICON);
	//_facade = new ModelFacade(); 
	_facade = ModelFacade.getFacade();
    }

    /**
     * Initializes fields prior to usage. Can be used instead of the 
     * individual setters.
     */
    public void initialize(Logger log){
	_log = log;
	_projectBrowser = ProjectBrowser.getInstance();
	_project = ProjectManager.getManager().getCurrentProject();
    }

    ////////////////////////////////////////////////////////////////
    // setters

    /**
     * Sets the logger (_log) to logger
     */
    public void setLog(Logger logger){ _log = logger; }
    
    /**
     * Sets the projectBrowswer to browser
     */
    public void setProjectBrowser(ProjectBrowser browser){
	_projectBrowser = browser;
    }
    
    /**
     * Sets the project to proj
     */
    public void setProject(Project proj){ _project = proj; }

    /**
     * Sets the outputPath to path
     */
    public void setOutputDir(String dir){ _outputDir = new String(dir); }

    ////////////////////////////////////////////////////////////////
    // main methods
    
    /**
     * Checks if ModelFacade has a method named method. Depracated!
     */
    public boolean hasMethod(String method){
	Class c = _facade.getClass();
	Method[] theMethods = c.getMethods();
      
	for (int i = 0; i < theMethods.length; i++) {
	    if(method.equals(theMethods[i].getName())){
		return true;
	    }
	}
	return false;
    }
    
    /**
     * Calls method named call in ModelFacade 
     * returns Object, which often is String or Collection
     * iteratorObject is the argument for the method 
     */
    public Object caller(String call, Object iteratorObject)
	throws Exception{
	//_log.info("Arg call: " + call + " Arg: " + 
	//	  _facade.getName(iteratorObject)); 
	Class c = _facade.getClass();
	Method[] theMethods = c.getMethods();   
	
	Object args[] = new Object[1];
	args[0] = iteratorObject;

	if(call.endsWith(new String("()"))){
	    int callLength = call.length()-2;
	    
	    String callName = new String(call.substring(0, callLength));
	    for (int i = 0; i < theMethods.length; i++) {
		
		if(callName.equals(theMethods[i].getName())){
		    try{
			return theMethods[i].invoke(null, args);
		    }
		    catch (IllegalAccessException e){
			_log.info("Crash" + e.getMessage());
		    }
		    catch (IllegalArgumentException e){
			_log.info("Crash" + e.getMessage());
		    }
		    catch (InvocationTargetException e){
			_log.info("Crash" + e.getMessage());
		    }
		    catch (NullPointerException e){
			_log.info("Crash" + e.getMessage());
		    }
		    catch (ExceptionInInitializerError e){
			_log.info("Crash" + e.getMessage());
		    }		   
		}	    
	    }
	}
	//throw new Exception("Illegal method call");
	return new String("Not a known method");
    }

    
    /**
     * Calls method named call in ModelFacade. Used when argument is to
     * be on of the default. ex. calledMethodName(model) and not an
     * iteratorObject
     */
    public Object caller(String call)
	throws Exception{
	//_log.info("call: " + call);
	//Class c = _facade.getClass();
	//Method[] theMethods = c.getMethods();

	if(call.endsWith(new String("()"))){
	   Class c = _facade.getClass();
	   Method[] theMethods = c.getMethods();
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
	    Class c= _facade.getClass();;
	    Method[] theMethods = c.getMethods();
	    int callLength = call.indexOf((int) '(') + 1;
	    
	    String callName = new String(call.substring(0, callLength - 1));
	    String arg = 
		new String(call.substring(callLength, call.length() - 1));
	    Object args[] = new Object[1];  
	    Object thisObject = null;

	    if(arg.equals(new String("model"))){
		args[0] = _project.getModel();
		//c = _facade.getClass();
		//theMethods = c.getMethods();
	    } else if(arg.equals(new String("project"))){
		thisObject = _project;
		args = null;
		c = _project.getClass();
		int vectorLen = c.getMethods().length;
		theMethods = new Method[vectorLen];
		theMethods = c.getMethods();
	    } else {
		//c = _facade.getClass();
		//theMethods = c.getMethods();
	    }
 
	    for (int i = 0; i < theMethods.length; i++) {
		if(callName.equals(theMethods[i].getName())){
		    try{	
			//_log.info("Call hit: " + arg + " " + callName);
			//Object args[] = new Object[1];
			//args[0] = _project.getModel(); 
			return theMethods[i].invoke(thisObject, 
						    args);
		    }catch(Exception e){
			throw e;
		    }
		}	    
	    }
	    
	    //return null;
	}
	throw new Exception("Illegal method call");
	//return new String("Illegal method call");
    }
    
    /**
     * Calls method named call in ModelFacade 
     * returns boolean, caller(..) can be used instead but
     * then Booolean.booleanValue() must be used. Depracated! 
     */
    public boolean booleanCaller(String call, Object args[]){
	if(hasMethod(call)){
	    Class c = _facade.getClass();
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
     * Returns all diagrams in the project. TODO: Figure out a clever
     * way to invoke with caller. Solved by using reflections on Project
     * Therefor not needed.
     */    
    public Collection getAllDiagrams(){
	return _project.getDiagrams();
    }


    /**
     * Saves a diagram as gif in the directory specified by _outputDir.
     * Returns a String with the path to the saved gif file.
     * TODO: Solve same Bug as in trySaveAllDiagrams() and implement 
     * better control for overwrite of old files. 
     */
    public String saveDiagram(UMLDiagram diagram) 
	throws Exception{
	//Todo: fix bug mentioned i trySaveAllDiagrams

	if( diagram instanceof Diagram ) {
	    String defaultName = ((Diagram) diagram).getName();
	    _log.info("active diagram" + 
		      _project.getActiveDiagram().getName());
	    _project.setActiveDiagram((ArgoDiagram) diagram);
	    _log.info("active diagram" + 
		      _project.getActiveDiagram().getName());
	    
	    defaultName = Util.stripJunk(defaultName);
	    
	    _log.info("diagram name " + defaultName);
	    
	    try {
		File defFile = 
		    new File(_outputDir + 
			     defaultName + "."
			     + FileFilters.GIFFilter._suffix);
		
		_log.info("diagram filename " + defaultName + "."
			  + FileFilters.GIFFilter._suffix);
		
		if (defFile != null) {
		    String path = defFile.getParent();
		    _log.info("diagram path " + path); 
		    
		    String name = defFile.getName();
		    _log.info("diagram name " + name);
		    
		    String extension = SuffixFilter.getExtension(defFile);
		    _log.info("diagram ext " + extension);
		    
		    CmdSaveGraphics cmd = null;
			
		    cmd = new CmdSaveGIF();
			    

		    if ( !path.endsWith( separator ) ) {
			path += separator;
		    }
			
		    _projectBrowser.showStatus( "Writing " + path + name + "..." );
		    _log.info( "Writing " + path + name + "..." );    

		    if ( defFile.exists() ) {
			String t = "Overwrite " + path + name;
			int response =
			    JOptionPane.showConfirmDialog(_projectBrowser,
							  t, t,
							  JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.NO_OPTION){ 
			    throw new Exception("Cannot overwrite file");
			}
		    }
		    
		    FileOutputStream fo = new FileOutputStream( defFile );
		    cmd.setStream(fo);
		    cmd.doIt();
		    fo.close();
		    _projectBrowser.showStatus( "Wrote " + path + name );
		    _log.info( "Wrote " + path + name + "..." );
		    //return true;
		    return new String(path + name);
		}
		
	    }
	    catch ( FileNotFoundException ignore )
		{
		    throw ignore;
		}
	    catch ( IOException ignore )
		{
		    throw ignore;
		}
	}
	
	throw new Exception("Not a valid diagram");
    }

    /**
     * Tests saving of diagrams as gif-files.
     * TODO: Solve bug that causes only open diagram to
     * be saved. 
     */
    public boolean trySaveAllDiagrams( boolean overwrite ) {
	
	_log.info("trySaveAllDiagrams started");

	Vector diagramVector =
	    _project.getDiagrams();
	
	int diagramVectorSize = diagramVector.size();
		
	for(int i = 0; i < diagramVectorSize; i++){
	    Object target = diagramVector.elementAt(i); 
	    
	    if( target instanceof Diagram ) {
		String defaultName = ((Diagram) target).getName();
		_log.info("active diagram" + 
			  _project.getActiveDiagram().getName());
		_project.setActiveDiagram((ArgoDiagram) target);
		_log.info("active diagram" + 
			_project.getActiveDiagram().getName());

		defaultName = Util.stripJunk(defaultName);

		_log.info("diagram name " + defaultName);

		try {
		    File defFile = 
			new File("/home/pum3/danielsson/" + 
				 defaultName + "."
				 + FileFilters.GIFFilter._suffix);

		    _log.info("diagram filename " + defaultName + "."
			     + FileFilters.GIFFilter._suffix);

		    if (defFile != null) {
			String path = defFile.getParent();
			_log.info("diagram path " + path); 

			String name = defFile.getName();
			_log.info("diagram name " + name);

			String extension = SuffixFilter.getExtension(defFile);
			_log.info("diagram ext " + extension);
     
			CmdSaveGraphics cmd = null;
			    
			cmd = new CmdSaveGIF();
			    

			if ( !path.endsWith( separator ) ) {
			    path += separator;
			}
			
			_projectBrowser.showStatus( "Writing " + path + name + "..." );
			_log.info( "Writing " + path + name + "..." );    

			if ( defFile.exists() && !overwrite ) {
			    String t = "Overwrite " + path + name;
			    int response =
				JOptionPane.showConfirmDialog(_projectBrowser,
							      t, t,
							      JOptionPane.YES_NO_OPTION);
			    if (response == JOptionPane.NO_OPTION) return false;
			}
			    
			FileOutputStream fo = new FileOutputStream( defFile );
			cmd.setStream(fo);
			cmd.doIt();
			fo.close();
			_projectBrowser.showStatus( "Wrote " + path + name );
			_log.info( "Wrote " + path + name + "..." );
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







