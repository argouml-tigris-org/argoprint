package org.argoprint.engine.interpreters;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Iterator;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.ArgoPrintIterator;
import org.argoprint.engine.Environment;
import org.w3c.dom.*;

public class InterpreterIterate extends Interpreter {

	public InterpreterIterate(ArgoPrintDataSource dataSource) {
		super("iterate", dataSource);
	}

	/**
	 * Processes the iterate tag.
	 */
	protected void processTag(Node tagNode, Environment env) throws Exception {
		NamedNodeMap attributes = tagNode.getAttributes();
		
		// Get the collection
		Object callReturnValue = callDataSource("what", attributes, env);
		if (!(callReturnValue instanceof Collection))
			throw new Exception("The object returned from the call to the data source is not a collection.");
		Collection iterateCollection = (Collection) callReturnValue;

		Node sortvalueAttribute = attributes.getNamedItem("sortvalue");
		if (sortvalueAttribute != null) {
			String sortvalue = sortvalueAttribute.getNodeValue();
			iterateCollection = sortCollection(iterateCollection, sortvalue);
		}
			
		Node iteratornameAttribute = attributes.getNamedItem("iteratorname");
		if (iteratornameAttribute == null) 
			throw new BadTemplateException("Iterate tag contains no iteratorname attribute.");
		String iteratorname = iteratornameAttribute.getNodeValue();
		
		ArgoPrintIterator iterator = new ArgoPrintIterator(iterateCollection.iterator());
		env.addIterator(iteratorname, iterator);

		Document document = tagNode.getOwnerDocument();
		Node parentNode = tagNode.getParentNode();
		NodeList children = tagNode.getChildNodes();
		Node newNode;

		// For every object in the iterator, clone every child, attach it to the parent and recurse
		while (iterator.hasNext()) {
			iterator.next();
			for (int i = 0; i < children.getLength(); i++) {
				newNode = children.item(i).cloneNode(true);
				parentNode.insertBefore(newNode, tagNode);
				_firstHandler.handleTag(newNode, env);
			}
		}
		env.removeIterator(iteratorname);
		parentNode.removeChild(tagNode);
	}
	
	/**
	 * Sorts a Collection according to a String returned by a call to the data source for each object in the collection. 
	 * 
	 * @param collection The collection to sort.
	 * @param sortvalue The call that will be applied to each object providing a value to sort on.
	 * @return A sorted Collection.
	 * @throws Exception
	 */
	private Collection sortCollection(Collection collection, String sortvalue) throws Exception {
		Iterator iterator = collection.iterator();
		TreeMap sortedMap = new TreeMap();
		Object object;
		Object returned;
		while (iterator.hasNext()) {
			object = iterator.next();
			returned = _dataSource.caller(sortvalue, object);
			if (!(returned instanceof String))
				throw new Exception("The sortvalue function did not return a String.");
			sortedMap.put(returned, object);
		}
		return sortedMap.values();
	}
}