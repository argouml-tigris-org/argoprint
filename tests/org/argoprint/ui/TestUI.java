package org.argoprint.ui;

import java.io.FileNotFoundException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Testing the User Interface.
 */
public class TestUI extends TestCase {
    private Settings settings = null;

    public TestUI(String param) {
        super(param);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestUI("setSettingsTest"));
        return new TestSuite(TestUI.class);
    }

    public void testSettingsCorrectOutputDir() {
        String outputDir = "./";

        settings.setOutputDir(outputDir);
        assertEquals(settings.getOutputDir(), outputDir);
    }

    public void testSettingsCorrectOutputFile() {
        String outputFile = "settings_destination.xml";

        settings.setOutputFile(outputFile);
        assertEquals(settings.getOutputFile(), outputFile);
    }


    public void testSettingsIncorrectOutputDir() {
        String outputDir = "incorrect_dir/";
        boolean exceptionCaught = false;

        settings.setOutputDir(outputDir);
        try {
            settings.checkCorrectness();
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("No exception thrown by setOutputDir.");
        }
    }

    public void testSettingsIncorrectTemplate() {
        String template = "nonexistant_template.xml";
        boolean exceptionCaught = false;

        try {
            settings.setTemplate(template);
            settings.checkCorrectness();
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("No exception thrown by setTemplate.");
        }
    }

    public void testSettingsIncorrectTemplateOutputFile() {
        String outputFile = "directory/that/does/not/exist/output.xml";
        boolean exceptionCaught = false;

        settings.setOutputFile(outputFile);
        try {
            settings.checkCorrectness();
        } catch (Exception e) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            fail("No exception thrown by setOutputFile.");
        }
    }

    /**
     * @see junit.framework.TestCase#setUp()
     *
     * @throws FileNotFoundException if the test case is incorrect.
     */
    protected void setUp() throws FileNotFoundException {
        settings =
            new Settings("tests/TestEngine.xml",
                	 "tests/EngineTestResult", "tests/");
    }
};
