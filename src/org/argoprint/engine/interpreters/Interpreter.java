package org.argoprint.engine.interpreters;

import org.w3c.dom.Node;
import org.argoprint.engine.*;
import org.argoprint.uml_interface.UMLInterface;

/**
 * Superclass for the interpreters.
 */
abstract public class Interpreter{
    static private String _TagName;
    private Interpreter _nextHandler;
    protected Interpreter _firstHandler;
    // TODO this should be changed when the interface is defined later
	// processTag uses calls to Interface
    private UMLInterface _umlInterface;
    
    public Interpreter() {
    	_nextHandler = null;
    }
    
    /**
	 * @param tagName The name of the tag that this Interpreter can process.
	 * @param prefix The namespace prefix of the tag that this Interpreter can process.
	 * @param umlInterface The interface that this Interpreter should make calls to.
	 * @param main The Main object. 
	 */
	public Interpreter(String tagName, UMLInterface umlInterface) {
    	_TagName = tagName;
    	_umlInterface = umlInterface;
    }
    
    /**
     * Specifies the next Interpreter in the chain of responsibility.
     * 
	 * @param nextHandler The next Interpreter.
	 */
	public void setNextHandler(Interpreter nextHandler) {
    	_nextHandler = nextHandler;
    }
	
	/**
		 * Specifies the first Interpreter in the chain of responsibility.
		 * 
		 * @param firstHandler The first Interpreter.
		 */
		public void setFirstHandler(Interpreter firstHandler) {
			_firstHandler = firstHandler;
		}
    
    /**
     * Processes the Node or sends it to the next Interpreter.
     *  
	 * @param tagNode The Node to handle.
	 * @param env The environment in which the Node is to be processed.
	 */
	public void handleTag(Node tagNode, Environment env)
	throws Exception {
		if (canHandle(tagNode))
			processTag(tagNode, env);
		else
			if (_nextHandler == null)
				throw new Exception("The last Interpreter in the chain could not handle the Node.");
			else
				_nextHandler.handleTag(tagNode, env);	
    }

    protected abstract void processTag(Node tagNode, Environment env) throws Exception;

    /**
     * Checks if this Interpreter can handle this Node.
     * 
	 * @param tagNode
	 * @return
	 */
	protected boolean canHandle(Node tagNode){
    	if (tagNode.getNodeType() == Node.ELEMENT_NODE && tagNode.getLocalName() == _TagName && tagNode.getPrefix() == "argoprint")
			return true;
		else
			return false;
    } 
}
