package org.argoprint.engine.interpreters;

import java.util.Collection;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.ArgoPrintIterator;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InterpreterIterate extends Interpreter {

	public InterpreterIterate(ArgoPrintDataSource dataSource) {
		super("iterate", dataSource);
	}

	/**
	 * Processes the iterate tag.
	 * 
	 * @param tagNode
	 * @param env
	 */
	protected void processTag(Node tagNode, Environment env) throws Exception {
		NamedNodeMap attributes = tagNode.getAttributes();
		
		// Get the collection
		Object callReturnValue = callDataSource(attributes, env);
		if (!(callReturnValue instanceof Collection))
			throw new Exception("The object returned from the call to the data source is not a collection.");
		Collection iterateCollection = (Collection) callReturnValue;
			
		Node iteratorNameAttribute = attributes.getNamedItem("iteratorname");
		if (iteratorNameAttribute == null) 
			throw new BadTemplateException("Iterate tag contains no iteratorname attribute.");
		String iteratorName = iteratorNameAttribute.getNodeValue();

		// TODO: Implement sorting, for now I just ignore sortvalue
		ArgoPrintIterator iterator = new ArgoPrintIterator(iterateCollection.iterator());
		env.addIterator(iteratorName, iterator);

		Document document = tagNode.getOwnerDocument();
		Node parentNode = tagNode.getParentNode();
		NodeList children = tagNode.getChildNodes();
		Node newNode;

		// First detach the children from the iterate tag
		for (int i = 0; i < children.getLength(); i++) 
			tagNode.removeChild(children.item(i));
		
		// For every object in the iterator, clone every child, attach it to the parent and recurse
		while (iterator.hasNext()) {
			iterator.next();
			for (int i = 0; i < children.getLength(); i++) {
				newNode = children.item(i).cloneNode(true);
				parentNode.insertBefore(newNode, tagNode);
				_firstHandler.handleTag(newNode, env);
			}
		}
		env.removeIterator(iteratorName);
		parentNode.removeChild(tagNode);
	}
}	