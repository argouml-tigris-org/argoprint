// $Id:  $
// Copyright (c) 2008 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
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

package org.argoprint.persistence.xslt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.TransformerException;

import org.argoprint.ArgoPrint;
import org.argoprint.persistence.TemplateEngine;
import org.argoprint.persistence.TemplateEngineException;
import org.argouml.kernel.Project;

/**
 * This class is responsible for transforming the UML model into XML using a
 * stylesheet.
 * 
 * @author mfortner
 * 
 */
public class XSLTTemplateEngine implements TemplateEngine {

	/**
	 * {@inheritDoc}
	 */
	public void generate(Project project, String outputFile, String templateFile)
			throws IOException, TemplateEngineException {
		InputStream streamTemplate = new FileInputStream(templateFile);
		OutputStream streamOutput = new FileOutputStream(outputFile);

		InputStream paramTemplate = streamTemplate;
		OutputStream paramOutput = streamOutput;

		try {
			ArgoPrint.generate(paramTemplate, paramOutput);
		} catch (TransformerException e) {
			throw new IOException(e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getTemplateExtensions() {
		return new String[] { "xsl", "xslt" };
	}

}
