package org.argoprint.persistence;

import java.io.IOException;

/**
 * This interface is used to process an output file after the file has been generated.
 * 
 * @author mfortner
 *
 */
public interface PostProcessor {

	/**
	 * This method processes a file after the file has been generated.
	 * @param file  The file to be processed.
	 * @throws IOException
	 */
	public void processFile(String file) throws IOException;
	
	/**
	 * This method returns an array of file extensions supported by this processor.
	 * Note that the extensions must be unique. 
	 * @return  An array of supported extensions.
	 */
	public String[] getSupportedExtensions();
}
