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

package org.argoprint.persistence.velocity;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.argoprint.persistence.TemplateEngine;
import org.argoprint.persistence.TemplateEngineException;
import org.argoprint.util.DiagramUtil;
import org.argoprint.util.UseCaseUtil;
import org.argouml.kernel.Project;

/**
 * This class is responsible for invoking the Velocity templating engine.
 * 
 * @author mfortner
 * 
 */
public class VelocityTemplateEngine implements TemplateEngine {

	/**
	 * {@inheritDoc}
	 */
	public void generate(Project project, String outputFile, String templateFile)
			throws IOException, TemplateEngineException {

		try {

			Velocity.init();

			VelocityContext context = new VelocityContext();

			context.put("project", project);
			context.put("DiagramUtil", new DiagramUtil());
			context.put("UseCaseUtil", new UseCaseUtil());
			
			
			Writer writer = new FileWriter(outputFile);
			
			//evaluate( Context context, Writer writer, String logTag, InputStream instream )
			Velocity.evaluate(context, writer, "VELOCITY", new FileReader(templateFile));

			writer.flush();
			writer.close();
			

		} catch (ResourceNotFoundException rnfe) {
			throw new TemplateEngineException(rnfe);
		} catch (ParseErrorException pee) {
			throw new TemplateEngineException(pee);
		}
		catch (MethodInvocationException mie) {
			throw new TemplateEngineException(mie);
		}
		catch (Exception e) {
		    // TODO: Why are we throwing an exception which is unrelated
		    // to the actual error? - tfm
		    throw new IOException(e.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getTemplateExtensions() {
		return new String[] { "vm" };
	}

}
