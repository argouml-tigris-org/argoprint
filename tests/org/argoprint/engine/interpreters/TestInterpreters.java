package org.argoprint.engine.interpreters;

import junit.framework.*;
import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;

import org.argoprint.engine.interpreters.*;
import org.argoprint.engine.Environment;
import org.argoprint.DataSourceStub;

import java.io.*;

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

    public void testDefault() throws Exception {
        // Get a node that should not be changed.
        Node node = _doc.getChildNodes().item(0);
        Node param_node = node.cloneNode(true);
        Node comp_node = node.cloneNode(true);

        _default.handleTag(param_node, _env);

        assertTrue("Default handler alters node.",
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

/*
        _bind    = new InterpreterBind();
        _call    = new InterpreterCall();
        _if      = new InterpreterIf();
        _iterate = new InterpreterIterate();
*/
        _default = new InterpreterDefault(_uml_int);
        _default.setFirstHandler(_default);



/*
        assertNotNull("Could not create InterpreterBind.",    _bind);
        assertNotNull("Could not create InterpreterCall.",    _call);
        assertNotNull("Could not create InterpreterIf.",      _if);
        assertNotNull("Could not create InterpreterIterate.", _iterate);
*/
        //assertNotNull("Could not create InterpreterDefault.", _default);
    }

    private boolean nodesEqual(Node left, Node right) {
        // Consider nodes equal if the following attributes are equal.
        return left.getNodeName() == right.getNodeName() &&
               left.getNodeType() == right.getNodeType() &&
               left.getNodeValue() == right.getNodeValue() &&
               left.getPrefix() == right.getPrefix() &&
               left.getNamespaceURI() == right.getNamespaceURI() &&
               left.getLocalName() == right.getLocalName();

    }
}
