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

package org.argoprint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

public class DataSourceStub implements ArgoPrintDataSource {
    
    /**
     * Applies the method with name call with no parameters to the iteratorObject.

     * @see ArgoPrintDataSource#caller(String, Object)
     */ 
    public Object caller(String call, Object iteratorObject) 
    	throws UnsupportedCallException {
        
        Object returnValue = null;
        Method callMethod;
        try {
            callMethod = iteratorObject.getClass().getMethod(call, new Class[0]);
        } catch (SecurityException e) {
            throw new UnsupportedCallException(e);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedCallException(e);
        }
        try {
            returnValue = callMethod.invoke(iteratorObject, new Object[0]);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedCallException(e);
        } catch (IllegalAccessException e) {
            throw new UnsupportedCallException(e);
        } catch (InvocationTargetException e) {
            throw new UnsupportedCallException(e);
        }
        return returnValue;
    }
    
    public Object caller(String call) throws UnsupportedCallException {
        Vector v = new Vector(5);
        if (call.equals("getString()"))
            return new String("Return value of getString() in DataSourceStub.");
        else if (call.equals("getCollection()")) {
            v.add(0, "object1");
            v.add(1, "object2");
            v.add(2, "object3");
            v.add(3, "object4");
            v.add(4, "object5");
            return v;
        }
        else if (call.equals("getTrueBoolean()"))
            return new Boolean(true);
        else if (call.equals("getFalseBoolean()"))
            return new Boolean(false);
        else
            throw new UnsupportedCallException();	
    }
}
