package org.argoprint.engine.interpreters;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.Environment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InterpreterDefault extends Interpreter {

	public InterpreterDefault(ArgoPrintDataSource dataSource) {
		super("", dataSource);
	}

	/**
	 * Only needs to recurse on the children.
	 * 
	 * @param tagNode
	 * @param env
	 */
	protected void processTag(Node tagNode, Environment env) throws Exception {
		NodeList children = tagNode.getChildNodes();
		if (children != null) {
			// recurse on all children except text nodes
			for (int i = 0; i < children.getLength(); i++){
				if (children.item(i).getNodeType() != Node.TEXT_NODE)
					if (_firstHandler == null)
						throw new Exception("First handler is not specified in this Interpreter.");
					else
						_firstHandler.handleTag(children.item(i), env);
			}
		}
	}

	protected boolean canHandle(Node tagNode){
		return true;
	}
}