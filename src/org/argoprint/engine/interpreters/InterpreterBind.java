// $Id$
// Copyright (c) 2003-2004, Mikael Albertsson, Mattias Danielsson, Per Engström,
// Fredrik Gröndahl, Martin Gyllensten, Anna Kent, Anders Olsson,
// Mattias Sidebäck.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright
//   notice, this list of conditions and the following disclaimer in the
//   documentation and/or other materials provided with the distribution.
//
// * Neither the name of the University of Linköping nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.

package org.argoprint.engine.interpreters;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This Interpreter handle the bind tag.<p>
 *
 * The bind tag looks like this:
 * <pre>
 * &lt;ap:bind name="NAME"&gt;
 *   &lt;ap:attr name="NAME"&gt;blabla&lt;/ap:attr&gt;
 *   ...
 * &lt;/ap:bind&gt;
 * </pre>
 *
 * TODO:
 * Suggested new version of this tag to allow:
 * <pre>
 * &lt;ap:bind name="NAME"&gt;
 *   &lt;ap:attr&gt;
 *     &lt;ap:name&gt;blabla&lt;/ap:name&gt;
 *     &lt;ap:value&gt;blabla&lt;/ap:value&gt;
 *   &lt;/ap:attr&gt;
 *   &lt;ap:attr&gt;
 *     &lt;ap:name&gt;blabla&lt;/ap:name&gt;
 *     &lt;ap:value&gt;blabla&lt;/ap:value&gt;
 *   &lt;/ap:attr&gt;
 *   ...
 *   &lt;ap:contents&gt;
 *     blabla
 *   &lt;/ap:contents&gt;
 * &lt;/ap:bind&gt;
 * </pre>
 */
public class InterpreterBind extends Interpreter {

    /**
     * Constructor for the interpreter.
     *
     * @param dataSource The data source to call.
     * @param first The interpreter where we restart the parsing.
     */
    public InterpreterBind(ArgoPrintDataSource dataSource, Interpreter first) {
	super("bind", dataSource, first);
    }

    /**
     * Constructor for the interpreter.
     *
     * @param dataSource The data source to call.
     */
    public InterpreterBind(ArgoPrintDataSource dataSource) {
	this(dataSource, null);
    }

    /**
     * Processes the bind tag.
     *
     * @see Interpreter#processTag(Node, Environment)
     */
    protected void processTag(Node tagNode, Environment env)
    	throws BadTemplateException, UnsupportedCallException {

	// Create the new element
	NamedNodeMap attributes = tagNode.getAttributes();
	Node attr = attributes.getNamedItem("name");
	if (attr == null) {
	    throw new BadTemplateException("Bind tag contains no "
					   + "name attribute.");
	}
	Document document = tagNode.getOwnerDocument();
	Element newElement = document.createElement(attr.getNodeValue());

        NodeList children;
	NodeList bindChildren = tagNode.getChildNodes();
	for (int i = 0; i < bindChildren.getLength();) {
            Node child = bindChildren.item(i);
	    if (isNodeNamed(child, "attr")) {
		attributes = child.getAttributes();
		attr = attributes.getNamedItem("name");
                if (attr == null) {
                    throw new BadTemplateException("attr tag contains no "
						   + "name attribute.");
                }
                children = child.getChildNodes();
                // Recurse on the contents of the attr tag.
                recurse(getVector(children), env);
                child.normalize();

		// Extract the text and put it in the attribute
                children = child.getChildNodes();
		if (children.getLength() == 0) {
		    newElement.setAttribute(attr.getNodeValue(), "");
		} else if ((children.item(0).getNodeType() == Node.TEXT_NODE)
			   && (children.getLength() == 1)) {
		    newElement.setAttribute(attr.getNodeValue(),
					    children.item(0).getNodeValue());
		} else {
                    throw new BadTemplateException("Malformed attr tag.");
		}
                i++;
            } else {
                tagNode.removeChild(child);
                newElement.appendChild(child);
                firstHandler.handleTag(child, env);
                // Don't increment i since removeChild removed index i
            }
        }
        Node parentNode = tagNode.getParentNode();
        parentNode.replaceChild(newElement, tagNode);
    }
}
