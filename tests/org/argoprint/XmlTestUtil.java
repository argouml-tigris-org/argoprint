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


package org.argoprint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Collected some utility methods that are common to several test cases.
 */
public final class XmlTestUtil extends TestCase {

    /**
     * Constructor.
     */
    private XmlTestUtil() {
    }

    /**
     * Converts an XML file to a String (for comparison).
     *
     * @param filename The filename to read.
     * @return The contents of the file as a String.
     * @throws SAXException If the file is not correct XML.
     * @throws IOException If we cannot open the file.
     */
    public static String fileToString(String filename)
    	throws SAXException, IOException {
        OutputFormat of = new OutputFormat();
        of.setIndent(4);
        of.setPreserveSpace(false);
        of.setLineWidth(80);

        DOMParser rparser = new DOMParser();
        rparser.parse(filename);
        ByteArrayOutputStream rstream = new ByteArrayOutputStream();
        (new XMLSerializer(rstream, of)).serialize(rparser.getDocument());

        return stripWs(rstream.toString());
    }


    /**
     * Compare two {@link Node}s.
     *
     * @param left The correct Node.
     * @param right The Node to test.
     * @return <code>true</code> if they are similar.
     */
    public static boolean nodesEqual(Node left, Node right) {
        ByteArrayOutputStream lstream = new ByteArrayOutputStream();
        ByteArrayOutputStream rstream = new ByteArrayOutputStream();
        OutputFormat of = new OutputFormat();
        of.setIndent(4);
        of.setPreserveSpace(false);
        of.setLineWidth(80);
        try {
            (new XMLSerializer(lstream, of)).serialize((Element) left);
            (new XMLSerializer(rstream, of)).serialize((Element) right);
        } catch (IOException e) {
            fail("Could not serialize. Shouldn't happen:" + e);
        }

        String lstring = stripWs(lstream.toString());
        String rstring = stripWs(rstream.toString());

        return lstring.equals(rstring);
    }

    /**
     * Remove whitespace.
     *
     * @param in The String to clean.
     * @return A newly created string without the whitespace.
     */
    private static String stripWs(String in) {
        StringBuffer buf = new StringBuffer(in);

        int pos;
        while ((pos = buf.indexOf("&#xa;")) > -1) {
            buf.replace(pos, pos + 5, "\n");
        }

        for (int i = buf.length() - 1; i >= 0; --i) {
            char c = buf.charAt(i);
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                buf.deleteCharAt(i);
                continue;
            }
        }
        return buf.toString();
    }
}

