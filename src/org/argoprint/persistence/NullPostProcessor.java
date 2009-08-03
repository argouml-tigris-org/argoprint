package org.argoprint.persistence;

import java.io.IOException;

/**
 * This class is used to insure that no post processing is performed 
 * on selected file types.
 * 
 * @author mfortner
 *
 */
public class NullPostProcessor implements PostProcessor {
	
	static final String[] exts = new String[]{"html","xhtml","htm"};

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
		// DO NOTHING.

	}

}
