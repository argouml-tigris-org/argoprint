// $Id ArgoPrintEditorModel.java,v 0.1 2006/06/21 17:26 comp_ Exp $
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

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.argoprint.ui.DocumentTreeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ArgoPrintEditorModel
    implements DocumentSource {

    private Document document;
    private EventListenerList listenerList;

    public ArgoPrintEditorModel() {
	document = ArgoPrintDocumentFactory.createEmptyDocument();
	listenerList = new EventListenerList();
    }

    public void addDocumentSourceListener(DocumentSourceListener listener) {
	listenerList.add(DocumentSourceListener.class, listener);
    }

    public void removeDocumentSourceListener(DocumentSourceListener listener) {
	listenerList.remove(DocumentSourceListener.class, listener);
    }

    public void fireEvent(short type) {
	Object[] list = listenerList.getListenerList();
	DocumentSourceEvent event = new DocumentSourceEvent(this, type);
	for (int i = list.length - 2; i >= 0; i -= 2)
         if (list[i] == DocumentSourceListener.class)
             ((DocumentSourceListener)list[i + 1]).documentSourceChanged(event);
    }
    
    public void setDocument(Document document) {
	this.document = document;
	fireEvent(DocumentSourceEvent.DOCUMENT_CHANGED);
    }

    public Document getDocument() {
	return document;
    }

    public void newDocument() {
	document = ArgoPrintDocumentFactory.createEmptyDocument();
	fireEvent(DocumentSourceEvent.DOCUMENT_CHANGED);
    }

    public void loadDocument(File file)
	throws
	    java.io.IOException,
	    org.xml.sax.SAXException {

	try {
	    document = DocumentBuilderFactory
		.newInstance()
		.newDocumentBuilder()
		.parse(file);
	} catch (javax.xml.parsers.ParserConfigurationException ex) {
	    // TODO
	    System.err.println(ex);
	}
	fireEvent(DocumentSourceEvent.DOCUMENT_CHANGED);
    }

    public void saveDocument(File file) {
	// TODO: indentation of output
	try {
	    Transformer transformer = TransformerFactory
		.newInstance()
		.newTransformer();
	    transformer.transform(new DOMSource(document), new StreamResult(file));
	} catch (javax.xml.transform.TransformerConfigurationException ex) {
	    // TODO
	    System.err.println(ex);
	} catch (javax.xml.transform.TransformerException ex) {
	    // TODO
	    System.err.println(ex);
	}
	    
    }

//     public void removeSubtree(Node root) {
// 	try {
// 	    // does this remove all the nodes?
// 	    root.getParent().removeChild(root);
// 	} catch (org.w3c.dom.DOMException ex) {
// 	    // TODO
// 	    System.err.println(ex);
// 	}
//     }

}
