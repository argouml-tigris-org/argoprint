/*
******************************************************************
Copyright (c) 200, Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the xmlunit.sourceforge.net nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/

package org.custommonkey.xmlunit;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import junit.framework.AssertionFailedError;
import junit.framework.TestSuite;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Test case used to test the XMLTestCase
 */
public class test_XMLTestCase extends XMLTestCase{
    private final String[] control = new String[]{
        "<root/>",
        "<root></root>",
        "<root>test</root>",
        "<root attr=\"test\">test</root>",
        "<test/>",
        "<root>test</root>",
        "<root attr=\"test\"/>",
        "<root><outer><inner></inner></outer></root>",
        "<root attr=\"test\"><outer>test<inner>test</inner></outer></root>",
        "<root attr=\"test\"><outer>test<inner>test</inner></outer></root>"
    };
    private final String[] test = new String[]{
        "<fail/>",
        "<fail/>",
        "<fail>test</fail>",
        "<root>test</root>",
        "<fail/>",
        "<root>fail</root>",
        "<root attr=\"fail\"/>",
        "<root><outer><inner>test</inner></outer></root>",
        "<root attr=\"test\"><outer>fail<inner>test</inner></outer></root>",
        "<root attr=\"fail\"><outer>test<inner>test</inner></outer></root>"
    };

    /**
     *  Test for the compareXML method.
     */
    public void testCompareXMLStrings() throws Exception {
        for(int i=0;i<control.length;i++){
            assertEquals("compareXML case " + i + " failed", true,
                compareXML(control[i], control[i]).similar());
            assertEquals("!compareXML case " + i + " failed", false,
                compareXML(control[i], test[i]).similar());
        }
    }

