package org.argoprint.engine.interpreters;

import java.io.IOException;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xerces.parsers.DOMParser;
import org.argoprint.DataSourceStub;
import org.argoprint.XmlTestUtil;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestInterpreters extends TestCase {
    private class InterpreterTestException extends Exception {
        public InterpreterTestException(String message) {
            super(message);
        }
    };

    private InterpreterDefault defaultInterpreter = null;

    private DataSourceStub umlInt            = null;
    private Document       correctTemplate   = null;
    private Document       incorrectTemplate = null;
    private Environment    env                = null;

    private HashMap interpreters   = null;

    /**
     * Constructor.
     *
     * @param s The name of the test case.
     */
    public TestInterpreters(String s) {
        super(s);
    }

    /**
     * @return a test.
     */
    public static Test suite() {
        // Code calling the tests.
        return new TestSuite(TestInterpreters.class);
    }

    public void interpreterTest(String name, Document doc, int num)
    	throws Exception {
        // Get a node that should not be changed.
        NodeList nodes  = doc.getChildNodes().item(0).getChildNodes();
        Node compareNode  = null;
        name = name.toLowerCase();

        // Get the node containing the expected result.
        int expectPos = findNode(nodes, "expected_ap_" + name + "_result", 0);
        assertTrue("expected_ap_" + name + "_result-node not found.",
                   expectPos != -1);
        NodeList expectNodes = nodes.item(expectPos).getChildNodes();
        int collectPos = findNode(expectNodes, "collect_" + name, 0);
        assertTrue("collect_" + name + "-node for expected result not found.",
                collectPos != -1);
        for (int i = 0; i < num; ++i) {
            collectPos =
                findNode(expectNodes, "collect_" + name, collectPos + 1);
            assertTrue("collect_" + name
                       + "-node for expected result not found.",
                    collectPos != -1);
        }
        compareNode = expectNodes.item(collectPos);

        // Get the node that collects the nodes that are to be transformed.
        collectPos = findNode(nodes, "collect_" + name, 0);
        assertTrue("collect_" + name + "-node not found.", collectPos != -1);
        for (int i = 0; i < num; ++i) {
            collectPos = findNode(nodes, "collect_" + name, collectPos + 1);
            assertTrue("collect_" + name + "-node not found.",
                    collectPos != -1);
        }
        NodeList tagNodes = nodes.item(collectPos).getChildNodes();

        // Get the nodes that are about to be transformed.
        int tagPos = findNode(tagNodes, "ap:" + name, 0);
        assertTrue("ap:" + name + "-node not found.", tagPos != -1);
        Node tagNode = tagNodes.item(tagPos);
        ((Interpreter) interpreters.get(name)).handleTag(tagNode, env);

        // Get the node at the same position as the call-node used to be.
        Node resultNode = nodes.item(collectPos);

        assertTrue(name + "-interpreter does not give expected result.",
                   XmlTestUtil.nodesEqual(compareNode, resultNode));
    }

    /*
     * Test cases.
     *
     */
    public void testCorrectIterate() throws Exception {
        interpreterTest("iterate", correctTemplate, 0);
    }

    public void testCorrectIterateWithSortvalue() throws Exception {
        interpreterTest("iterate", correctTemplate, 1);
    }

    public void testCorrectBind() throws Exception {
        interpreterTest("bind", correctTemplate, 0);
    }

    public void testCorrectIf() throws Exception {
        interpreterTest("if", correctTemplate, 0);
    }

    public void testCorrectCall() throws Exception {
        interpreterTest("call", correctTemplate, 0);
    }

    public void testDefault() throws Exception {
        // Get a node that should not be changed.
        NodeList nodes =
            correctTemplate.getChildNodes().item(0).getChildNodes();
        Node paramNode = null;
        Node compNode  = null;

        int pos = findNode(nodes, "default_test", 0);

        paramNode = nodes.item(pos).cloneNode(true);
        compNode  = nodes.item(pos).cloneNode(true);

        defaultInterpreter.handleTag(paramNode, env);

        assertTrue("Default-interpreter does not give expected result.",
                   XmlTestUtil.nodesEqual(paramNode, compNode));
    }

    public void testIterateIncorrectSortvalue() throws Exception {
        boolean exceptionCaught = false;
        try {
            interpreterTest("iterate", incorrectTemplate, 0);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("Exception not thrown for <iterate> with bad sortvalue.");
        }
    }

    public void testIterateIncorrectWhat() throws Exception {
        boolean exceptionCaught = false;
        try {
            interpreterTest("iterate", incorrectTemplate, 1);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("Exception not thrown for <iterate> called with "
                 + "nonexistant method.");
        }
    }

    public void testIterateNoIteratorName() throws Exception {
        boolean exceptionCaught = false;
        try {
            interpreterTest("iterate", incorrectTemplate, 2);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("Exception not thrown for <iterate> without iteratorname.");
        }
    }

    public void testCallIncorrectWhat() throws Exception {
        boolean exceptionCaught = false;
        try {
            interpreterTest("call", incorrectTemplate, 0);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("Exception not thrown for <call> with nonexistant method.");
        }
    }

    public void testIfIncorrectWhat() throws Exception {
        boolean exceptionCaught = false;
        try {
            interpreterTest("if", incorrectTemplate, 0);
        } catch (InterpreterTestException e) {
            throw e;
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("Exception not thrown for <if> with non-boolean method.");
        }
    }

    public void testBindAttrWithQuote() throws Exception {
        interpreterTest("bind", incorrectTemplate, 0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws IOException {
        // Prepare the nodes from the template.
        DOMParser parser = new DOMParser();

        String correctTemplateName = "CorrectTemplate.xml";
        String incorrectTemplateName = "IncorrectTemplate.xml";

        // Parse the correct template.
        try {
            parser.parse("tests/org/argoprint/engine/interpreters/"
                    + correctTemplateName);
        } catch (org.xml.sax.SAXException e) {
            fail("Could not parse " + correctTemplateName + ": "
                    + e.getMessage());
        }
        correctTemplate = parser.getDocument();
        assertNotNull("Could not get the " + correctTemplateName
                + " from the parser.",
                correctTemplate);

        // Parse the incorrect template.
        try {
            parser.parse("tests/org/argoprint/engine/interpreters/"
                    + incorrectTemplateName);
        } catch (org.xml.sax.SAXException e) {
            fail("Could not parse " + incorrectTemplateName + ": "
                    + e.getMessage());
        }
        incorrectTemplate = parser.getDocument();
        assertNotNull("Could not get " + incorrectTemplateName
                + " from the parser.",
                incorrectTemplate);

        umlInt = new DataSourceStub();
        assertNotNull(umlInt);

        env = new Environment();
        assertNotNull(env);

        interpreters = new HashMap();

        defaultInterpreter = new InterpreterDefault(umlInt);
        defaultInterpreter.setFirstHandler(defaultInterpreter);
        interpreters.put("default", defaultInterpreter);

        InterpreterCall callInterpreter = new InterpreterCall(umlInt);
        callInterpreter.setFirstHandler(callInterpreter);
        callInterpreter.setNextHandler(defaultInterpreter);
        interpreters.put("call", callInterpreter);

        InterpreterIterate iterateInterpreter = new InterpreterIterate(umlInt);
        iterateInterpreter.setFirstHandler(iterateInterpreter);
        iterateInterpreter.setNextHandler(callInterpreter);
        interpreters.put("iterate", iterateInterpreter);

        InterpreterIf ifInterpreter = new InterpreterIf(umlInt);
        ifInterpreter.setFirstHandler(ifInterpreter);
        ifInterpreter.setNextHandler(defaultInterpreter);
        interpreters.put("if", ifInterpreter);

        InterpreterBind bindInterpreter = new InterpreterBind(umlInt);
        bindInterpreter.setFirstHandler(bindInterpreter);
        bindInterpreter.setNextHandler(defaultInterpreter);
        interpreters.put("bind", bindInterpreter);


        assertNotNull("Could not create InterpreterBind.",    bindInterpreter);
        assertNotNull("Could not create InterpreterIf.",      ifInterpreter);
        assertNotNull("Could not create InterpreterCall.",    callInterpreter);
        assertNotNull("Could not create InterpreterIterate.",
                iterateInterpreter);
        assertNotNull("Could not create InterpreterDefault.",
                defaultInterpreter);
    }


    private int findNode(NodeList nodes, String name, int start) {
        for (int i = start; i < nodes.getLength(); ++i) {
            if (nodes.item(i).getNodeName().equals(name)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * @param params Arguments.
     */
    public static void main(String[] params) {
        junit.textui.TestRunner.run(suite());
    }
}
