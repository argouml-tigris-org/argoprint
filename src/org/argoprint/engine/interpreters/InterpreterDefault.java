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
			for (int i = 0; i < children.getLength(); i++){
				_firstHandler.handleTag(children.item(i), env);
			}
		}
	}

	protected boolean canHandle(Node tagNode){
		return true;
	}
}