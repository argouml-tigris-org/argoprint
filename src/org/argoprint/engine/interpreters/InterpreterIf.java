package org.argoprint.engine.interpreters;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.Environment;
import org.w3c.dom.*;

public class InterpreterIf extends Interpreter {

	public InterpreterIf(ArgoPrintDataSource dataSource) {
		super("if", dataSource);
	}

	/**
	 * Processes the if tag.
	 * 
	 * @param tagNode
	 * @param env
	 */
	protected void processTag(Node tagNode, Environment env) throws Exception {
		// Evaluate condition
		NamedNodeMap attributes = tagNode.getAttributes();
		Object returnValue = callDataSource("cond", attributes, env);
		if (!(returnValue instanceof Boolean))
			throw new BadTemplateException("The condition did not evaluate to a Boolean.");
		Boolean cond = (Boolean)returnValue;
		
		NodeList ifChildren = tagNode.getChildNodes();
		
		// Find then and else nodes
		Node thenNode = null;
		Node elseNode = null;
		for (int i = 0; i < ifChildren.getLength(); i++) {
			if (isNodeNamed(ifChildren.item(i), PREFIX, "then"))
				thenNode = ifChildren.item(i);
			else if (isNodeNamed(ifChildren.item(i), PREFIX, "else"))
				elseNode = ifChildren.item(i);
		}
						
		// Decide what part of the sub tree (if any) shall be used
		NodeList resultChildren;
		if (cond.equals(Boolean.TRUE)) {
			if (thenNode == null)
				resultChildren = ifChildren;
			else
				resultChildren = thenNode.getChildNodes();
		}
		else {
			if (elseNode == null)
				resultChildren = null;
			else
				resultChildren = elseNode.getChildNodes();
		}
		
		// Attach the result to parent and remove if tag
		Node ifParent = tagNode.getParentNode();
		if (!(resultChildren == null || resultChildren.getLength() == 0)) { 
			Node resultParent = resultChildren.item(0).getParentNode();
			Node resultChild;
			while (resultChildren.getLength() > 0) {
				resultChild = resultChildren.item(0);
				resultParent.removeChild(resultChild);
				ifParent.appendChild(resultChild);
			}
		}
		ifParent.removeChild(tagNode);
		
		handleChildren(ifParent, env);
	}
}	
	