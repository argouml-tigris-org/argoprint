// $Id$
// Copyright (c) 2003-2005, Linus Tolke, Mikael Albertsson, Mattias Danielsson,
// Per Engström, Fredrik Gröndahl, Martin Gyllensten, Anna Kent, Anders Olsson,
// Mattias Sidebäck.
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

package org.argoprint.engine;

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
     *
     * @return The suite.
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
