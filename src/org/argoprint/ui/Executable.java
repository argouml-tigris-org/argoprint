/* $Id$
 *******************************************************************************
 * Copyright (c) 2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mfortner
 *******************************************************************************
 */

package org.argoprint.ui;

/**
 * This class provides a means for threads to throw an exception which can later
 * be caught.  It is intended for use with ThreadPools other other constructs 
 * where one submits an executable and later wants to know which threads failed and why.
 *
 * @author mfortner
 */
public interface Executable extends Runnable {
    
    /**
     * This method gets the exception that was thrown by the Executable.
     * @return
     */
    public Throwable getException();
    
    /**
     * Sets the exception thrown by the executable.
     * @param ex  The new exception.
     */
    public void setException(Throwable ex);
    
    /**
     * Indicates that the thread threw an exception while running.
     * @return true if an exception has been thrown and the exception variable is set.
     */
    public boolean hasException();
    
    /**
     * Gets the name of the executable
     * @return the name of the executable
     */
    public String getName();
    
    /**
     * Sets the name of the executable
     * @param name the name of the executable
     */
    public void setName(String name);

}
