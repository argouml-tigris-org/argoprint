package org.argoprint.engine;

import java.util.*;

/**
 * The environment or scope for argoprint. Used for storing iterators.
 */
class Environment{
    Environment instance;

    private HashTableet iteratorTable;

    public boolean iteratorExists(String name){
	//TODO: fix "naming" of a iterator

	Iterator iter = iteratorTable.iterator();
	while(iter.hasNext()){
	    //if(name.equals(((Iterator) (iter.next())).getName())){
		return true;
		//}
	} 
	return false;
    }
    public boolean addIterator(String name, Iterator iterator){
	return false;
    }

    public boolean removeIterator(String name){	
	return false;
    }

    public Iterator getIterator(String name){	
	return null;
    }

    
    public Environment instance(){
	return instance;
    }
    
}
