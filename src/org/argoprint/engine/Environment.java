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

package org.argoprint.engine;

import java.util.*;

/**
 * The environment or scope for argoprint. Used for storing iterators.
 * @author matda701, Mattias Danielsson
 */
public class Environment{    
    /**
     * Hashtable containing ArgoPrintIterator's hashed with
     * name specified by the user/template.
     * 
     * String => ArgoPrintIterator
     */
    private Hashtable _iteratorTable;

    /**
     * Constructor for Environment
     */ 
    public Environment(){
        _iteratorTable = new Hashtable();
    }

    /**
     * Checks if iterator name exists in the table.
     * 
     * @param name The iterator to search for.
     * @return <tt>true</tt> if found.
     */    
    public boolean existsIterator(String name){
	return _iteratorTable.containsKey(name);
    }
  
    
    /**
     * Adds an Iterator to the hashtable, hashed with name. Returns
     * true if succesful.
     * 
     * @param name The name to save the iterator under.
     * @param iterator The iterator to save.
     * @return true if all goes well.
     */
    public boolean addIterator(String name, Iterator iterator) {
        if (name == null) {
            return false;
        }
        if (iterator == null) {
            return false;
        }
        
	if(_iteratorTable.containsKey(name)){
	    //Key collision occured. Perhaps solve this by calling rehash() ?
	    //and calling addIterator recursively, Or perhaps totally 
	    //unneccesary since add and get solves this automagically
	    return false;
	}
	
	_iteratorTable.put(name, iterator);	
	return true;
    }
    
    /**
     * Removes the iterator name from the hashtable.
     * 
     * @param name The name of the iterator to remove.
     * @return <tt>true</tt> if there actually was an iterator with that name.
     */
    public boolean removeIterator(String name) {
        Iterator iter = (Iterator) _iteratorTable.remove(name);
	if (iter == null) {
	    return false;
	}
	return true;
    }

    /**
     * Gets the Iterator called name from the hashtable. 
     * 
     * @param name The name of the iterator to get.
     * @return The iterator if succesful, otherwise null.
     */
    public ArgoPrintIterator getIterator(String name) {
        if (name == null) {
            return null;
        }
        
        return (ArgoPrintIterator) _iteratorTable.get(name);
    }
}
