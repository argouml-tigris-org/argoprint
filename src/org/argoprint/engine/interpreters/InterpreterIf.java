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

package org.argoprint.engine.interpreters;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argoprint.engine.Environment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This Interpreter processes the if tag.<p>
 *
 * The if tag looks like this:<ul>
 * <li>&lt;ap:if cond="VALUE"&gt;blabla&lt;/ap:if&gt;,
 * <li>&lt;ap:if cond="VALUE"&gt;
 *       &lt;ap:then&gt;blabla&lt;/ap:then&gt;
 *     &lt;/ap:if&gt;, or
 * <li>&lt;ap:if cond="VALUE"&gt;
 *       &lt;ap:then&gt;blabla&lt;/ap:then&gt;
 *       &lt;ap:else&gt;blabla&lt;/ap:else&gt;
 *     &lt;/ap:if&gt;
 * </ul>
 */
public class InterpreterIf extends Interpreter {

    /**
     * Constructor for this interpreter.
     * 
     * @param dataSource The data source to call.
     * @param first The interpreter to call when working recursively.
     */
    public InterpreterIf(ArgoPrintDataSource dataSource, Interpreter first) {
	super("if", dataSource, first);
    }

    /**
     * Processes the if tag.
     *
     * @see Interpreter#processTag(Node, Environment)
     */
    protected void processTag(Node tagNode, Environment env) 
    	throws BadTemplateException, UnsupportedCallException {
        
	// Evaluate condition
	NamedNodeMap attributes = tagNode.getAttributes();
	Object returnValue = callDataSource("cond", attributes, env);
	if (!(returnValue instanceof Boolean)) {
	    throw new BadTemplateException("The condition did not evaluate "
	                                   + "to a Boolean.");
	}
		
	// Decide what part of the sub tree (if any) shall be used
	NodeList resultChildren;
	if (((Boolean) returnValue).booleanValue()) {
	    Node thenNode = findNode(tagNode.getChildNodes(), "then");
	    if (thenNode == null)
		resultChildren = tagNode.getChildNodes();
	    else
		resultChildren = thenNode.getChildNodes();
	}
	else {
	    Node elseNode = findNode(tagNode.getChildNodes(), "else");
	    if (elseNode == null) {
		resultChildren = null;
	    } else {
		resultChildren = elseNode.getChildNodes();
	    }
	}
		
	attachResult(tagNode, env, resultChildren);
    }

    /**
     * Return the child node with a given name.<p>
     * 
     * This returns the first found node with the name.
     * 
     * @param children where to search for the node.
     * @param nodeName The name to search from.
     * @return The found node (or <tt>null</tt> if not found).
     */
    private Node findNode(NodeList children, String nodeName) {
        for (int i = 0; i < children.getLength(); i++) {
            if (isNodeNamed(children.item(i), nodeName)) {
        	return children.item(i);
            }
        }
        return null;
    }

    /**
     * Attach the result to parent and remove if tag
     * 
     * @param tagNode the node that we are removing.
     * @param env The contect we are in.
     * @param resultChildren The result of the if.
     * @throws BadTemplateException when the input file is syntactically wrong.
     * @throws UnsupportedCallException when the input triggers invalid calls.
     */
    private void attachResult(Node tagNode, Environment env, 
            		      NodeList resultChildren) 
    	throws BadTemplateException, UnsupportedCallException {
        
        Node ifParent = tagNode.getParentNode();
        if (!(resultChildren == null || resultChildren.getLength() == 0)) { 
            Node resultParent = resultChildren.item(0).getParentNode();
            NodeList resultVector = getVector(resultChildren);
            for (int i = 0; i < resultVector.getLength(); i++) {
        	Node child = resultVector.item(i);
        	resultParent.removeChild(child);
        	ifParent.insertBefore(child, tagNode);
            }
            recurse(resultVector, env);
        }
        ifParent.removeChild(tagNode);
    }
}
