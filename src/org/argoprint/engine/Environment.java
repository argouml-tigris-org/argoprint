package org.argoprint.engine;

import java.util.*;

/**
 * The environment or scope for argoprint. Used for storing iterators.
 */
public class Environment{
    Environment instance;

    private Hashtable iteratorTable;

    public Environment(){
        instance = this;
	iteratorTable = new Hashtable();
    }

    public boolean existsIterator(String name){
	return iteratorTable.containsKey(name);
    }
    
    public boolean addIterator(String name, Iterator iterator){
	if(iteratorTable.containsKey(name)){
	    //Key collision occured. Perhaps solve this by calling rehash() ?
	    //and calling addIterator recursively, Or perhaps totally unneccesary
	    //since add and get solves this automagically
	    return false;
	}
	
	try{
	    iteratorTable.put(name, iterator);
	}
	catch(Exception e){
	    //this means name or iterator is null
	    return false;
	}
	
	return true;
    }

    public boolean removeIterator(String name){	
	try{
	    Iterator iter = (Iterator)iteratorTable.remove(name);
	    if(iter == null) { return false; }
	} 
	catch(Exception e){
	}
	return true;
    }

    public Iterator getIterator(String name){	
	try{
	    return (Iterator)iteratorTable.get(name);
	}
	catch(Exception e){
	    return null;
	}
    }

    
    public Environment instance(){
	return instance;
    }
    
}
