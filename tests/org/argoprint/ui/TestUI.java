package org.argoprint.ui;

import org.argoprint.ui.*;
import junit.framework.*;

public class TestUI extends TestCase {
    private Settings _settings = null;

    public TestUI(String param) {
        super(param);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestUI("setSettingsTest"));
        return new TestSuite(TestUI.class);
    }

    public void testSettingsCorrectOutputDir() {
        String output_dir = "./";

        _settings.setOutputDir(output_dir);
        assertEquals(_settings.getOutputDir(), output_dir);
    }

    public void testSettingsCorrectTemplate() {
        String template = "settings_template.xml";

        _settings.setTemplate(template);
        assertEquals(_settings.getTemplate(), template);
    }

    public void testSettingsCorrectOutputFile() {
        String output_file = "settings_destination.xml";

        _settings.setOutputFile(output_file);
        assertEquals(_settings.getOutputFile(), output_file);
    }


    public void testSettingsIncorrectOutputDir() {
        String output_dir = "incorrect_dir/";
        boolean exception_caught = false;

        try {
            _settings.setOutputDir(output_dir);
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("No exception thrown by setOutputDir.");
        }
    }

    public void testSettingsIncorrectTemplate() {
        String template = "nonexistant_template.xml";
        boolean exception_caught = false;
        try {
            _settings.setTemplate(template);
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("No exception thrown by setTemplate.");
        }
    }

    public void testSettingsIncorrectTemplateOutputFile() {
        String output_file = "directory/that/does/not/exist/output.xml";
        boolean exception_caught = false;
        try {
            _settings.setOutputFile(output_file);
        } catch (Exception e) {
            exception_caught = true;
        }
        if (!exception_caught) {
            fail("No exception thrown by setOutputFile.");
        }
    }

    protected void setUp() {
        _settings = new Settings();
        assertNotNull("Could not create settings object.", _settings);
    }
};
