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
 * Class which extends iterator to better keep track of current object.
 *
 * @author matda701, Mattias Danielsson
 */
public class ArgoPrintIterator implements Iterator{
    
    /**
     * The iterator
     */ 
    private Iterator iterator;

    /**
     * The current object in the iterator. null until next() has been run
     */
    private Object currentObject;

    /**
     * Constructor, takes the iterator to be wrapped into ArgoPrintIterator.
     * 
     * @param iter The iterator to wrap.
     */
    public ArgoPrintIterator(Iterator iter){
	iterator = iter;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() { 
        return iterator.hasNext(); 
    }
   
    /**
     * @see java.util.Iterator#next()
     * 
     * Also updates currentObject.
     */
    public Object next(){
	currentObject = iterator.next();
	return currentObject;
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() 
	throws UnsupportedOperationException,
	       IllegalStateException {
        iterator.remove();
    }

    /**
     * Returns the currentObject in the iterator. Null if {@link #next()} 
     * hasn't been invoked.
     * 
     * @return the current object.
     */
    public Object getCurrentObject(){
	return currentObject;
    }
}
