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
