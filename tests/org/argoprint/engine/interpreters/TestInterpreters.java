package org.argoprint.engine.interpreters;

import junit.framework.*;
import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;

import org.argoprint.engine.interpreters.*;
import org.argoprint.engine.Environment;
import org.argoprint.DataSourceStub;

import java.io.*;
import java.lang.*;

public class TestInterpreters extends TestCase {
    private InterpreterBind    _bind    = null;
    private InterpreterCall    _call    = null;
    //private InterpreterIf      _if      = null;
    private InterpreterIterate _iterate = null;
    private InterpreterDefault _default = null;

    private DataSourceStub _uml_int = null;
    private Document       _doc     = null;
    private Environment    _env     = null;


    public static Test suite() {
        // Code calling the tests.
        return new TestSuite(TestInterpreters.class);
    }

    public void testIterate() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = _doc.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node comp_node  = null;

        int pos = findNode(nodes, "expected_ap_iterate_result", 0);
        comp_node  = nodes.item(pos).getChildNodes().item(0);

        pos = findNode(nodes, "ap:iterate", 0);
        assertTrue(pos != -1);
        param_node = nodes.item(pos);

        _iterate.handleTag(param_node, _env);

        // Get the node at the same position as the call-node used to be.
        Node result_node = nodes.item(pos);

        assertTrue("Iterate-interpreter does not give expected result.",
                   nodesEqual(result_node, comp_node));
    }
 
    public void testCall() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = _doc.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node comp_node  = null;

        int pos = findNode(nodes, "expected_ap_call_result", 0);
        comp_node  = nodes.item(pos).getChildNodes().item(0);

        pos = findNode(nodes, "ap:call", 0);
        assertTrue(pos != -1);
        param_node = nodes.item(pos);

        _call.handleTag(param_node, _env);

        // Get the node at the same position as the call-node used to be.
        Node result_node = nodes.item(pos);

        assertTrue("Call-interpreter does not give expected result.",
                   nodesEqual(result_node, comp_node));
    }

    public void testDefault() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = _doc.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node comp_node  = null;


        int pos = findNode(nodes, "tok", 0);

        param_node = nodes.item(pos).cloneNode(true);
        comp_node  = nodes.item(pos).cloneNode(true);

        _default.handleTag(param_node, _env);

        assertTrue("Default-interpreter does not give expected result.",
                   nodesEqual(param_node, comp_node));
    }

    // TODO: Make sure the tests create objects correctly.
    protected void setUp() throws IOException {
        // Prepare the nodes from the template.
        DOMParser parser = new DOMParser();
        try {
            parser.parse("org/argoprint/engine/interpreters/testtemplate.xml");
        } catch (org.xml.sax.SAXException e) {
            fail("Could not parse testtemplate.xml: " + e.getMessage());
        }
        _doc = parser.getDocument();
        assertNotNull("Could not get the document.", _doc);

        _uml_int = new DataSourceStub();
        assertNotNull(_uml_int);

        _env = new Environment();
        assertNotNull(_env);

        _call    = new InterpreterCall(_uml_int);
        _call.setFirstHandler(_call);
        _iterate = new InterpreterIterate(_uml_int);
	_iterate.setFirstHandler(_iterate);
        _default = new InterpreterDefault(_uml_int);
        _default.setFirstHandler(_default);
/*
        _bind    = new InterpreterBind();
	_bind.setFirstHandler(_bind);
        _if      = new InterpreterIf();
	_if.setFirstHandler(_bind);
*/



/*
        assertNotNull("Could not create InterpreterBind.",    _bind);
        assertNotNull("Could not create InterpreterIf.",      _if);
*/
        assertNotNull("Could not create InterpreterCall.",    _call);
        assertNotNull("Could not create InterpreterIterate.", _iterate);
        assertNotNull("Could not create InterpreterDefault.", _default);
    }

    private boolean nodesEqual(Node left, Node right) {
        // Consider nodes equal if the following attributes are equal.
        class pair {
            public pair(String left, String right) { l = left; r = right; }
            public String l;
            public String r;
        };

        if (left == null || right == null) {
            if (left != right) {
                return false;
            } else {
                return true;
            }
        }

        if (left.getNodeType() != right.getNodeType()) {
            return false;
        }
        if (left.getChildNodes().getLength() != right.getChildNodes().getLength()) {
            return false;
        }

        if (left.getAttributes() == null || right.getAttributes() == null) {
            if (left.getAttributes() != right.getAttributes()) {
                return false;
            }
        } else if (!mapsEqual(left.getAttributes(), right.getAttributes())) {
            return false;
        }

        final int length = 5;
        pair results[] = new pair[length];
        results[0] = new pair(left.getNodeName(), right.getNodeName());
        results[1] = new pair(left.getNodeValue(), right.getNodeValue());
        results[2] = new pair(left.getPrefix(), right.getPrefix());
        results[3] = new pair(left.getNamespaceURI(), right.getNamespaceURI());
        results[4] = new pair(left.getLocalName(), right.getLocalName());

        for (int i = 0; i < length; ++i) {
            if ((results[i].l == null && results[i].r != null) ||
                (results[i].l != null && results[i].r == null)) {
                return false;
            }
            if (results[i].l != null &&
                results[i].r != null &&
                !results[i].l.equals(results[i].r)) {
                return false;
            }
        }

        for (int i = 0; i < right.getChildNodes().getLength(); ++i) {
            if (!nodesEqual(right.getChildNodes().item(i),
                            left.getChildNodes().item(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean mapsEqual(NamedNodeMap left, NamedNodeMap right) {
        if (left == null || right == null) {
            if (left == right) {
                return true;
            } else {
                return false;
            }
        }

        if (left.getLength() != right.getLength()) {
            return false;
        }

        for (int i = 0; i < left.getLength(); ++i) {
            if (!nodesEqual(left.item(i), right.item(i))) {
                return false;
            }
        }

        return true;
    }

    private int findNode(NodeList nodes, String name, int start) {
        for (int i = start; i < nodes.getLength(); ++i) {
            if (nodes.item(i).getNodeName().equals(name)) {
                return i;
            }
        }
        return -1;
    }


    public static void main(String params[]) {
        junit.textui.TestRunner.run(suite());
    }
}
