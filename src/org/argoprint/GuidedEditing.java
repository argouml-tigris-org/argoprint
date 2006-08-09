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

import java.net.JarURLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.namespace.QName;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.*;

public class GuidedEditing {

    // TODO: should be moved out
    public static String N_URI_XSLT = "http://www.w3.org/1999/XSL/Transform";

    private static HashMap<String, Document> schemas = new HashMap<String, Document>();
    private static XPathVariableResolverImpl varRes = new XPathVariableResolverImpl();


    // TODO: should be moved out
    static {
	// GuidedEditing.add(APToolkit.URI_XSLT, APToolkit.XS_DOM_XSLT);
	GuidedEditing
	    .add(N_URI_XSLT,
		 APResources.getDOMFromJAR(APResources.SCHEMA_XSLT));
    }

    private static class XPathVariableResolverImpl
	implements XPathVariableResolver {

	private HashMap<QName, Object> varList;

	public XPathVariableResolverImpl() {
	    varList = new HashMap<QName, Object>();
	}

	public Object resolveVariable(QName variable) {
	    return varList.get(variable);
	}

	public void clear() {
	    varList.clear();
	}

	public void put(QName variable, Object value) {
	    varList.put(variable, value);
	}

    }

    public static void add(String namespaceURI, Document schema) {
	schemas.put(namespaceURI, schema);
    }

    public static void remove(String namespaceURI) {
	schemas.remove(namespaceURI);
    }

    public static Set<String> getAvailableURIs() {
	return schemas.keySet();
    }

    public static boolean knows(Node elem) {
	return schemas
	    .containsKey(elem.getNamespaceURI());
    }

    /* Utility functions */

    private static Object evalXPathOn(String xpath, String namespaceURI) {
	Object result = null;
	Document schema = schemas.get(namespaceURI);

	try {
	    result = XPathFactory
		.newInstance()
		.newXPath()
		.evaluate(xpath, schema, XPathConstants.NODESET);
	} catch (javax.xml.xpath.XPathExpressionException ex) {
	    ex.printStackTrace();
	}

	return result;
    }

    private static NameList toNameListCData(NodeList nodes) {
	NameListImpl result = new NameListImpl();

	System.err.println(nodes.getLength());

	for (int i = 0; i < nodes.getLength(); i++)
	    result.add(((CharacterData)nodes.item(i)).getData());

	return result;
    }

    private static NameList toNameListAttr(NodeList nodes) {
	NameListImpl result = new NameListImpl();

	System.err.println(nodes.getLength());

	for (int i = 0; i < nodes.getLength(); i++)
	    result.add(((Attr)nodes.item(i)).getValue());

	return result;
    }


    /* DocumentEditVAL */

    public static NameList getDefinedElements(Document doc, String namespaceURI) {
	NodeList nodes = (NodeList)evalXPathOn("/grammar/define/element/name/text()"
					       ,namespaceURI);
	
	return toNameListCData(nodes);
    }

    /* NodeEditVAL */

    // validationType
    public static final short VAL_WF                    = 1;
    public static final short VAL_NS_WF                 = 2;
    public static final short VAL_INCOMPLETE            = 3;
    public static final short VAL_SCHEMA                = 4;

    // validationState

    public static final short VAL_TRUE                  = 5;
    public static final short VAL_FALSE                 = 6;
    public static final short VAL_UNKNOWN               = 7;

    public static String getDefaultValue(Node node) {
	return "DefaultValue";
    }

    public static DOMStringList getEnumeratedValues(Node node) {
	return null;
    }
    
    public static short canInsertBefore(Node node,
					Node newChild, 
					Node refChild) {
	return VAL_FALSE;
    }

    public static short canRemoveChild(Node node,
				       Node oldChild) {
	return VAL_FALSE;
    }

    public static short canReplaceChild(Node node,
					Node newChild, 
					Node oldChild) {
	return VAL_FALSE;
    }

    public static short canAppendChild(Node node,
				       Node newChild) {
	return VAL_FALSE;
    }

    /* ElementEditVAL */

    public static final short VAL_EMPTY_CONTENTTYPE     = 1; 
    public static final short VAL_ANY_CONTENTTYPE       = 2;
    public static final short VAL_MIXED_CONTENTTYPE     = 3;
    public static final short VAL_ELEMENTS_CONTENTTYPE  = 4;
    public static final short VAL_SIMPLE_CONTENTTYPE    = 5;

    public static NameList getAllowedChildren(Element element) {
	NameListImpl names = new NameListImpl();
	NodeList refnames = (NodeList) evalXPathOn(String.format("/grammar/define[element/name/text()='%s']/descendant::ref/@name",
								 element.getLocalName()),
						   element.getNamespaceURI());

	for (int i = 0; i < refnames.getLength(); i++) {
	    StringBuffer result = new StringBuffer();

	    CharacterData text = (CharacterData)((NodeList)evalXPathOn(String.format("/grammar/define[@name='%s']/element/name/text()",
										     ((Attr)refnames.item(i)).getValue()),
								       element.getNamespaceURI())).item(0);

	    Attr ns = (Attr)((NodeList)evalXPathOn(String.format("/grammar/define[@name='%s']/element/name/@ns",
								 ((Attr)refnames.item(i)).getValue()),
						   element.getNamespaceURI())).item(0);
	    
	    if (ns != null) {
		String prefix = element.lookupPrefix(ns.getValue());
		if (prefix != null) {
		    result.append(prefix);
		    result.append(":");
		}
	    }

	    // TODO: anyName
	    if (text != null)
		result.append(text.getData());

	    names.add(result.toString());
	}
	return names;
    }

