// $Id$
// Copyright (c) 2009 The Regents of the University of California. All
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
package org.argoprint.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class is responsible for packaging the temporary output file into a zip
 * file.
 * 
 * @author mfortner
 * 
 */
public class OpenOfficePostProcessor implements PostProcessor {

	private static final String[] exts = new String[] { "odt" };

	/**
	 * {@inheritDoc}
	 */
	public String[] getSupportedExtensions() {
		return exts;
	}

	/**
	 * {@inheritDoc}
	 */
	public void processFile(String file) throws IOException {
		File outputFile = new File(file);
		File parentdir = outputFile.getParentFile();

		String[] filenames = parentdir.list();

		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		// Create the ZIP file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				outputFile));

		// Compress the files
		for (int i = 0; i < filenames.length; i++) {
			FileInputStream in = new FileInputStream(filenames[i]);

			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(filenames[i]));

			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			// Complete the entry
			out.closeEntry();
			in.close();
		}

		// Complete the ZIP file
		out.close();

	}

}
