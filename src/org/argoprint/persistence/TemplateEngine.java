// $Id$
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

package org.argoprint.persistence;

import java.io.IOException;

import org.argouml.kernel.Project;

/**
 * This interface serves as an adapter for templating engines. Classes which
 * implement this interface are expected to make calls to 3rd party templating
 * engines. This provides a consistent programmatic interface for the use of
 * templating engines in ArgoPrint.
 * 
 * @author mfortner
 * 
 */
public interface TemplateEngine {

	/**
	 * This method gets the template file extensions supported by this template
	 * engine.
	 * 
	 * @return an array of supported template extensions (i.e. xslt, vm). The
	 *         extension should not include the period.
	 */
	public String[] getTemplateExtensions();

	/**
	 * This method takes the contents of a project and uses the templateFile to
	 * generate the content of the outputFile.
	 * 
	 * @param project
	 *            The currently selected project
	 * @param outputFile
	 *            The output file
	 * @param templateFile
	 *            The template file
	 * @throws IOException
	 *             if there is a problem generating the file.
	 */
	public void generate(Project project, String outputFile, String templateFile)
			throws IOException, TemplateEngineException;

}
