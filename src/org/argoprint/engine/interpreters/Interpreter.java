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


package org.argoprint.engine.interpreters;

import java.util.Vector;
import org.w3c.dom.*;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.*;

/**
 * Superclass for the interpreters.
 */
abstract public class Interpreter{
    protected static final String PREFIX = "ap";
    private String _tagName;
    private Interpreter _nextHandler;
    protected Interpreter _firstHandler;
    protected ArgoPrintDataSource _dataSource;
    
    public Interpreter() {
    }
    
    /**
     * @param tagName The name of the tag that this Interpreter can process.
     * @param dataSource The ArgoPrintDataSource that this Interpreter should fetch data from.
     */
    public Interpreter(String tagName, ArgoPrintDataSource dataSource) {
    	_tagName = tagName;
    	_dataSource = dataSource;
    	_nextHandler = null;
    	_firstHandler = null;
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
     * @param firstHandler The first Interpreter.
     */
    public void setFirstHandler(Interpreter firstHandler) {
	_firstHandler = firstHandler;
    }
    
    /**
     * Processes the Node or sends it to the next Interpreter.
     *  
     * @param tagNode The Node to handle.
     * @param env The environment in which the Node is to be processed.
     */
    public void handleTag(Node tagNode, Environment env)
	throws Exception {
	if (canHandle(tagNode))
	    processTag(tagNode, env);
	else
	    if (_nextHandler == null)
		throw new Exception("The last Interpreter in the chain could not handle the Node.");
	    else
		_nextHandler.handleTag(tagNode, env);	
    }

    protected abstract void processTag(Node tagNode, Environment env) throws Exception;

    /**
     * Checks if this Interpreter can handle this Node.
     * 
     * @param tagNode
     * @return
     */
    protected boolean canHandle(Node tagNode){
	if (tagNode.getNodeType() == Node.ELEMENT_NODE && tagNode.getLocalName().equals(_tagName) && tagNode.getPrefix().equals(PREFIX)) {
	    return true;
	}
	else {
	    return false;
	}
    }
    
    /**
     * Uses the attributes what and iterator to call the data source and return the value.
     * 
     * @param callAttr The name of the attribute containing the call.
     * @param attributes A NamedNodeMap of map of attributes that must contain at least the attribute what.
     * @param env The Environment in which to process the call.
     * @return The Object as returned from the data source.
     * @throws Exception
     */
    protected Object callDataSource(String callAttr, NamedNodeMap attributes, Environment env) 
	throws Exception {
	Node callAttrNode = attributes.getNamedItem(callAttr);
	if (callAttrNode == null)
	    throw new BadTemplateException(_tagName + " tag contains no " + callAttr +" attribute.");
	Object returnValue;
	Node iteratorAttribute = attributes.getNamedItem("iterator");
	if (iteratorAttribute == null)
	    returnValue = _dataSource.caller(callAttrNode.getNodeValue());
	else {
	    if (!env.existsIterator(iteratorAttribute.getNodeValue()))
		throw new BadTemplateException("Value of iterator in " + _tagName + 
					       " tag does not correspond to a valid iterator. "
					       + iteratorAttribute.getNodeValue());
	    
	    ArgoPrintIterator iterator = env.getIterator(iteratorAttribute.getNodeValue());
	    returnValue = _dataSource.caller(callAttrNode.getNodeValue(), iterator.currentObject());
	}
	return returnValue;
    }
		
    protected boolean isNodeNamed(Node node, String prefix, String localName) {
	if ((node.getLocalName() == null) || node.getPrefix() == null)
	    return false;
	else
	    return (node.getLocalName().equals(localName) && node.getPrefix().equals(prefix));
    }
	
    protected Vector getVector(NodeList nodeList) { 
	int numNodes = nodeList.getLength();
	Vector vector = new Vector(numNodes);
	for (int i = 0; i < numNodes; i++){
	    vector.add(i, nodeList.item(i));
	}
	return vector;
    }
	
    protected void recurse(Vector nodes, Environment env) throws Exception {
	for (int i = 0; i < nodes.size(); i++) {
	    _firstHandler.handleTag((Node)nodes.get(i), env);
	}
    }
}
