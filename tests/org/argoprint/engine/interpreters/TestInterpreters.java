package org.argoprint.engine.interpreters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.argoprint.DataSourceStub;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestInterpreters extends TestCase {
    private class InterpreterTestException extends Exception {
        public InterpreterTestException(String message) {
            super(message);
        }
    };

    private InterpreterBind    _bind    = null;
    private InterpreterCall    _call    = null;
    private InterpreterIf      _if      = null;
    private InterpreterIterate _iterate = null;
    private InterpreterDefault _default = null;

    private DataSourceStub _uml_int            = null;
    private Document       _correct_template   = null;
    private Document       _incorrect_template = null;
    private Environment    _env                = null;

    private HashMap _interpreters   = null;

    public TestInterpreters(String s) {
        super(s);
    }

    public static Test suite() {
        // Code calling the tests.
        return new TestSuite(TestInterpreters.class);
    }

    public void InterpreterTest(String name, Document doc, int num) throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = doc.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node compare_node  = null;
        name = name.toLowerCase();

        // Get the node containing the expected result.
        int expect_pos = findNode(nodes, "expected_ap_"+name+"_result", 0);
        assertTrue("expected_ap_"+name+"_result-node not found.", expect_pos != -1);
        NodeList expect_nodes = nodes.item(expect_pos).getChildNodes();
        int collect_pos = findNode(expect_nodes, "collect_"+name, 0);
        assertTrue("collect_"+name+"-node for expected result not found.", collect_pos != -1);
        for (int i = 0; i < num; ++i) {
            collect_pos = findNode(expect_nodes, "collect_"+name, collect_pos+1);
            assertTrue("collect_"+name+"-node for expected result not found.", collect_pos != -1);
        }
        compare_node = expect_nodes.item(collect_pos);

        // Get the node that collects the nodes that are to be transformed.
        collect_pos = findNode(nodes, "collect_"+name, 0);
        assertTrue("collect_"+name+"-node not found.", collect_pos != -1);
        for (int i = 0; i < num; ++i) {
            collect_pos = findNode(nodes, "collect_"+name, collect_pos+1);
            assertTrue("collect_"+name+"-node not found.", collect_pos != -1);
        }
        NodeList tag_nodes = nodes.item(collect_pos).getChildNodes();

        // Get the nodes that are about to be transformed.
        int tag_pos = findNode(tag_nodes, "ap:"+name, 0);
        assertTrue("ap:"+name+"-node not found.", tag_pos != -1);
        Node tag_node = tag_nodes.item(tag_pos);
        ((Interpreter)_interpreters.get(name)).handleTag(tag_node, _env);

        // Get the node at the same position as the call-node used to be.
        Node result_node = nodes.item(collect_pos);

        assertTrue(name+"-interpreter does not give expected result.",
                   nodesEqual(compare_node, result_node));
    }




    /*
     * Test cases.
     *
     */
    public void testCorrectIterate() throws Exception {
        InterpreterTest("iterate", _correct_template, 0);
    }

    public void testCorrectIterateWithSortvalue() throws Exception {
        InterpreterTest("iterate", _correct_template, 1);
    }

    public void testCorrectBind() throws Exception {
        InterpreterTest("bind", _correct_template, 0);
    }
 
    public void testCorrectIf() throws Exception {
        InterpreterTest("if", _correct_template, 0);
    }
 
    public void testCorrectCall() throws Exception {
        InterpreterTest("call", _correct_template, 0);
    }

    public void testDefault() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = _correct_template.getChildNodes().item(0).getChildNodes();
        Node param_node = null;
        Node comp_node  = null;


        int pos = findNode(nodes, "default_test", 0);

        param_node = nodes.item(pos).cloneNode(true);
        comp_node  = nodes.item(pos).cloneNode(true);

        _default.handleTag(param_node, _env);

        assertTrue("Default-interpreter does not give expected result.",
                   nodesEqual(param_node, comp_node));
    }

    public void testIterateIncorrectSortvalue() throws Exception {
        boolean exception_caught = false;
        try {
            InterpreterTest("iterate", _incorrect_template, 0);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("Exception not thrown for <iterate> with bad sortvalue.");
        }
    }

    public void testIterateIncorrectWhat() throws Exception {
        boolean exception_caught = false;
        try {
            InterpreterTest("iterate", _incorrect_template, 1);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("Exception not thrown for <iterate> called with nonexistant method.");
        }
    }

    public void testIterateNoIteratorName() throws Exception {
        boolean exception_caught = false;
        try {
            InterpreterTest("iterate", _incorrect_template, 2);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("Exception not thrown for <iterate> without iteratorname.");
        }
    }

    public void testCallIncorrectWhat() throws Exception {
        boolean exception_caught = false;
        try {
            InterpreterTest("call", _incorrect_template, 0);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("Exception not thrown for <call> with nonexistant method.");
        }
    }

    public void testIfIncorrectWhat() throws Exception {
        boolean exception_caught = false;
        try {
            InterpreterTest("if", _incorrect_template, 0);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("Exception not thrown for <if> with non-boolean method.");
        }
    }

    public void testBindAttrWithQuote() throws Exception {
        InterpreterTest("bind", _incorrect_template, 0);
    }






    protected void setUp() throws IOException {
        // Prepare the nodes from the template.
        DOMParser parser = new DOMParser();

        String correct_template_name = "CorrectTemplate.xml";
        String incorrect_template_name = "IncorrectTemplate.xml";

        // Parse the correct template.
        try {
            parser.parse("org/argoprint/engine/interpreters/" +
                         correct_template_name);
        } catch (org.xml.sax.SAXException e) {
            fail("Could not parse " + correct_template_name + ": " +
                 e.getMessage());
        }
        _correct_template = parser.getDocument();
        assertNotNull("Could not get the "+correct_template_name+" from the parser.", _correct_template);

        // Parse the incorrect template.
        try {
            parser.parse("org/argoprint/engine/interpreters/" +
                         incorrect_template_name);
        } catch (org.xml.sax.SAXException e) {
            fail("Could not parse " + incorrect_template_name + ": " +
                 e.getMessage());
        }
        _incorrect_template = parser.getDocument();
        assertNotNull("Could not get " + incorrect_template_name + " from the parser.", _incorrect_template);

        _uml_int = new DataSourceStub();
        assertNotNull(_uml_int);

        _env = new Environment();
        assertNotNull(_env);

        _interpreters = new HashMap();

        _default = new InterpreterDefault(_uml_int);
        _default.setFirstHandler(_default);
        _interpreters.put("default", _default);

        _call = new InterpreterCall(_uml_int);
        _call.setFirstHandler(_call);
        _call.setNextHandler(_default);
        _interpreters.put("call", _call);

        _iterate = new InterpreterIterate(_uml_int);
        _iterate.setFirstHandler(_iterate);
        _iterate.setNextHandler(_call);
        _interpreters.put("iterate", _iterate);

        _if = new InterpreterIf(_uml_int);
        _if.setFirstHandler(_if);
        _if.setNextHandler(_default);
        _interpreters.put("if", _if);

        _bind = new InterpreterBind(_uml_int);
        _bind.setFirstHandler(_bind);
        _bind.setNextHandler(_default);
        _interpreters.put("bind", _bind);


        assertNotNull("Could not create InterpreterBind.",    _bind);
        assertNotNull("Could not create InterpreterIf.",      _if);
        assertNotNull("Could not create InterpreterCall.",    _call);
        assertNotNull("Could not create InterpreterIterate.", _iterate);
        assertNotNull("Could not create InterpreterDefault.", _default);
    }


    /*
     * Some utility functions.
     */

    private void outputDocument(Document doc) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        OutputFormat of = new OutputFormat(doc);
        of.setIndent(4);
        of.setIndenting(true);
        of.setOmitComments(true);
        of.setPreserveSpace(false);
        of.setLineWidth(80);
        (new XMLSerializer(result, of)).serialize(doc);

        System.out.print(result.toString());
    }

    private boolean nodesEqual(Node left, Node right) throws IOException {
        ByteArrayOutputStream lstream = new ByteArrayOutputStream();
        ByteArrayOutputStream rstream = new ByteArrayOutputStream();
        OutputFormat of = new OutputFormat();
        of.setIndent(4);
        of.setPreserveSpace(false);
        of.setLineWidth(80);
        (new XMLSerializer(lstream, of)).serialize((Element)left);
        (new XMLSerializer(rstream, of)).serialize((Element)right);

        String lstring = strip_ws(lstream.toString());
        String rstring = strip_ws(rstream.toString());

        return lstring.equals(rstring);
    }

    private String strip_ws(String in) {
        StringBuffer buf = new StringBuffer(in);
        for (int i = buf.length()-1; i >=0; --i) {
            char c = buf.charAt(i);
            if (c == ' ' || c == '\n' || c == '\t') {
                buf.deleteCharAt(i);
            }
        }
        return buf.toString();
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
