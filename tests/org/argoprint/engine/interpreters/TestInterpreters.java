// $Id$
// Copyright (c) 2005, Linus Tolke.
//
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright
//   notice, this list of conditions and the following disclaimer in the
//   documentation and/or other materials provided with the distribution.
//
// * Neither the name of the University of Linköping nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.

package org.argoprint.engine.interpreters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xerces.parsers.DOMParser;
import org.argoprint.DataSourceStub;
import org.argoprint.UnsupportedCallException;
import org.argoprint.XmlTestUtil;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test for the interpreters.
 */
public class TestInterpreters extends TestCase {
    private InterpreterDefault defaultInterpreter;

    private DataSourceStub umlInt;
    private Document       correctTemplate;
    private Document       incorrectTemplate;
    private Environment    env;

    private Map interpreters;

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

    /**
     * Do the tests.
     *
     * @param nameArg The interpreter to test.
     * @param doc The document.
     * @param num The number in the node.
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void interpreterTest(String nameArg, Document doc, int num)
    	throws BadTemplateException, UnsupportedCallException {
        // Get a node that should not be changed.
        NodeList nodes  = doc.getChildNodes().item(0).getChildNodes();
        Node compareNode  = null;
        String name = nameArg.toLowerCase();

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

    /**
     * Test iterate.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testCorrectIterate()
    	throws BadTemplateException, UnsupportedCallException {
        interpreterTest("iterate", correctTemplate, 0);
    }

    /**
     * Test iterate.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testCorrectIterateWithSortvalue()
    	throws BadTemplateException, UnsupportedCallException {
        interpreterTest("iterate", correctTemplate, 1);
    }

    /**
     * Test bind.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testCorrectBind()
    	throws BadTemplateException, UnsupportedCallException {
        interpreterTest("bind", correctTemplate, 0);
    }

    /**
     * Test if.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testCorrectIf()
    	throws BadTemplateException, UnsupportedCallException {
        interpreterTest("if", correctTemplate, 0);
    }

    /**
     * Test call.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testCorrectCall()
    	throws BadTemplateException, UnsupportedCallException {
        interpreterTest("call", correctTemplate, 0);
    }

    /**
     * Test iterate.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testDefault()
    	throws BadTemplateException, UnsupportedCallException {
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

    /**
     * Test iterate with incorrect sort value.
     *
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testIterateIncorrectSortvalue() throws BadTemplateException {
        try {
            interpreterTest("iterate", incorrectTemplate, 0);
        } catch (UnsupportedCallException e) {
            return;
        }
        fail("Exception not thrown for <iterate> with bad sortvalue.");
    }

    /**
     * Test iterate with incorrect what.
     *
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testIterateIncorrectWhat() throws BadTemplateException {
        try {
            interpreterTest("iterate", incorrectTemplate, 1);
        } catch (UnsupportedCallException e) {
            return;
        }
        fail("Exception not thrown for <iterate> called with "
                + "nonexistant method.");
    }

    /**
     * Test iterate without name.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     */
    public void testIterateNoIteratorName() throws UnsupportedCallException {
        try {
            interpreterTest("iterate", incorrectTemplate, 2);
        } catch (BadTemplateException e) {
            return;
        }
        fail("Exception not thrown for <iterate> without iteratorname.");
    }

    /**
     * Test iterate with incorrect what.
     *
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testCallIncorrectWhat() throws BadTemplateException {
        try {
            interpreterTest("call", incorrectTemplate, 0);
        } catch (UnsupportedCallException e) {
            return;
        }
        fail("Exception not thrown for <call> with nonexistant method.");
    }

    /**
     * Test if with incorrect what.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     */
    public void testIfIncorrectWhat() throws UnsupportedCallException {
        try {
            interpreterTest("if", incorrectTemplate, 0);
        } catch (BadTemplateException e) {
            return;
        }
        fail("Exception not thrown for <if> with non-boolean method.");
    }

    /**
     * Test bind.
     *
     * @throws UnsupportedCallException if we cannot call evaluate a call.
     * @throws BadTemplateException if the template is incorrect.
     */
    public void testBindAttrWithQuote()
    	throws BadTemplateException, UnsupportedCallException {
        interpreterTest("bind", incorrectTemplate, 0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws IOException {
        // Prepare the nodes from the template.

        String correctTemplateName = "CorrectTemplate.xml";
        String incorrectTemplateName = "IncorrectTemplate.xml";

        // Parse the correct template.
        correctTemplate = parseTestTemplate(correctTemplateName);
        assertNotNull("Could not get the " + correctTemplateName
                + " from the parser.",
                correctTemplate);

        // Parse the incorrect template.
        incorrectTemplate = parseTestTemplate(incorrectTemplateName);
        assertNotNull("Could not get " + incorrectTemplateName
                + " from the parser.",
                incorrectTemplate);

        umlInt = new DataSourceStub();
        env = new Environment();
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
    }


    /**
     * Parse a test template.
     *
     * @param templateName The name of the template.
     * @return The parsed document.
     * @throws IOException if we cannot read the file.
     */
    private Document parseTestTemplate(String templateName) throws IOException {
        DOMParser parser = new DOMParser();
        try {
            parser.parse("tests/org/argoprint/engine/interpreters/"
                    + templateName);
        } catch (org.xml.sax.SAXException e) {
            fail("Could not parse " + templateName + ": "
                    + e.getMessage());
        }
        Document document = parser.getDocument();
        return document;
    }

    /**
     * Find the index of a node in a NodeList.
     *
     * @param nodes The NodeList.
     * @param name The node name to search for.
     * @param start Where to start.
     * @return The index.
     */
    private int findNode(NodeList nodes, String name, int start) {
        for (int i = start; i < nodes.getLength(); ++i) {
            if (nodes.item(i).getNodeName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
