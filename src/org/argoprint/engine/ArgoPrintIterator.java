package org.argoprint.engine;

import java.util.*;

/**
 * Class which extends iterator too better keep track of current object
 */
public class ArgoPrintIterator implements Iterator{
    private Iterator _iterator;

    private Object _currentObject;

    public ArgoPrintIterator(Iterator iter){
	_iterator = iter;
    }

    public boolean hasNext(){ return _iterator.hasNext(); }
    
    public Object next(){
	_currentObject = _iterator.next();
	return _currentObject;
    }

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

    
    public Object currentObject(){
	return _currentObject;
    }


}
