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

import org.apache.xpath.NodeSet;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argoprint.engine.ArgoPrintIterator;
import org.argoprint.engine.Environment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Superclass for the interpreters.
 */
public abstract class Interpreter {
    /**
     * The prefix of all ArgoPrint xml tags.
     */
    protected static final String PREFIX = "ap";
    private String tagName;
    private Interpreter _nextHandler;
    
    /**
     * The handler that is to be called first.
     */
    protected Interpreter firstHandler;
    
    /**
     * The data source that we are working from.
     */
    protected ArgoPrintDataSource dataSource;
    
    /**
     * Constructs a new Interpreter.
     * 
     * @param name The name of the tag that this Interpreter can process.
     * @param source The ArgoPrintDataSource that this Interpreter
     * should fetch data from.
     */
    public Interpreter(String name, ArgoPrintDataSource source) {
        this(name, source, null);
    }

    /**
     * Constructs a new Interpreter.
     * 
     * @param name The name of the tag that this Interpreter can process.
     * @param source The ArgoPrintDataSource that this Interpreter
     * should fetch data from.
     * @param first The firstHandler to set up.
     */
    public Interpreter(String name, ArgoPrintDataSource source, Interpreter first) {
    	tagName = name;
    	dataSource = source;
    	firstHandler = first;
    	_nextHandler = null;
    }
    
    /**
     * Specifies the next Interpreter in the chain of responsibility.
     * 
     * @param nextHandler The next Interpreter.
     */
    public void setNextHandler(Interpreter nextHandler) {
    	_nextHandler = nextHandler;
    }
	
    /**
     * Specifies the first Interpreter in the chain of responsibility.
     * 
     * @param handler The first Interpreter.
     */
    public void setFirstHandler(Interpreter handler) {
	firstHandler = handler;
    }
    
    /**
     * Processes the Node or sends it to the next Interpreter.
     *  
     * @param tagNode The Node to handle.
     * @param env The environment in which the Node is to be processed.
     * @throws BadTemplateException when the input file is syntactically wrong.
     * @throws UnsupportedCallException when the input triggers invalid calls.
     */
    public void handleTag(Node tagNode, Environment env) 
    	throws BadTemplateException, UnsupportedCallException {
        
	if (canHandle(tagNode)) {
	    processTag(tagNode, env);
	} else {
	    if (_nextHandler == null) {
		throw new BadTemplateException("The last Interpreter "
		        		       + "in the chain could not "
		        		       + "handle the Node.");
	    } else {
		_nextHandler.handleTag(tagNode, env);
	    }
	}
    }


    /**
     * Processes the tag.
     *
     * @param tagNode is the node to process
     * @param env is the environment that the node has.
     * @throws BadTemplateException when the input file is syntactically wrong.
     * @throws UnsupportedCallException when the input triggers invalid calls.
     */
    protected abstract void processTag(Node tagNode, Environment env) 
    	throws BadTemplateException, UnsupportedCallException;

    /**
     * Checks if this Interpreter can handle this Node.
     * 
     * @param tagNode the node
     * @return true if this Interpreter can handle this node.
     */
    protected boolean canHandle(Node tagNode) {
	return (tagNode.getNodeType() == Node.ELEMENT_NODE
		&& tagNode.getLocalName().equals(tagName)
		&& tagNode.getPrefix().equals(PREFIX));
    }
    
    /**
     * Uses the attributes what and iterator to call the data source
     * and return the value.
     * 
     * @param callAttr The name of the attribute containing the call.
     * @param attributes A NamedNodeMap of map of attributes that must
     * contain at least the attribute what.
     * @param env The Environment in which to process the call.
     * @return The Object as returned from the data source. Depending on the 
     *         context it could be a Collection or object, a Boolean or 
     * 	       something that is converted to a String using 
     * 	       {@link Object#toString()}.
     * @throws BadTemplateException if the correct attributes are not found.
     * @throws BadTemplateException when the input file is syntactically wrong.
     * @throws UnsupportedCallException when the input triggers invalid calls.
     */
    protected Object callDataSource(String callAttr,
				    NamedNodeMap attributes, Environment env) 
    	throws BadTemplateException, UnsupportedCallException {
        
	Node callAttrNode = attributes.getNamedItem(callAttr);
	if (callAttrNode == null) {
	    throw new BadTemplateException(tagName + " tag contains no "
					   + callAttr + " attribute.");
	}
	Object returnValue;
	Node iteratorAttribute = attributes.getNamedItem("iterator");
	if (iteratorAttribute == null) {
	    returnValue = dataSource.caller(callAttrNode.getNodeValue());
	} else {
	    if (!env.existsIterator(iteratorAttribute.getNodeValue())) {
		throw new BadTemplateException(
	                "Value of iterator in " + tagName + 
			" tag does not correspond to a valid iterator. "
			+ iteratorAttribute.getNodeValue());
	    }
	    
	    ArgoPrintIterator iterator =
		env.getIterator(iteratorAttribute.getNodeValue());
	    returnValue = dataSource.caller(callAttrNode.getNodeValue(),
					     iterator.getCurrentObject());
	}
	return returnValue;
    }
		
    /**
     * Tests the node name.
     * 
     * @param node The node to test.
     * @param prefix The prefix to find.
     * @param localName The name.
     * @return <tt>true</tt> if the name matches.
     */
    protected boolean isNodeNamed(Node node, String prefix, String localName) {
	if ((node.getLocalName() == null) || node.getPrefix() == null) {
	    return false;
	} else {
	    return (node.getLocalName().equals(localName)
		    && node.getPrefix().equals(prefix));
	}
    }
	
    /**
     * @param node The node to test.
     * @param localName The name.
     * @see #isNodeNamed(Node, String, String)
     * @return <tt>true</tt> if the name matches.
     */
    protected boolean isNodeNamed(Node node, String localName) {
	return isNodeNamed(node, PREFIX, localName);
    }
	
    /**
     * Get a copy of the NodeList.
     * 
     * @param nodeList The NodeList to copy.
     * @return The newly created NodeList.
     */
    protected static NodeList getVector(NodeList nodeList) {
        NodeSet set = new NodeSet(nodeList);
        set.setShouldCacheNodes(true);
        return (NodeList) set;
    }
	
    /**
     * Continue the parsing for each of the specified nodes.
     * 
     * @param nodes The specified nodes.
     * @param env Current environment.
     * @throws BadTemplateException Thrown if the input is incorrect.
     * @throws UnsupportedCallException Thrown if there is some problem with
     *         calling the data source.
     */
    protected void recurse(NodeList nodes, Environment env)
    	throws BadTemplateException, UnsupportedCallException {
        
	for (int i = 0; i < nodes.getLength(); i++) {
	    firstHandler.handleTag(nodes.item(i), env);
	}
    }
}
