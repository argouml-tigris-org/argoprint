package org.argoprint.engine;

import java.util.*;

/**
 * The environment or scope for argoprint. Used for storing iterators.
 * @author matda701, Mattias Danielsson
 */
public class Environment{

    /**
     * Reference to self
     */
    private Environment _instance;
    
    /**
     * Hashtable containing ArgoPrintIterator's hashed with
     * name spcifyed by the user/template.
     */
    private Hashtable _iteratorTable;

    /**
     * Constructor for Environment
     */ 
    public Environment(){
        _instance = this;
	_iteratorTable = new Hashtable();
    }

    /**
     * Checks if iterator name exists in the table
     */    
    public boolean existsIterator(String name){
	return _iteratorTable.containsKey(name);
    }
  
    
    /**
     * Adds an Iterator to the hashtable, hashed with name. Returns
     * true if succesful.
     */
    public boolean addIterator(String name, Iterator iterator){
	if(_iteratorTable.containsKey(name)){
	    //Key collision occured. Perhaps solve this by calling rehash() ?
	    //and calling addIterator recursively, Or perhaps totally 
	    //unneccesary since add and get solves this automagically
	    return false;
	}
	
	try{
	    _iteratorTable.put(name, iterator);
	}
	catch(Exception e){
	    //this means name or iterator is null
	    return false;
	}
	
	return true;
    }
    
    /**
     * Removes the Iterator name from the hashtable. Returns
     * true if succesful.
     */
    public boolean removeIterator(String name){	
	try{
	    Iterator iter = (Iterator)_iteratorTable.remove(name);
	    if(iter == null) { return false; }
	} 
	catch(Exception e){
	}
	return true;
    }

    /**
     * Gets the Iterator called name from the hashtable. Returns
     * the iterator if succesful, othervise null 
     */
    public ArgoPrintIterator getIterator(String name){	
	try{
	    return (ArgoPrintIterator)_iteratorTable.get(name);
	}
	catch(Exception e){
	    return null;
	}
    }

    /**
     * Returns referenc to self
     */
    public Environment instance(){
	return _instance;
    }
}
