package org.argoprint.engine;

import junit.framework.*;
import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.*;

import org.argoprint.ui.*;
import org.argoprint.engine.interpreters.*;
import org.argoprint.engine.Environment;
import org.argoprint.*;

import java.io.*;
import java.lang.*;
import java.util.*;

public class IntegrationTest extends TestCase {
    private Main _main = null;
    private Settings _settings = null;
    private ArgoPrintDataSource _uml_interface = null;

    private final String t_dir = "IntegrationTestTemplates/";

    public IntegrationTest(String s) {
        super(s);
    }

    public static Test suite() {
        // Code calling the tests.
        return new TestSuite(IntegrationTest.class);
    }

    private void performIntegrationTest(String name) throws Exception {
        _settings = new Settings(t_dir+name+".xml",
                                 t_dir+name+"Result.xml",
                                 t_dir);

        _main.initializeSystem(_settings);
        try {
            _main.go();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        assertTrue(name+"-test did not generate expected output.",
                    filesEqual(_settings.getOutputFile(),
                               t_dir+name+"Expected.xml"));
    }

    public void testCorrectBind() throws Exception {
        performIntegrationTest("CorrectBind");
    }

    public void testCorrectBindTwoChildren() throws Exception {
        performIntegrationTest("CorrectBindTwoChildren");
    }

    public void testCorrectBind22Children() throws Exception {
        performIntegrationTest("CorrectBind22Children");
    }

    public void testCorrectCall() throws Exception {
        performIntegrationTest("CorrectCall");
    }

    public void testCorrectDefault() throws Exception {
        performIntegrationTest("CorrectDefault");
    }

    public void testCorrectIf() throws Exception {
        performIntegrationTest("CorrectIf");
    }

    public void testCorrectIterate() throws Exception {
        performIntegrationTest("CorrectIterate");
    }

    public void testCorrectIterateSortvalue() throws Exception {
        performIntegrationTest("CorrectIterateSortvalue");
    }

    public void testIncorrectBind() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectBind");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectBindNoAttrName() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectBindNoAttrName");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectBindNoName() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectBindNoName");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectCall() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectCall");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectIf() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectIf");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectIfSubtags() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectIfSubtags");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectIteratorIterate() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectIteratorIterate");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectMethodIterate() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectMethodIterate");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }

    public void testIncorrectSortvalueIterate() throws Exception {
        boolean exception_caught = false;
        try {
            performIntegrationTest("IncorrectSortvalueIterate");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exception_caught = true;
        }

        assertTrue("No expection thrown!", exception_caught);
    }



    protected void setUp() throws Exception {
        _uml_interface = new DataSourceStub();
        assertNotNull(_uml_interface);

        _main = new Main(_uml_interface);
    }

    private boolean filesEqual(String left, String right) throws Exception {
        DOMParser rparser = new DOMParser();
        DOMParser lparser = new DOMParser();
        lparser.parse(left);
        rparser.parse(right);

        ByteArrayOutputStream lstream = new ByteArrayOutputStream();
        ByteArrayOutputStream rstream = new ByteArrayOutputStream();
        OutputFormat of = new OutputFormat();
        of.setIndent(4);
        of.setPreserveSpace(false);
        of.setLineWidth(80);
        (new XMLSerializer(lstream, of)).serialize(lparser.getDocument());
        (new XMLSerializer(rstream, of)).serialize(rparser.getDocument());

        String lstring = strip_ws(lstream.toString());
        String rstring = strip_ws(rstream.toString());

        return lstring.equals(rstring);
    }

    private int findNode(NodeList nodes, String name, int start) {
        for (int i = start; i < nodes.getLength(); ++i) {
            if (nodes.item(i).getNodeName().equals(name)) {
                return i;
            }
        }
        return -1;
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
}
