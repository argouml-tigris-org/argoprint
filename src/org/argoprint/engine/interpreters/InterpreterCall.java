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
		Object returnValue = callDataSource(attributes, env);
        if (returnValue == null)
            returnValue = new String("null");
		Document document = tagNode.getOwnerDocument();
		Node parentNode = tagNode.getParentNode();
		Node fetchedData = document.createTextNode(returnValue.toString());
		parentNode.replaceChild(fetchedData, tagNode);
	}	
}	
	