    /**
     * Test the comparision of two files
     */
    public void testCompareFiles() throws Exception {
        assertXMLEqual(new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test1.xml"),
            new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test1.xml"));
        assertXMLNotEqual(new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test1.xml"),
            new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test2.xml"));

        try{
            assertXMLNotEqual(new FileReader("nosuchfile.xml"),
                new FileReader("nosuchfile.xml"));
            fail("Expecting FileNotFoundException");
        }catch(FileNotFoundException e){}
    }

    /**
     *  Test for the assertXMLEquals method.
     */
    public void testXMLEqualsStrings() throws Exception {
        for(int i=0;i<control.length;i++){
            assertXMLEqual("assertXMLEquals test case " + i + " failed",
                control[i], control[i]);
            assertXMLNotEqual("assertXMLNotEquals test case" + i + " failed",
                control[i], test[i]);
        }
    }

    /**
     *  Test for the assertXMLEquals method.
     */
    public void testXMLEqualsDocuments() throws Exception {
        Document controlDocument, testDocument;
        for(int i=0;i<control.length;i++){
            controlDocument = XMLUnit.buildControlDocument(control[i]);
            assertXMLEqual("assertXMLEquals test case " + i + " failed",
                controlDocument, controlDocument);
            testDocument = XMLUnit.buildTestDocument(test[i]);
            assertXMLNotEqual("assertXMLNotEquals test case" + i + " failed",
                controlDocument, testDocument);
        }
    }

    private static final String xpathValuesControlXML =
            "<root><outer attr=\"urk\"><inner attr=\"urk\">"
            + "controlDocument</inner></outer></root>";
    private static final String xpathValuesTestXML =
            "<root><outer attr=\"urk\"><inner attr=\"ugh\">"
            + "testDocument</inner></outer></root>";

    public void testXpathValuesEqualUsingDocument() throws Exception {
        Document controlDocument = XMLUnit.buildControlDocument(xpathValuesControlXML);
        Document testDocument = XMLUnit.buildTestDocument(xpathValuesTestXML);

        assertXpathValuesEqual("//text()", "//inner/text()", controlDocument);
        assertXpathValuesEqual("//inner/@attr", controlDocument,
            "//outer/@attr", testDocument);

        assertXpathValuesNotEqual("//inner/text()", "//outer/@attr", controlDocument);
        assertXpathValuesNotEqual("//inner/text()", controlDocument,
            "//text()", testDocument);
    }

    public void testXpathValuesEqualUsingString() throws Exception {
        assertXpathValuesEqual("//text()", "//inner/text()", xpathValuesControlXML);
        assertXpathValuesEqual("//inner/@attr", xpathValuesControlXML,
            "//outer/@attr", xpathValuesTestXML);

        assertXpathValuesNotEqual("//inner/text()", "//outer/@attr", xpathValuesControlXML);
        assertXpathValuesNotEqual("//inner/text()", xpathValuesControlXML,
            "//text()", xpathValuesTestXML);
    }

    public void testXpathEvaluatesTo() throws Exception {
        assertXpathEvaluatesTo("urk", "//outer/@attr", xpathValuesControlXML);
        try {
            assertXpathEvaluatesTo("yum", "//inner/@attr", xpathValuesControlXML);
            fail("Expected assertion to fail #1");
        } catch (AssertionFailedError e) {
        }
        assertXpathEvaluatesTo("2", "count(//@attr)", xpathValuesControlXML);

        Document testDocument = XMLUnit.buildTestDocument(xpathValuesTestXML);
        assertXpathEvaluatesTo("ugh", "//inner/@attr", testDocument);
        try {
            assertXpathEvaluatesTo("yeah", "//outer/@attr", testDocument);
            fail("Expected assertion to fail #2");
        } catch (AssertionFailedError e) {
        }

    }

    public void testNodeTest() throws Exception {
        NodeTester tester = new CountingNodeTester(1);
        assertNodeTestPasses(xpathValuesControlXML, tester, Node.TEXT_NODE);
        try {
            assertNodeTestPasses(xpathValuesControlXML, tester, Node.ELEMENT_NODE);
            fail("Expected node test failure #1!");
        } catch (AssertionFailedError e) {
        }

        NodeTest test = new NodeTest(new StringReader(xpathValuesTestXML));
        tester = new CountingNodeTester(4);
        assertNodeTestPasses(test, tester,
            new short[] {Node.TEXT_NODE, Node.ELEMENT_NODE}, true);
        assertNodeTestPasses(test, tester,
            new short[] {Node.TEXT_NODE, Node.COMMENT_NODE}, false);

        try {
            assertNodeTestPasses(test, tester,
            new short[] {Node.TEXT_NODE, Node.ELEMENT_NODE}, false);
            fail("Expected node test failure #2!");
            assertNodeTestPasses(test, tester,
            new short[] {Node.TEXT_NODE, Node.COMMENT_NODE}, true);
            fail("Expected node test failure #3!");
        } catch (AssertionFailedError e) {
        }
    }

    public void testXMLValid() {
        // see test_Validator class
    }

    private static final String TREES_OPEN = "<trees>";
    private static final String TREES_CLOSE = "</trees>";
    private static final String xpathNodesControlXML = TREES_OPEN
            + "<tree evergreen=\"false\">oak</tree>"
            + "<tree evergreen=\"false\">ash</tree>"
            + "<tree evergreen=\"true\">scots pine</tree>"
            + "<tree evergreen=\"true\">spruce</tree>"
            + "<favourite><!-- is this a tree or a bush?! -->"
            + "<tree evergreen=\"false\">magnolia</tree>"
            + "</favourite>"
            + "<fruit>"
            + "<apples><crunchy/><yum/><tree evergreen=\"false\">apple</tree></apples>"
            + "</fruit>"
            + TREES_CLOSE;
    private static final String xpathNodesTestXML = TREES_OPEN
            + "<tree evergreen=\"false\">oak</tree>"
            + "<tree evergreen=\"false\">ash</tree>"
            + "<tree evergreen=\"true\">scots pine</tree>"
            + "<tree evergreen=\"true\">spruce</tree>"
            + "<tree flowering=\"true\">cherry</tree>"
            + "<tree flowering=\"true\">apple</tree>"
            + "<favourite><!-- is this a tree or a bush?! -->"
            + "<tree evergreen=\"false\">magnolia</tree>"
            + "</favourite>"
            + "<apples><crunchy/><yum/><tree evergreen=\"false\">apple</tree></apples>"
            + TREES_CLOSE;

    public void testXpathsEqual() throws Exception {
        Document controlDoc = XMLUnit.buildControlDocument(xpathNodesControlXML);
        Document testDoc = XMLUnit.buildTestDocument(xpathNodesTestXML);

        String[] controlXpath = new String[]{"/trees/tree[@evergreen]",
                "//tree[@evergreen='false']",
                "/trees/favourite",
                "//fruit/apples"};
        String[] testXpath = {controlXpath[0],
                controlXpath[1],
                "//favourite",
                "//apples"};

        // test positive passes
        for (int i=0; i < controlXpath.length; ++i) {
            assertXpathsEqual(controlXpath[i], controlDoc,
                testXpath[i], testDoc);
            assertXpathsEqual(controlXpath[i], xpathNodesControlXML,
                testXpath[i], xpathNodesTestXML);
            assertXpathsEqual(controlXpath[i], testXpath[i], controlDoc);
            assertXpathsEqual(controlXpath[i], testXpath[i], xpathNodesControlXML);
        }
        // test negative fails
        for (int i=0; i < controlXpath.length; ++i) {
            try {
                assertXpathsNotEqual(controlXpath[i], controlDoc,
                    testXpath[i], testDoc);
                fail("should not be notEqual!");
            } catch (AssertionFailedError e) {
            }
            try {
                assertXpathsNotEqual(controlXpath[i], xpathNodesControlXML,
                testXpath[i], xpathNodesTestXML);
                fail("should not be notEqual!");
            } catch (AssertionFailedError e) {
            }
            try {
                assertXpathsNotEqual(controlXpath[i], testXpath[i], controlDoc);
                fail("should not be notEqual!");
            } catch (AssertionFailedError e) {
            }
            try {
                assertXpathsNotEqual(controlXpath[i], testXpath[i], xpathNodesControlXML);
                fail("should not be notEqual!");
            } catch (AssertionFailedError e) {
            }
        }
    }

    public void testXpathsNotEqual() throws Exception {
        Document controlDoc = XMLUnit.buildControlDocument(xpathNodesControlXML);
        Document testDoc = XMLUnit.buildTestDocument(xpathNodesTestXML);

        String[] controlXpath = new String[]{"/trees/tree[@evergreen]",
                "//tree[@evergreen='false']",
                "/trees/favourite",
                "//fruit/apples"};
        String[] testXpath = {"//tree",
                "//tree[@evergreen='true']",
                "//favourite/apples",
                "//apples/tree"};

        // test positive passes
        for (int i=0; i < controlXpath.length; ++i) {
            assertXpathsNotEqual(controlXpath[i], controlDoc,
                testXpath[i], testDoc);
            assertXpathsNotEqual(controlXpath[i], xpathNodesControlXML,
                testXpath[i], xpathNodesTestXML);
            assertXpathsNotEqual(controlXpath[i], testXpath[i], controlDoc);
            assertXpathsNotEqual(controlXpath[i], testXpath[i], xpathNodesControlXML);
        }
        // test negative fails
        for (int i=0; i < controlXpath.length; ++i) {
            try {
                assertXpathsEqual(controlXpath[i], controlDoc,
                    testXpath[i], testDoc);
                fail("should not be Equal!");
            } catch (AssertionFailedError e) {
            }
            try {
                assertXpathsEqual(controlXpath[i], xpathNodesControlXML,
                testXpath[i], xpathNodesTestXML);
                fail("should not be Equal!");
            } catch (AssertionFailedError e) {
            }
            try {
                assertXpathsEqual(controlXpath[i], testXpath[i], controlDoc);
                fail("should not be Equal!");
            } catch (AssertionFailedError e) {
            }
            try {
                assertXpathsEqual(controlXpath[i], testXpath[i], xpathNodesControlXML);
                fail("should not be Equal!");
            } catch (AssertionFailedError e) {
            }
        }
    }
    
    public void testDocumentAssertXpathExists() throws Exception {
        Document controlDoc = XMLUnit.buildControlDocument(xpathNodesControlXML);
        assertXpathExists("/trees/fruit/apples/yum", controlDoc);
        assertXpathExists("//tree[@evergreen='false']", controlDoc);
        try {
            assertXpathExists("//tree[@evergreen='idunno']", controlDoc);
            fail("Xpath does not exist");
        } catch (AssertionFailedError e) {
            // expected
        }
    }
    
    public void testStringAssertXpathExists() throws Exception {
        assertXpathExists("/trees/fruit/apples/yum", xpathNodesControlXML);
        assertXpathExists("//tree[@evergreen='false']", xpathNodesControlXML);
        try {
            assertXpathExists("//tree[@evergreen='idunno']", xpathNodesControlXML);
            fail("Xpath does not exist");
        } catch (AssertionFailedError e) {
            // expected
        }
    }

    public void testDocumentAssertNotXpathExists() throws Exception {
        Document controlDoc = XMLUnit.buildControlDocument(xpathNodesControlXML);
        assertXpathNotExists("//tree[@evergreen='idunno']", controlDoc);
        try {
            assertXpathNotExists("/trees/fruit/apples/yum", controlDoc);
            fail("Xpath does exist, once");
        } catch (AssertionFailedError e) {
            // expected
        }
        try {
            assertXpathNotExists("//tree[@evergreen='false']", controlDoc);
            fail("Xpath does exist many times");
        } catch (AssertionFailedError e) {
            // expected
        }
    }
    
    public void testStringAssertNotXpathExists() throws Exception {
        assertXpathNotExists("//tree[@evergreen='idunno']", xpathNodesControlXML);
        try {
            assertXpathNotExists("/trees/fruit/apples/yum", xpathNodesControlXML);
            fail("Xpath does exist, once");
        } catch (AssertionFailedError e) {
            // expected
        }
        try {
            assertXpathNotExists("//tree[@evergreen='false']", xpathNodesControlXML);
            fail("Xpath does exist many times");
        } catch (AssertionFailedError e) {
            // expected
        }
    }

    public test_XMLTestCase(String name) {
        super(name);
    }

    /**
     * returns the TestSuite containing this test
     */
    public static TestSuite suite(){
        return new TestSuite(test_XMLTestCase.class);
    }
}
