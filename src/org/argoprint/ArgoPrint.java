/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    phidias
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006 The Regents of the University of California. All
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

package org.argoprint;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.argouml.persistence.ArgoPrintInsider;

/**
 * Provides functionality to the UI.
 */
public class ArgoPrint {

    private static final String PROP_TRANSFORMER
	= "javax.xml.transform.TransformerFactory";

    private static final String PROP_TRANSFORMER_XALAN
	= "org.apache.xalan.processor.TransformerFactoryImpl";

    /*
     * Make sure that Xalan is uses behind JAXP Transform
     * in order to allow XSLT extensions.
     */
    static {
	System.setProperty(PROP_TRANSFORMER,
			   PROP_TRANSFORMER_XALAN);
    }

    /**
     * Generate output by processing the current project using
     * template.
     *
     * @param template stream containing an XSLT template
     * @param output stream to which the result is written
     * @throws TransformerException If the document generation fails. 
     */
    public static void generate(InputStream template,
				OutputStream output)
	throws TransformerException {

	generate(ArgoPrintInsider
		 .getInstance()
		 .getProjectInputStream(),
		 
		 template,
		 output);
    }

    /**
     * Generate output by processing input using template.
     *
     * @param input stream containing an XML document
     * @param template stream containing an XSLT template
     * @param output stream to which the result is written
     * @throws TransformerException If the document generation fails.
     */
    public static void generate(InputStream input,
            InputStream template,
            OutputStream output) throws TransformerException {

        try {
            Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(template));

            transformer.transform(new StreamSource(input),
                    new StreamResult(output));
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        }
    }
}
