package org.argoprint.engine;

import org.argoprint.ui.Settings;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestEngine extends TestCase {

    private Main _main = null;


    /** Specifies the test suite.
     */
    public static Test suite() {
        
    }

    /** Tests the Main.initializeSystem()-method.
     *  @See org.argoprint.Engine.Main#initializeSystem()
     */
    public void testInitializeSystem() {
        Settings s = new Settings();
        s.setDir("/tmp/");
        s.setTemplate("EngineTest.xml");
        s.setFile("/tmp/EngineTestResult");
        assertTrue(_main.initializeSystem(s));
    }

    public void testGoWithoutInitialize() {
        boolean caugth_exception = false;
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
    protected void setUp() {
        super.setUp();
        _main = Main();
        assertNotNull("Could not create a Main object.", _main);
    }
}
