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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This Interpreter processes the iterate tag.<p>
 * 
 * The iterate tag looks like this:<ul>
 * <li>Simple syntax:<pre>
 *     &lt;ap:iterate 
 *           what="expression1" iteratorname="name" sortvalue="expression"&gt;
 *       Tags processed one time for each value of <tt>expression1</tt>
 *       while <tt>name</tt> is bound to them one at the time.
 *     &lt;/ap:iterate&gt;
 * </pre>
 * 
 * <li>Complex syntax:<pre>
 *       &lt;ap:iterate 
 *           what="expression1" iteratorname="name" sortvalue="expression"&gt;
 *       &lt;ap:any&gt;
 *         Tags processes first if <tt>expression1</tt> is not empty.
 *         &lt;ap:foreach&gt;
 *         Tags processed one time for each value of <tt>expression1</tt>
 *         while <tt>name</tt> is bound to them one at the time.
 *         &lt;/ap:foreach&gt;
 *         Tags processes last if <tt>expression1</tt> is not empty.
 *       &lt;ap:/any&gt;
 *       &lt;ap:else&gt;
 *         Tags processed if <tt>expression1</tt> is empty.
 *       &lt;/ap:else&gt;
 *     &lt;/ap:iterate&gt;
 * </pre>
 *     The ap:else tags is optional.
 * </ul>
 * 
 */
public class InterpreterIterate extends Interpreter {

    /**
     * Constructor for this Interpreter.
     * 
     * @param dataSource The data source to call.
     * @param handler The handler to call next.
     */
    public InterpreterIterate(ArgoPrintDataSource dataSource, Interpreter handler) {
	super("iterate", dataSource, handler);
    }

    /**
     * Processes the iterate tag.
     *
     * @see Interpreter#processTag(Node, Environment)
     */
    protected void processTag(Node tagNode, Environment env) 
    	throws BadTemplateException, UnsupportedCallException {

	NamedNodeMap attributes = tagNode.getAttributes();
		
	ArgoPrintIterator iterator = calculateIterator(env, attributes);

	Node iteratornameAttribute = attributes.getNamedItem("iteratorname");
	if (iteratornameAttribute == null) 
	    throw new BadTemplateException("Iterate tag contains no "
					   + "iteratorname attribute.");
	String iteratorname = iteratornameAttribute.getNodeValue();

	Node parentNode = tagNode.getParentNode();
	NodeList children = tagNode.getChildNodes();
	
	NodeList anyList = findList(children, "any");
	NodeList elseList = null;

	env.addIterator(iteratorname, iterator);    
	
	if (anyList != null) {
	    // Complex syntax.
	    elseList = findList(children, "else");
	    
	    if (iterator.hasNext()) {
	        env.setCurrentIterator(iterator);
	        for (int i = 0; i < anyList.getLength(); i++) {
	            Node newNode = anyList.item(i).cloneNode(true);
	            parentNode.insertBefore(newNode, tagNode);
	            firstHandler.handleTag(newNode, env);
	        }
	        env.setCurrentIterator(null);
	    } else if (elseList != null) {
	        for (int i = 0; i < elseList.getLength(); i++) {
	            Node newNode = elseList.item(i).cloneNode(true);
	            parentNode.insertBefore(newNode, tagNode);
	            firstHandler.handleTag(newNode, env);
	        }	    
	    }
	} else {
	    // Simple syntax.
	    // For every object in the iterator, clone every child, attach
	    // it to the parent and recurse
	    while (iterator.hasNext()) {
	        iterator.next();
	        for (int i = 0; i < children.getLength(); i++) {
	            Node newNode = children.item(i).cloneNode(true);
	            parentNode.insertBefore(newNode, tagNode);
	            firstHandler.handleTag(newNode, env);
	        }
	    }
	}

	env.removeIterator(iteratorname);
	parentNode.removeChild(tagNode);
    }
	
    /**
     * Get the collection.
     * 
     * @param attributes Where we search for attributes.
     * @return A newly created iterator.
     * @param env is the environment that the node has.
     * @throws BadTemplateException when the input file is syntactically wrong.
     * @throws UnsupportedCallException when the input triggers invalid calls.
     */
    private ArgoPrintIterator calculateIterator(Environment env, NamedNodeMap attributes) throws BadTemplateException, UnsupportedCallException {
        Object callReturnValue = callDataSource("what", attributes, env);
        if (callReturnValue == null) {
            throw new UnsupportedCallException("null returned from the call");
        } else if (!(callReturnValue instanceof Collection)) {
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
        		
        return new ArgoPrintIterator(iterateCollection.iterator());
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
	    returned = dataSource.caller(sortvalue, object);
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
