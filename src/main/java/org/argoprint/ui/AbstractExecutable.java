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

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Default implementation of an Executable.s
 *
 * @author mfortner
 */
public abstract class AbstractExecutable implements Executable {
    
    private Throwable exception = null;
    private String name = null;

    public abstract void run();

    
    public Throwable getException() {
        return exception;
    }

    
    public boolean hasException() {
        return exception != null;
    }
    
    /**
     * Sets the exception thrown during execution.
     * @param ex  The new exception
     */
    public void setException(Throwable ex){
        this.exception = ex;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    
    public void showException(final Component parent, final String message){
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
            
           
        });
    }

}
