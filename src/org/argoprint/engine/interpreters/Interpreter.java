package org.argoprint.engine.interpreters;

import org.w3c.dom.Node;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.*;

/**
 * Superclass for the interpreters.
 */
abstract public class Interpreter{
    private String _tagName;
    private Interpreter _nextHandler;
    protected Interpreter _firstHandler;
    protected ArgoPrintDataSource _dataSource;
    
    public Interpreter() {
    	}
    
    /**
	 * @param tagName The name of the tag that this Interpreter can process.
	 * @param dataSource The ArgoPrintDataSource that this Interpreter should fetch data from.
	 */
	public Interpreter(String tagName, ArgoPrintDataSource dataSource) {
    	_tagName = tagName;
    	_dataSource = dataSource;
    	_nextHandler = null;
    	_firstHandler = null;
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
	    	if (tagNode.getNodeType() == Node.ELEMENT_NODE && tagNode.getLocalName().equals(_tagName) && tagNode.getPrefix().equals("argoprint")) {
    		return true;
		}
		else {
			return false;
		}
    } 
}
