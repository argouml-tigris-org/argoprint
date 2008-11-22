// $Id:  $
// Copyright (c) 2008 The Regents of the University of California. All
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

package org.argoprint.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sun.misc.Service;

/**
 * This class is responsible for instantiating instances of templating engines.
 * All templating engines must be registered via the standard Java SPI mechanism
 * 
 * @author mfortner
 * @see http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider
 */
public class TemplateEngineFactory {
	
	private static List<TemplateEngine> templateEngineList = new ArrayList<TemplateEngine>();
	private static Map<String, TemplateEngine> templateEngineMap = new HashMap<String, TemplateEngine>();
	
	static{
		Iterator<TemplateEngine> it = Service.providers(org.argoprint.persistence.TemplateEngine.class);
		
		TemplateEngine engine = null;
		while(it.hasNext()){
			engine = it.next();
			templateEngineList.add(engine);
			String[] exts = engine.getTemplateExtensions();
			for (int i=0; i < exts.length; i++){
				templateEngineMap.put(exts[i], engine);
			}
		}
	}
	
	/**
	 * This method gets a list of all installed templating engines.
	 * @return
	 */
	public static List<TemplateEngine> getTemplateEngines(){
		return templateEngineList;
	}
	
	/**
	 * This method gets an instance of the template engine supported by this
	 * template engine.
	 * 
	 * @param ext  The file extension for a template.
	 * @return	An instance of the templating engine, or null if not found.
	 * @throws IllegalArgumentException if the extension parameter is null
	 */
	public static TemplateEngine getInstance(String ext){
		TemplateEngine engine = null;
		
		if (ext == null){
			throw new IllegalArgumentException("The extension cannot be null");
		}
		
		engine = templateEngineMap.get(ext);
		
		return engine;
	}

}
