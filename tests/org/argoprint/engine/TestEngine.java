package org.argoprint.engine;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.argoprint.DataSourceStub;
import org.argoprint.ui.Settings;
import org.xml.sax.SAXException;

public class TestEngine extends TestCase {

    private Main main = null;

    /**
     * Specifies the test suite.
     */
    public static Test suite() {
        return new TestSuite(TestEngine.class);
    }

    /**
     * Tests the Main.initializeSystem()-method.
     *
     * @see org.argoprint.engine.Main#initializeSystem(Settings)
     * @throws IOException if we cannot open the file or parse the file.
     * @throws SAXException If we cannot parse the file.
     */
    public void testInitializeSystem() throws SAXException, IOException {
        Settings s =
            new Settings("tests/TestEngine.xml",
                         "tests/EngineTestResult", "tests/");
        main.initializeSystem(s);
    }

    public void testGoWithoutInitialize() throws Exception {
        boolean exceptionCaught = false;
        try {
            main.go();
        } catch (Exception e) {
            // Everything went according to plans.
            exceptionCaught = true;
        }

        if (!exceptionCaught) {
            fail("Go-method did not throw an exception!");
        }

    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        main = new Main(new DataSourceStub());
    }
}
