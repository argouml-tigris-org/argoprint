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

import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argoprint.engine.ArgoPrintIterator;
import org.argoprint.engine.Environment;
import org.w3c.dom.*;

public class InterpreterIterate extends Interpreter {

    public InterpreterIterate(ArgoPrintDataSource dataSource) {
	super("iterate", dataSource);
    }

    /**
     * Processes the iterate tag.
     *
     * @see Interpreter#processTag(Node, Environment)
     */
    protected void processTag(Node tagNode, Environment env) throws BadTemplateException, UnsupportedCallException {
	NamedNodeMap attributes = tagNode.getAttributes();
		
	// Get the collection
	Object callReturnValue = callDataSource("what", attributes, env);
	if (!(callReturnValue instanceof Collection)
	    && !(callReturnValue instanceof Object)) {
	    throw new UnsupportedCallException("The object returned from the call "
					       + "to the data source is not a collection.");
	} else if (!(callReturnValue instanceof Collection)
		   && (callReturnValue instanceof Object)) {
	    ArrayList tmpCollection = new ArrayList();
	    tmpCollection.add(callReturnValue);
	    callReturnValue = tmpCollection;
	}
		
		
	Collection iterateCollection = (Collection) callReturnValue;

	Node sortvalueAttribute = attributes.getNamedItem("sortvalue");
	if (sortvalueAttribute != null) {
	    String sortvalue = sortvalueAttribute.getNodeValue();
	    iterateCollection = sortCollection(iterateCollection, sortvalue);
	}
			
	Node iteratornameAttribute = attributes.getNamedItem("iteratorname");
	if (iteratornameAttribute == null) 
	    throw new BadTemplateException("Iterate tag contains no "
					   + "iteratorname attribute.");
	String iteratorname = iteratornameAttribute.getNodeValue();
		
	ArgoPrintIterator iterator =
	    new ArgoPrintIterator(iterateCollection.iterator());
	env.addIterator(iteratorname, iterator);

	Document document = tagNode.getOwnerDocument();
	Node parentNode = tagNode.getParentNode();
	NodeList children = tagNode.getChildNodes();
	Node newNode;

	// For every object in the iterator, clone every child, attach
	// it to the parent and recurse
	while (iterator.hasNext()) {
	    iterator.next();
	    for (int i = 0; i < children.getLength(); i++) {
		newNode = children.item(i).cloneNode(true);
		parentNode.insertBefore(newNode, tagNode);
		firstHandler.handleTag(newNode, env);
	    }
	}
	env.removeIterator(iteratorname);
	parentNode.removeChild(tagNode);
    }
	
    /**
     * Sorts a Collection according to a String returned by a call to
     * the data source for each object in the collection.
     * 
     * @param collection The collection to sort.
     * @param sortvalue The call that will be applied to each object
     * providing a value to sort on.
     * @return A sorted Collection.
     * @throws UnsupportedCallException when the input triggers invalid calls.
     */
    private Collection sortCollection(Collection collection, String sortvalue) 
    	throws UnsupportedCallException {

	Iterator iterator = collection.iterator();
	TreeMap sortedMap = new TreeMap();
	Object object;
	Object returned;
	while (iterator.hasNext()) {
	    object = iterator.next();
	    returned = _dataSource.caller(sortvalue, object);
	    if (returned == null)
		returned = "null";
	    else if (!(returned instanceof String))
		throw new UnsupportedCallException("The sortvalue function "
		        			   + "did not return "
		        			   + "a String.");
	    sortedMap.put(returned, object);
	}
	return sortedMap.values();
    }
}
