package org.argoprint.engine.interpreters;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InterpreterBind extends Interpreter {

	public InterpreterBind(ArgoPrintDataSource dataSource) {
		super("bind", dataSource);
	}

	/**
	 * Processes the bind tag.
	 * 
	 * @param tagNode
	 * @param env
	 */
	protected void processTag(Node tagNode, Environment env) throws Exception {
		
		// Create the new element
		NamedNodeMap attributes = tagNode.getAttributes();
		Node attr = attributes.getNamedItem("name");
		if (attr == null)
			throw new BadTemplateException("Bind tag contains no name attribute.");
		Document document = tagNode.getOwnerDocument();
		Element newElement = document.createElement(attr.getNodeValue());

        NodeList children;
		NodeList bindChildren = tagNode.getChildNodes();
		for (int i = 0; i < bindChildren.getLength();) {
            Node child = bindChildren.item(i);
			if (isNodeNamed(child, "argoprint", "attr")) {
				attributes = child.getAttributes();
				attr = attributes.getNamedItem("name");
                if (attr == null)
                    throw new BadTemplateException("attr tag contains no name attribute.");
                children = child.getChildNodes();
                if (children.getLength() != 1)
                    throw new BadTemplateException("attr tag must have excactly one child.");
                // Recurse on the contents of the attr tag.
                _firstHandler.handleTag(children.item(0), env);
                children = child.getChildNodes();
                if (children.getLength() != 1)
                    throw new BadTemplateException("Child of attr tag must evaluate to excatly one tag.");
                if (children.item(0).getNodeType() != Node.TEXT_NODE)
                    throw new BadTemplateException("Child of attr tag must evaluate to text.");
                newElement.setAttribute(attr.getNodeValue(), children.item(0).getNodeValue());
                i++;
            }
            else {
                System.out.println("getLength before removal: " + bindChildren.getLength());
                tagNode.removeChild(child);
                System.out.println("getLength after removal: " + bindChildren.getLength());
                
                newElement.appendChild(child);
                _firstHandler.handleTag(child, env);
                // Don't increment i since removeChild removed index i
            }
        }
        Node parentNode = tagNode.getParentNode();
        parentNode.replaceChild(newElement, tagNode);
    }
	
	private boolean isNodeNamed(Node node, String prefix, String localName) {
        if ((node.getLocalName() == null) || node.getPrefix() == null)
            return false;
        else
		    return (node.getLocalName().equals(localName) && node.getPrefix().equals(prefix));
	}
}