// $Id$
// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argoprint;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/** Central point for resource locations */

public class APResources {

    // File Icons
    public static final String ICON_NEW
	= "/org/argoprint/Images/new.png";
    public static final String ICON_OPEN
	= "/org/argoprint/Images/open.png";
    public static final String ICON_SAVE
	= "/org/argoprint/Images/save.png";

    public static final String ICON_DOT_BLUE
	= "/org/argoprint/Images/blue_dot.png";
    public static final String ICON_DOT_GREEN
	= "/org/argoprint/Images/green_dot.png";
    public static final String ICON_DOT_RED
	= "/org/argoprint/Images/red_dot.png";

   
    // Cell Editor Icons
    public static final String ICON_ADD
	= "/org/argoprint/Images/add.png";
    public static final String ICON_CONFIRM
	= "/org/argoprint/Images/confirm.png";
    public static final String ICON_REMOVE
	= "/org/argoprint/Images/remove.png";

    // XML Schemas
    public static final String SCHEMA_XSLT
	= "/org/argoprint/Schemas/xslt_simpl.rng";
    public static final String SCHEMA_DOCBOOK
	= "/org/argoprint/Schemas/docbook_simpl.rng";

    public static final String MANAGER_DATA_FILENAME
	= "manager.apx";

    public static URL getResource(String id) {
	return APResources.class.getResource(id);
    }

    public static Document getDOMFromJAR(String rep) {
	Document result = null;
	try {
	    InputStream in = getResource(rep)
		.openConnection()
		.getInputStream();
	    
	    result = javax.xml.parsers.DocumentBuilderFactory
		.newInstance()
		.newDocumentBuilder()
		.parse(in);
	    
	} catch (org.xml.sax.SAXException ex) {
	    ex.printStackTrace();
	} catch (java.io.IOException ex) {
	    ex.printStackTrace();
	} catch (javax.xml.parsers.ParserConfigurationException ex) {
	    ex.printStackTrace();
	}
	return result;
    }
}
