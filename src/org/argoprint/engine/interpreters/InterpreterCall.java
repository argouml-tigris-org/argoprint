package org.argoprint.engine.interpreters;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class InterpreterCall extends Interpreter {

	public InterpreterCall(ArgoPrintDataSource dataSource) {
		super("call", dataSource);
	}

	/**
	 * Processes the call tag.
	 * 
	 * @param tagNode
	 * @param env
	 */
	protected void processTag(Node tagNode, Environment env) throws Exception {
		NamedNodeMap attributes = tagNode.getAttributes();
		Node whatNode = attributes.getNamedItem("what");
		if (whatNode == null)
			throw new BadTemplateException("Call tag contains no what attribute.");
		Node iteratorNode = attributes.getNamedItem("iterator");
		Object callReturnValue;
		//if (iteratorNode == null)
			callReturnValue = _dataSource.caller(whatNode.getNodeValue());
		//else 
			// Call other caller
		
		// TODO: fix this so it detects if it is a String
		/*
		if (!((callReturnValue.getClass()).equals(java.lang.Class.forName("java.lang.String"))))
			throw new Exception("Expected a String but got something else. The call was: " + whatNode.getNodeValue());
		*/
		
		Document document = tagNode.getOwnerDocument();
		Node parentNode = tagNode.getParentNode();
		Node fetchedData = document.createTextNode((String)callReturnValue);
		parentNode.replaceChild(fetchedData, tagNode);
	}	
}	
	