package org.argoprint.engine;

import java.util.*;

/**
 * The environment or scope for argoprint. Used for storing iterators.
 */
class Environment{
    Environment instance;

    private Set iteratorSet;

    public boolean exists(String name){
	//TODO: fix "naming" of a iterator

	Iterator iter = iteratorSet.iterator();
	while(iter.hasNext()){
	    //if(name.equals(((Iterator) (iter.next())).getName())){
		return true;
		//}
	} 
	return false;
    }
    public boolean add(Iterator iterator){
	return false;
    }

    public boolean remove(String name){	
	return false;
    }

    public Iterator getIterator(String name){	
	return null;
    }

    
    public Environment instance(){
	return instance;
    }
    
}
