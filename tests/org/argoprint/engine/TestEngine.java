package org.argoprint.engine;

import org.argoprint.ui.Settings;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.ConsoleAppender;

public class TestEngine extends TestCase {

    private Main _main = null;


    /** Specifies the test suite.
     */
    public static Test suite() {
        return null;
    }

    /** Tests the Main.initializeSystem()-method.
     *  @See org.argoprint.Engine.Main#initializeSystem()
     */
    public void testInitializeSystem() throws Exception {
        Settings s = new Settings();
        s.setOutputDir("/tmp/");
        s.setTemplate("TestEngine.xml");
        s.setOutputFile("/tmp/EngineTestResult");
        _main.initializeSystem(s);
    }

    public void testGoWithoutInitialize() {
        boolean caught_exception = false;
        try {
            _main.go();
        } catch (Exception e) {
            // Everything went according to plans.
            caught_exception = true;
        }

        if (!caught_exception) {
            fail("Go-method did not throw an exception!");
        }
    }

    /** @See junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        _main = new Main(false);
        assertNotNull("Could not create a Main object.", _main);
    }
}
