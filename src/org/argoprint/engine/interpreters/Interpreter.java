package org.argoprint.engine.interpreters;

import org.w3c.dom.Node;
import org.argoprint.engine.*;
import org.argoprint.uml_interface.UMLInterface;

/**
 * Superclass for the interpreters.
 */
abstract class Interpreter{
    private String _tagName;
    private String _prefix;
    private Interpreter _nextHandler;
    // TODO this should be changed when the interface is defined later
	// processTag uses calls to Interface
    private UMLInterface _umlInterface;
    // Interpreter needs to use interpret() and getDocument() in the Main object
    private Main _main;
    
    /**
	 * @param tagName The name of the tag that this Interpreter can process.
	 * @param prefix The namespace prefix of the tag that this Interpreter can process.
	 * @param umlInterface The interface that this Interpreter should make calls to.
	 * @param main The Main object. 
	 */
	public Interpreter(String tagName, String prefix, UMLInterface umlInterface, Main main) {
    	_tagName = tagName;
    	_prefix = prefix;
    	_umlInterface = umlInterface;
    	_main = main;
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
     * Processes the Node or sends it to the next Interpreter.
     *  
	 * @param tagNode The Node to handle.
	 * @param env The environment in which the Node is to be processed.
	 * @return 
	 */
	public Node handleTag(Node tagNode, Environment env){
		if (this.canHandle(tagNode))
			return this.processTag(tagNode, env);
		else
			return _nextHandler.handleTag(tagNode, env);		
    }

    protected abstract Node processTag(Node tagNode, Environment env);

    /**
     * Checks if this Interpreter can handle this Node.
     * 
	 * @param tagNode
	 * @return
	 */
	private boolean canHandle(Node tagNode){
    	if (tagNode.getNodeType() == Node.ELEMENT_NODE && tagNode.getLocalName() == _tagName && tagNode.getPrefix() == _prefix)
			return true;
		else
			return false;
    } 
}
