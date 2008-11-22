/**
 * 
 */
package org.argoprint.persistence;

/**
 * This exception is thrown whenever we're unable to find an appropriate TemplateEngine.
 * 
 * @author mfortner
 *
 */
public class TemplateEngineNotFoundException extends Exception {

	public TemplateEngineNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TemplateEngineNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TemplateEngineNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TemplateEngineNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
