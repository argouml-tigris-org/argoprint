/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006-2007 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.argouml.application.helpers.ApplicationVersion;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;

/**
 * Provides access to the non-public parts of the persistence system
 * without requiring modifications in the ArgoUML persistence API.
 */
public class ArgoPrintInsider {
    private static ArgoPrintInsider instance;

    private ArgoPrintInsider() {
    }

    public static ArgoPrintInsider getInstance() {
	if (instance == null)
	    instance = new ArgoPrintInsider();
	
	return instance;
    }
    
    /**
     * Returns an InputStream from which the XML representation of the
     * current project can be retrived.
     */
    public InputStream getProjectInputStream() {
	PipedInputStream result = null;

	final Project project
	    = ProjectManager.getManager().getCurrentProject();
	
	final PipedOutputStream out
	    = new PipedOutputStream();

	try {
	    result = new PipedInputStream(out);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	
	Thread producer = new Thread() {
		public void run() {
		    UmlFilePersister persister = (UmlFilePersister) PersistenceManager
			.getInstance().getPersisterFromFileName("foo.uml");
		    
		    project.setVersion(ApplicationVersion.getVersion());
		    project.setPersistenceVersion(UmlFilePersister.PERSISTENCE_VERSION);
		    
		    try {
			persister.writeProject(project, out, null);
		    } catch (Exception ex) {
			ex.printStackTrace();
		    }
		}
	    };
	producer.start();
	
	return result;
    }
}
