//$Id$
//Copyright (c) 2003-2004, Mikael Albertsson, Mattias Danielsson, Per Engström, 
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.model.ModelFacade;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.argouml.util.FileFilters;
import org.argouml.util.SuffixFilter;
import org.tigris.gef.base.CmdSaveGIF;
import org.tigris.gef.base.CmdSaveGraphics;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.util.Util;

/** 
 * The class ArgoPrint uses to interface to the ArgoUML model.
 * It primarily communicates with ModelFacade using java.lang.reflect.*
 * To save diagrams gef is used. TODO: fix bug that causes only
 * the open diagram to be saved correctly.
 *
 * @author matda701, Mattias Danielsson
 */
public class UMLInterface 
    implements ArgoPrintDataSource {
    private static final Logger LOG = Logger.getLogger(UMLInterface.class);
    
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
     * Classes to search for methods.
     */
    private List _classes; 

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
	//_classes = new ModelFacade(); 
	_classes = new ArrayList();
	_classes.add(new ModelFacade());
	_classes.add(Model.getUmlFactory());
	_classes.add(Model.getCoreHelper());
	_classes.add(Model.getUmlFactory().getDataTypes());
	_classes.add(Model.getUmlFactory().getCore());
	_classes.add(Model.getUmlFactory().getCommonBehavior());
	_classes.add(Model.getUmlFactory().getUseCases());
	// TODO: Add all of them.
    }

    /**
     * Initializes fields prior to usage. Can be used instead of the 
     * individual setters.
     */
    public void initialize() {
	_projectBrowser = ProjectBrowser.getInstance();
	_project = ProjectManager.getManager().getCurrentProject();
    }

    ////////////////////////////////////////////////////////////////
    // setters

    /**
     * Sets the projectBrowswer.
     * 
     * @param browser The browser.
     */
    public void setProjectBrowser(ProjectBrowser browser) {
	_projectBrowser = browser;
    }
    
    /**
     * Sets the project.
     * 
     * @param proj The project.
     */
    public void setProject(Project proj) { 
        _project = proj; 
    }

    /**
     * Sets the outputPath.
     * 
     * @param dir The path.
     */
    public void setOutputDir(String dir) { 
        _outputDir = dir; 
    }

    ////////////////////////////////////////////////////////////////
    // main methods
        
    /**
     * @see ArgoPrintDataSource#caller(String, Object)
     * 
     * Calls method named call in ModelFacade 
     * returns Object, which often is String or Collection
     * iteratorObject is the argument for the method 
     */
    public Object caller(String call, Object iteratorObject) {
	//_log.info("Arg call: " + call + " Arg: " + 
	//	  _classes.getName(iteratorObject)); 
	Iterator iter = _classes.iterator();
	while (iter.hasNext()) {
	    
	    Class c = (iter.next()).getClass();
	    Method[] theMethods = c.getMethods();   
	
	    Object args[] = new Object[1];
	    args[0] = iteratorObject;

	    if (call.endsWith("()")) {
		int callLength = call.length() - 2;
	    
		String callName = new String(call.substring(0, callLength));
		for (int i = 0; i < theMethods.length; i++) {
		
		    if (callName.equals(theMethods[i].getName())) {
			try {
			    return theMethods[i].invoke(null, args);
			}
			catch (IllegalAccessException e) {
			    LOG.info("Crash" + e.getMessage());
			}
			catch (IllegalArgumentException e) {
			    LOG.info("Crash" + e.getMessage());
			}
			catch (InvocationTargetException e) {
			    LOG.info("Crash" + e.getMessage());
			}
			catch (NullPointerException e) {
			    LOG.info("Crash" + e.getMessage());
			}
			catch (ExceptionInInitializerError e) {
			    LOG.info("Crash" + e.getMessage());
			}		   
		    }	    
		}
	    }
	}
	//throw new Exception("Illegal method call");
	return "Not a known method";
    }

    
    /**
     * Calls method named call in ModelFacade. Used when argument is to
     * be on of the default. ex. calledMethodName(model) and not an
     * iteratorObject.
     * 
     * @param call The string to call.
     * @return The result of the operation.
     * @throws UnsupportedCallException if the call was incorrectly made.
     */
    public Object caller(String call) throws UnsupportedCallException {
	
	Iterator iter = _classes.iterator();

	while (iter.hasNext()) {
	    Object obj = iter.next();
	    Class c = obj.getClass();

	    if (call.endsWith("()")) {

		Method[] theMethods = c.getMethods();
		int callLength = call.length() - 2;

		System.out.println("Class " + c + " with "
				   + theMethods.length + " methods: ");

		String callName = new String(call.substring(0, callLength));
		for (int i = 0; i < theMethods.length; i++) {
		    if (callName.equals(theMethods[i].getName())) {
			try {
			    System.out.println("Trying: " 
					       + c + "." + callName + "()");
			    return theMethods[i].invoke(obj, null);
			}
			catch (IllegalAccessException ignore ) {
			    System.out.println("IllegalAccessException: "
					       + ignore);
			    ignore.printStackTrace();
			}
			catch (IllegalArgumentException ignore ) {
			    System.out.println("IllegalArgumentException: "
					       + ignore);
			    ignore.printStackTrace();
			}
			catch (InvocationTargetException ignore ) {
			    System.out.println("InvocationTargetException: "
					       + ignore);
			    ignore.printStackTrace();
			}
			catch (NullPointerException ignore ) {
			    System.out.println("NullPointerException: "
					       + ignore);
			    ignore.printStackTrace();
			}
			catch (ExceptionInInitializerError ignore ) {
			    System.out.println("ExceptionInInitializerError: "
					       + ignore);
			    ignore.printStackTrace();
			}		   
		    }	    
		}
	    } else if (call.endsWith(")")) {

		Method[] theMethods = c.getMethods();
		int callLength = call.indexOf('(') + 1;
	    
		String callName = new String(call.substring(0, callLength - 1));
		String arg = 
		    new String(call.substring(callLength, call.length() - 1));
		Object args[] = new Object[1];  
		Object thisObject = null;

		if (arg.equals("model")) {
		    args[0] = _project.getModel();
		} else if (arg.equals("project")) {
		    thisObject = _project;
		    args = null;
		    c = _project.getClass();
		    int vectorLen = c.getMethods().length;
		    theMethods = new Method[vectorLen];
		    theMethods = c.getMethods();
		}
 
		for (int i = 0; i < theMethods.length; i++) {
		    if (callName.equals(theMethods[i].getName())) {	
		        //_log.info("Call hit: " + arg + " " + callName);
		        //Object args[] = new Object[1];
		        //args[0] = _project.getModel(); 
		        try {
		            return theMethods[i].invoke(thisObject, 
		                    args);
		        } catch (IllegalArgumentException e) {
		            throw new UnsupportedCallException(e);
		        } catch (IllegalAccessException e) {
		            throw new UnsupportedCallException(e);
		        } catch (InvocationTargetException e) {
		            throw new UnsupportedCallException(e);
		        }
		    }	    
		}

		//return null;
	    }
	}

	throw new UnsupportedCallException("Illegal method call: " + call);
	//return "Illegal method call";
    }
    
    /**
     * Saves a diagram as gif in the directory specified by _outputDir.
     * Returns a String with the path to the saved gif file.
     * TODO: Solve same Bug as in trySaveAllDiagrams() and implement 
     * better control for overwrite of old files.
     * 
     * @param diagram The diagram to save.
     * @return The name of the file created.
     * @throws IOException if there is some problem with the creation 
     *         of the files.
     * @throws UnsupportedCallException if we cannot construct the diagram.
     */
    public String saveDiagram(UMLDiagram diagram) 
    	throws IOException, UnsupportedCallException {
        
	//Todo: fix bug mentioned i trySaveAllDiagrams

	if (diagram != null) {
	    String defaultName = ((Diagram) diagram).getName();
	    LOG.info("active diagram" + 
		      _project.getActiveDiagram().getName());
	    _project.setActiveDiagram(diagram);
	    LOG.info("active diagram" + 
		      _project.getActiveDiagram().getName());
	    
	    defaultName = Util.stripJunk(defaultName);
	    
	    LOG.info("diagram name " + defaultName);
	    
	    File defFile = 
		new File(_outputDir 
			 + defaultName + "."
			 + FileFilters.GIF_FILTER.getSuffix());
		
	    LOG.info("diagram filename " + defaultName + "."
		      + FileFilters.GIF_FILTER.getSuffix());

	    if (defFile != null) {
		String path = defFile.getParent();
		LOG.info("diagram path " + path); 
		    
		String name = defFile.getName();
		LOG.info("diagram name " + name);
		    
		String extension = SuffixFilter.getExtension(defFile);
		LOG.info("diagram ext " + extension);
		    
		CmdSaveGraphics cmd = null;
			
		cmd = new CmdSaveGIF();
			    

		if ( !path.endsWith( "/" ) ) {
		    path += "/";
		}
			
		_projectBrowser.showStatus( "Writing " + path + name + "..." );
		LOG.info( "Writing " + path + name + "..." );    

		FileOutputStream fo = new FileOutputStream( defFile );
		cmd.setStream(fo);
		cmd.doIt();
		fo.close();
		_projectBrowser.showStatus( "Wrote " + path + name );
		LOG.info( "Wrote " + path + name + "..." );
		//return true;
		return path + name;
	    }
	}
	
	throw new UnsupportedCallException("Not a valid diagram");
    }
} /* end class UMLInteface */







