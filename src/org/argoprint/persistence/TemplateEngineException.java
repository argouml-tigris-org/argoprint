/**
 * 
 */
package org.argoprint.persistence;

/**
 * This class is used to identify exceptions caused by the template engine.
 * @author mfortner
 *
 */
public class TemplateEngineException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3565698436707778006L;

	
	/**
	 * Constructor
	 * @param msg	The exception message.
	 */
	public TemplateEngineException(String msg){
		super(msg);
	}


	/**
	 * Constructor
	 */
	public TemplateEngineException() {
		super();
		
	}


	/**
	 * Constructor
	 * @param message  The exception message.
	 * @param cause		The throwable exception to be wrapped.
	 */
	public TemplateEngineException(String message, Throwable cause) {
		super(message, cause);
		
	}



	/**
	 * Constructor
	 * @param cause  the throwable exception to be wrapped.
	 */
	public TemplateEngineException(Throwable cause) {
		super(cause);
		
	}
	
	

}
