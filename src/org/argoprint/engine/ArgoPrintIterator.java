package org.argoprint.engine;

import java.util.*;

/**
 * Class which extends iterator too better keep track of current object
 *
 * @author matda701, Mattias Danielsson
 */
public class ArgoPrintIterator implements Iterator{
    
    /**
     * The iterator
     */ 
    private Iterator _iterator;

    /**
     * The current object in the iterator. null until next() has been run
     */
    private Object _currentObject;

    /**
     * Constructor, takes the iterator to be wrapped into ArgoPrintIterator
     */
    public ArgoPrintIterator(Iterator iter){
	_iterator = iter;
    }

    /**
     * Checks if the iterator has more objects
     */
    public boolean hasNext(){ return _iterator.hasNext(); }
   
    /**
     * Returns the next object in the iterator. Also updates _currentObject
     */ 
    public Object next(){
	_currentObject = _iterator.next();
	return _currentObject;
    }

    /**
     * Removes last object returned by next(). Se Iterator for more info
     */
    public void remove() 
	throws UnsupportedOperationException,
	       IllegalStateException {
	try{
	    _iterator.remove();
	} catch(UnsupportedOperationException e){
	    throw e;
	}
	catch(IllegalStateException e){
	    throw e;
	}
    }

    /**
     * Returns the currentObject in the iterator. Null if next() hasnt been
     * invoked.
     */
    public Object currentObject(){
	return _currentObject;
    }
}
