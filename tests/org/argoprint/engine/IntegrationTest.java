package org.argoprint.engine;

import java.io.ByteArrayOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.argoprint.DataSourceStub;
import org.argoprint.ui.Settings;

public class IntegrationTest extends TestCase {
    private Main main = null;
    private Settings settings = null;

    private static final String T_DIR = "tests/IntegrationTestTemplates/";

    public IntegrationTest(String s) {
        super(s);
    }

    public static Test suite() {
        // Code calling the tests.
        return new TestSuite(IntegrationTest.class);
    }

    private void performIntegrationTest(String name) throws Exception {
        settings =
            new Settings(T_DIR + name + ".xml",
                         T_DIR + name + "Result.xml",
                         T_DIR);

        main.initializeSystem(settings);
        try {
            main.go();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        assertTrue(name + "-test did not generate expected output.",
                    filesEqual(settings.getOutputFile(),
                               T_DIR + name + "Expected.xml"));
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
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectBind");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectBindNoAttrName() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectBindNoAttrName");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectBindNoName() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectBindNoName");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectCall() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectCall");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectIf() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectIf");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectIfSubtags() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectIfSubtags");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectIteratorIterate() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectIteratorIterate");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectMethodIterate() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectMethodIterate");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }

    public void testIncorrectSortvalueIterate() throws Exception {
        boolean exceptionCaught = false;
        try {
            performIntegrationTest("IncorrectSortvalueIterate");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("No expection thrown!", exceptionCaught);
    }



    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        main = new Main(new DataSourceStub());
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

        String lstring = stripWs(lstream.toString());
        String rstring = stripWs(rstream.toString());

        return lstring.equals(rstring);
    }

    private String stripWs(String in) {
        StringBuffer buf = new StringBuffer(in);
        for (int i = buf.length() - 1; i >= 0; --i) {
            char c = buf.charAt(i);
            if (c == ' ' || c == '\n' || c == '\t') {
                buf.deleteCharAt(i);
            }
        }
        return buf.toString();
    }
}
