package org.argoprint.engine.interpreters;

import junit.framework.*;
import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.*;

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

    public TestInterpreters(String s) {
        super(s);
    }

    public static Test suite() {
        // Code calling the tests.
        /*
        TestSuite s = new TestSuite();
        s.addTest(new TestInterpreters("testIterate"));
        return s;
        */
        return new TestSuite(TestInterpreters.class);
    }

    public void testIterate() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = _doc.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node comp_node  = null;

        int expect_pos = findNode(nodes, "expected_ap_iterate_result", 0);
        assertTrue("expected_ap_iterate_result-node not found.", expect_pos != -1);
        int collect_pos = findNode(nodes.item(expect_pos).getChildNodes(), "collect_iterate", 0);
        comp_node  = nodes.item(expect_pos).getChildNodes().item(collect_pos);

        collect_pos = findNode(nodes, "collect_iterate", 0);
        assertTrue("collect_iterate-node not found.", collect_pos != -1);
        int iterate_pos = findNode(nodes.item(collect_pos).getChildNodes(), "ap:iterate", 0);
        assertTrue("ap:iterate-node not found.", iterate_pos != -1);
        param_node = nodes.item(collect_pos).getChildNodes().item(iterate_pos);

        _iterate.handleTag(param_node, _env);

        // Get the node at the same position as the call-node used to be.
        Node result_node = nodes.item(collect_pos);

        ByteArrayOutputStream comp_stream   = new ByteArrayOutputStream();
        ByteArrayOutputStream result_stream = new ByteArrayOutputStream();

        XMLSerializer ser1 = new XMLSerializer(comp_stream, null);
        XMLSerializer ser2 = new XMLSerializer(result_stream, null);
        ser1.serialize((Element)comp_node);
        ser2.serialize((Element)result_node);

        assertTrue("Iterate-interpreter does not give expected result.",
                   comp_stream.toString().equals(result_stream.toString()));
    }
 
    public void testCall() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = _doc.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node comp_node  = null;

        // Find the position of the expected result for the call node.
        int expect_pos = findNode(nodes, "expected_ap_call_result", 0);
        // Find the position of the cellction-node within that node.
        int collect_pos = findNode(nodes.item(expect_pos).getChildNodes(), "collect_call", 0);
        comp_node  = nodes.item(expect_pos).getChildNodes().item(collect_pos);

        // Find the position of the collect_call around the ap:call-node.
        collect_pos = findNode(nodes, "collect_call", 0);
        assertTrue(collect_pos != -1);
        // Find the position of the ap:call-tag within that node.
        int call_pos = findNode(nodes.item(collect_pos).getChildNodes(), "ap:call", 0);
        assertTrue(call_pos != -1);
        param_node = nodes.item(collect_pos).getChildNodes().item(call_pos);

        _call.handleTag(param_node, _env);

        // Get the node at the same position as the collect_call-node used to
        // be.
        Node result_node = nodes.item(collect_pos);

        ByteArrayOutputStream comp_stream   = new ByteArrayOutputStream();
        ByteArrayOutputStream result_stream = new ByteArrayOutputStream();

        XMLSerializer ser1 = new XMLSerializer(comp_stream, null);
        XMLSerializer ser2 = new XMLSerializer(result_stream, null);
        ser1.serialize((Element)comp_node);
        ser2.serialize((Element)result_node);

        assertTrue("Call-interpreter does not give expected result.",
                   comp_stream.toString().equals(result_stream.toString()));
    }

    public void testDefault() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = _doc.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node comp_node  = null;


        int pos = findNode(nodes, "default", 0);

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

        _default = new InterpreterDefault(_uml_int);
        _default.setFirstHandler(_default);
        _call    = new InterpreterCall(_uml_int);
        _call.setFirstHandler(_call);
        _iterate = new InterpreterIterate(_uml_int);
        _iterate.setFirstHandler(_iterate);
        _iterate.setNextHandler(_default);
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
                System.out.println("One node null");
                return false;
            } else {
                return true;
            }
        }

        if (left.getNodeType() != right.getNodeType()) {
            System.out.println("Node types not same");
            return false;
        }
        if (left.getChildNodes().getLength() != right.getChildNodes().getLength()) {
            System.out.println("Not same amount of children");
            System.out.println(left.getChildNodes().getLength());
            System.out.println(right.getChildNodes().getLength());
            return false;
        }

        if (left.getAttributes() == null || right.getAttributes() == null) {
            if (left.getAttributes() != right.getAttributes()) {
                System.out.println("One attributes null");
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
