package org.argoprint.engine.interpreters;

import java.util.Vector;

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
		// TODO: divide into some private methods

		// Evaluate condition
		NamedNodeMap attributes = tagNode.getAttributes();
		Object returnValue = callDataSource("cond", attributes, env);
		if (!(returnValue instanceof Boolean))
			throw new BadTemplateException("The condition did not evaluate to a Boolean.");
		
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
		if (((Boolean)returnValue).booleanValue()) {
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
			Vector resultVector = getVector(resultChildren);
			for (int i = 0; i < resultVector.size(); i++) {
				Node child = (Node)resultVector.get(i);
				resultParent.removeChild(child);
				ifParent.insertBefore(child, tagNode);
			}
			recurse(resultVector, env);
		}
		ifParent.removeChild(tagNode);
	}
	

}
	