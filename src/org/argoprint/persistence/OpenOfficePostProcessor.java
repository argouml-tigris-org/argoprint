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
