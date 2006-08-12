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

package org.argoprint.ui;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.argoprint.DocumentSource;
import org.argoprint.DocumentSourceEvent;
import org.argoprint.DocumentSourceListener;
import org.argoprint.GuidedEditing;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NameList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Maps DOM Document objects to TreeModel objects and provides helper
 * functionality
 */

public class DocumentTreeModel
    implements TreeModel, DocumentSourceListener {

    private DocumentSource source;
    private EventListenerList listenerList;

    public DocumentTreeModel(DocumentSource source) {
	this.source = source;
	listenerList = new EventListenerList();
    }

    public Document getDocument() {
	return source.getDocument();
    }

    public Object getChild(Object parent, int index) {
	Object result = null;
	Node node = (Node) parent;

	if (index < node.getAttributes().getLength())
	    result = node.getAttributes().item(index);
	else
	    result = node
		.getChildNodes()
		.item(index - node.getAttributes().getLength());

	return result;
    }

    public int getChildCount(Object parent) {
	int result;

	if (parent instanceof Attr) 
	    result = 0;
	else {
	    Node node = (Node) parent;
	    result = node.getChildNodes().getLength();
	    if (node.hasAttributes())
		result += node.getAttributes().getLength();
	}

	return result;
    }

    public int getIndexOfChild(Object parent, Object child) {
	if ((parent == null) || (child == null))
	    return -1;

	Node node = (Node) parent;

	if (child instanceof Attr) {
	    NamedNodeMap attrNodes = node.getAttributes();
	    for (int i = 0; i < attrNodes.getLength(); i++)
		if (attrNodes.item(i) == child)
		    return i;
	} else if (child instanceof Node) {
	    NodeList childNodes = node.getChildNodes();
	    for (int i = 0; i < childNodes.getLength(); i++)
		if (childNodes.item(i) == child)
		    return node
			.getAttributes()
			.getLength() 
			+ i;
	}

	return -1;
    }

    public Object getRoot() {
	return source.getDocument().getDocumentElement();
    }

    public void appendAttribute(TreePath parentPath, String qname) {
	Element parent = (Element) parentPath.getLastPathComponent();
	String prefix = qname
	    .substring(0, Math.max(0, qname.indexOf(':')));
	Attr child = source.getDocument()
	    .createAttributeNS(parent
			       .lookupNamespaceURI(prefix),
			       qname);
	
	child.setValue("");

	try {
	    parent.setAttributeNode(child);
	    fireTreeNodesInserted(parentPath.getPath(),
				  getIndexOfChild(parent, child));
	} catch (org.w3c.dom.DOMException ex) {
	    // TODO
	    System.err.println(ex);
	}
    }

    private String inPrefixedForm(Element prefixSolver,
				  String uri,
				  String localname) {
	StringBuffer result = new StringBuffer();
	String prefix = prefixSolver.lookupPrefix(uri);
	if (prefix != null) {
	    result.append(prefix);
	    result.append(":");
	}
	result.append(localname);
	return result.toString();
    }

    private void addRequiredAtts(Element child) {
	/* Add the required attributes together with the element */
	NameList atts = GuidedEditing.getRequiredAttributes(child);
	String label;
	for (int i = 0; i < atts.getLength(); i++) {
	    label = inPrefixedForm(child,
				   atts.getNamespaceURI(i),
				   atts.getName(i));
	    child.setAttributeNode(source.getDocument()
				   .createAttributeNS(atts.getNamespaceURI(i),
						      label));
	}
    }

    public void appendChild(TreePath parentPath, String qname) {
	Node parent = (Node) parentPath.getLastPathComponent();
	String prefix = qname
	    .substring(0, Math.max(0, qname.indexOf(':')));

	Element child = source.getDocument()
	    .createElementNS(parent.lookupNamespaceURI(prefix),
			     qname);
	
	addRequiredAtts(child);

	try {
	    parent.appendChild(child);
	    fireTreeNodesInserted(parentPath.getPath(),
				  getChildCount(parent) - 1);
	} catch (org.w3c.dom.DOMException ex) {
	    // TODO
	    System.err.println(ex);
	}
    }

    public void appendTextChild(TreePath parentPath) {
	Node parent = (Node) parentPath.getLastPathComponent();

	Node child = source.getDocument()
	    .createTextNode("");
	
	try {
	    parent.appendChild(child);
	    fireTreeNodesInserted(parentPath.getPath(),
				  getChildCount(parent) - 1);
	} catch (org.w3c.dom.DOMException ex) {
	    // TODO
	    System.err.println(ex);
	}
    }

    public void insertSiblingBefore(TreePath refNodePath, String qname) {
	Node refNode = (Node) refNodePath.getLastPathComponent();
	Node parent = refNode.getParentNode();
	String prefix = qname
	    .substring(0, Math.max(0, qname.indexOf(':')));

	Element newNode = source.getDocument()
	    .createElementNS(parent.lookupNamespaceURI(prefix),
			     qname);
	int index = getIndexOfChild(parent, refNode);

	addRequiredAtts(newNode);

	try {
	    parent.insertBefore(newNode, refNode);
	    fireTreeNodesInserted(refNodePath.getParentPath().getPath(), index);
	} catch (org.w3c.dom.DOMException ex) {
	    // TODO
	    System.err.println(ex);
	}
    }

    public void insertTextBefore(TreePath refNodePath) {
	Node refNode = (Node) refNodePath.getLastPathComponent();
	Node parent = refNode.getParentNode();

	Node newNode = source.getDocument()
	    .createTextNode("");
	int index = getIndexOfChild(parent, refNode);

	try {
	    parent.insertBefore(newNode, refNode);
	    fireTreeNodesInserted(refNodePath.getParentPath().getPath(), index);
	} catch (org.w3c.dom.DOMException ex) {
	    // TODO
	    System.err.println(ex);
	}
    }

    public void insertSiblingAfter(TreePath refNodePath, String qname) {
	Node nextSibling = ((Node) refNodePath.getLastPathComponent())
	    .getNextSibling();

	if (nextSibling == null)
	    appendChild(refNodePath.getParentPath(), qname);
	else
	    insertSiblingBefore(refNodePath
				.getParentPath()
				.pathByAddingChild(nextSibling),

				qname);
    }

    public void insertTextAfter(TreePath refNodePath) {
	Node nextSibling = ((Node) refNodePath.getLastPathComponent())
	    .getNextSibling();

	if (nextSibling == null)
	    appendTextChild(refNodePath.getParentPath());
	else
	    insertTextBefore(refNodePath
			     .getParentPath()
			     .pathByAddingChild(nextSibling));
    }

    public void removeSubTree(TreePath path) {
	Node toRemove = (Node) path.getLastPathComponent();
	TreePath parentPath = path
	    .getParentPath();

	if (parentPath != null) {
	    int index = getIndexOfChild(parentPath
					.getLastPathComponent(),
					toRemove);

	    try {
		if (toRemove instanceof Attr)
		    ((Attr) toRemove).getOwnerElement()
			.removeAttributeNode((Attr) toRemove);
		else
		    toRemove.getParentNode()
			.removeChild(toRemove);

		fireTreeNodesRemoved(path.getParentPath().getPath(), index);
	    } catch (org.w3c.dom.DOMException ex) {
		// TODO
		System.err.println(ex);
	    }
	}
    }

    public boolean isLeaf(Object node) {
	return (getChildCount(node) == 0);
    }

    public void	valueForPathChanged(TreePath path, Object newValue) {
    }

    public void	addTreeModelListener(TreeModelListener l) {
	listenerList.add(TreeModelListener.class, l);
    }
    
    public void	removeTreeModelListener(TreeModelListener l) {
	listenerList.add(TreeModelListener.class, l);
    }

    public void documentSourceChanged(DocumentSourceEvent e) {
	fireTreeStructureChanged();
    }

    public void	fireTreeNodesChanged() {
    }
    public void	fireTreeNodesInserted(Object [] parent, int index) {
	int indexes [] = {index};

	TreeModelEvent event = new TreeModelEvent(this, parent, indexes, null);

	Object[] listeners = listenerList.getListenerList();

	for (int i = listeners.length - 2; i >= 0; i -= 2) 
	    if (listeners[i] == TreeModelListener.class)
		((TreeModelListener) listeners[i + 1]).treeNodesInserted(event);
    }
    public void	fireTreeNodesRemoved(Object [] parent, int index) {
	int indexes [] = {index};

	TreeModelEvent event = new TreeModelEvent(this, parent, indexes, null);

	Object[] listeners = listenerList.getListenerList();

	for (int i = listeners.length - 2; i >= 0; i -= 2) 
	    if (listeners[i] == TreeModelListener.class)
		((TreeModelListener) listeners[i + 1]).treeNodesRemoved(event);
    }
    public void	fireTreeStructureChanged() {
	TreeModelEvent event = 
	    new TreeModelEvent(this, new TreePath(getRoot()));

	Object[] listeners = listenerList.getListenerList();

	for (int i = listeners.length - 2; i >= 0; i -= 2) 
	    if (listeners[i] == TreeModelListener.class)
		((TreeModelListener) listeners[i + 1])
		    .treeStructureChanged(event);
    }
}
 
