package org.argoprint.engine.interpreters;

//import org.apache.xerces.*;

import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.Node;

import org.argoprint.engine.*;


/**
 * Superclass for the interpreters.
 */
abstract class Interpreter{
    private String tagName;
    
    public Node handleTag(Node tagNode, Environment env){
	return null;
    }

    protected abstract Node processTag(Node tagNode, Environment env);

    private boolean canHandle(Node tagNode){
	return false;
    } 
}