    // TODO
    public static NameList getAllowedFirstChildren(Element element) {
	return getAllowedChildren(element);
    }

    public static NameList getAllowedParents(Element element) {
	NodeList nodes = (NodeList) evalXPathOn(String.format("/grammar/define/element[descendant::ref[@name=/grammar/define[element/name/text()='%s']/@name]]/name/text()",
								 element.getLocalName()),
						   element.getNamespaceURI());

	if (nodes == null)
	    return new NameListImpl();
	else
	    return toNameListCData(nodes);
    }

    // TODO
    public static NameList getAllowedNextSiblings(Element element) {
	if (element.getOwnerDocument().getDocumentElement().isSameNode(element))
	    return new NameListImpl();
	else
	    return getAllowedChildren((Element)element.getParentNode());
    }

    // TODO
    public static NameList getAllowedPreviousSiblings(Element element) {
	if (element.getOwnerDocument().getDocumentElement().isSameNode(element))
	    return new NameListImpl();
	else
	    return getAllowedChildren((Element)element.getParentNode());
    }

    public static NameList getAllowedAttributes(Element element) {
	NameListImpl result = new NameListImpl();

	NodeList nodes = (NodeList) evalXPathOn(String.format("/grammar/define[element/name/text()='%s']/descendant::attribute/name",
							      element.getLocalName()),
						element.getNamespaceURI());

	for (int i = 0; i < nodes.getLength(); i++) {
	    Element node = (Element)nodes.item(i);
	    result.add(node.getAttribute("ns"), node.getTextContent());
	}

	return result;
    }

    public static NameList getRequiredAttributes(Element element) {
	NameListImpl result = new NameListImpl();

	NodeList nodes = (NodeList) evalXPathOn(String.format("/grammar/define[element/name/text()='%s']/descendant::attribute[not(ancestor::choice/empty)]/name",
							      element.getLocalName()),
						element.getNamespaceURI());

	for (int i = 0; i < nodes.getLength(); i++) {
	    Element node = (Element)nodes.item(i);
	    result.add(node.getAttribute("ns"), node.getTextContent());
	}

	return result;
    }

    public static short getContentType(Element element) {
	return VAL_EMPTY_CONTENTTYPE;
    }

    public static short canSetTextContent(Element element, String possibleTextContent) {
	return VAL_FALSE;
    }

    public static short canSetAttribute(Element element,
					String attrname, 
					String attrval) {
	return VAL_FALSE;
    }

    public static short canSetAttributeNode(Element element,
					    Attr attrNode) {
	return VAL_FALSE;
    }

    public static short canSetAttributeNS(Element element,
					  String namespaceURI, 
					  String qualifiedName, 
					  String value) {
	return VAL_FALSE;
    }

    public static short canRemoveAttribute(Element element,
					   String attrname) {
	return VAL_FALSE;
    }

    public static short canRemoveAttributeNS(Element element,
					     String namespaceURI, 
					     String localName) {
	return VAL_FALSE;
    }

    public static short canRemoveAttributeNode(Element element,
					       Node attrNode) {
	return VAL_FALSE;
    }

    public static short isElementDefined(Element element,
					 String name) {
	return VAL_FALSE;
    }

    public static short isElementDefinedNS(Element element,
					   String namespaceURI, 
					   String name) {
	return VAL_FALSE;
    }

    /* CharacterDataEditVAL */
    
    public static short isWhitespaceOnly(CharacterData chardata) {
	return VAL_FALSE;
    }

    public static short canSetData(CharacterData chardata,
				   String arg) {
	return VAL_FALSE;
    }

    public static short canAppendData(CharacterData chardata,
				      String arg) {
	return VAL_FALSE;
    }

    public static short canReplaceData(CharacterData chardata,
				       int offset, 
				       int count, 
				       String arg)
	throws DOMException {
	return VAL_FALSE;
    }

    public static short canInsertData(CharacterData chardata,
				      int offset, 
				      String arg)
	throws DOMException {
	return VAL_FALSE;
    }

    public static short canDeleteData(CharacterData chardata,
				      int offset, 
				      int count)
	throws DOMException {
	return VAL_FALSE;
    }


    // TODO: should be sorted
    private static class NameListImpl
	implements NameList {

	private ArrayList names, namespaces;

	public NameListImpl() {
	    names = new ArrayList();
	    namespaces = new ArrayList();
	}

	public void add(String str) {
	    if (!contains(str))
		names.add(str);
	}
 
	public void add(String namespaceURI, String name) {
	    if (!containsNS(namespaceURI, name)) {
		names.add(name);
		namespaces.add(names.size() - 1, namespaceURI);
	    }
	}
	
	public boolean contains(String str) {
	    return names.contains(str);
	}
	public boolean containsNS(String namespaceURI, String name) {
	    for (int i = 0; i < names.size(); i++)
		if (names.get(i) != null &&
		    names.get(i).equals(name) &&
		    namespaces.get(i) != null &&
		    namespaces.get(i).equals(namespaceURI))
		    return true;
	    return false;
	}
	public int getLength() {
	    return names.size();
	}
	public String getName(int index) {
	    return (String)names.get(index);
	}
	public String getNamespaceURI(int index) {
	    return (String)namespaces.get(index);
	}
    }

    /* Utility class implementations */


    // TODO: should be sorted
    private static class DOMStringListImpl
	implements DOMStringList {

	private ArrayList list;

	public DOMStringListImpl() {
	    list = new ArrayList();
	}

	public void add(String str) {
	    if (!contains(str))
		list.add(str);
	}

	public boolean contains(String str) {
	    return list.contains(str);
	}
	public int getLength() {
	    return list.size();
	}
	public String item(int index) {
	    return (String)list.get(index);
	}
    }
    
}
