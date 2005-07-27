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
import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
     */
    public static String fileToString(String filename) {
        Source source = new StreamSource(new File(filename));

        return sourceToString(source);
    }


    /**
     * @param source A Source with XML.
     * @return The String generated from the source.
     */
    private static String sourceToString(Source source) {
        TransformerFactory transformerFactory =
            TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new Error("Incorrectly configured transformer.", e);
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        ByteArrayOutputStream rstream = new ByteArrayOutputStream();
        Result result = new StreamResult(rstream);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new Error("Shouldn't happen: Identitity transformer fails.",
                    	    e);
        }

        return stripWs(rstream.toString());
    }

    /**
     * Compare two {@link Node}s.
     *
     * @param mess The string message to give when an error is seen.
     * @param left The correct Node.
     * @param right The Node to test.
     */
    public static void assertNodesEqual(String mess, Node left, Node right) {
        assertEquals(mess + " Type differs.",
                left.getNodeType(), right.getNodeType());
        assertEquals(mess + " Name differs.",
                left.getNodeName(), right.getNodeName());
        assertEquals(mess + " Not both have attributes.",
                left.getAttributes() == null,
                right.getAttributes() == null);

        assertEquals(mess + " Not both have values.",
                left.getNodeValue() == null,
                right.getNodeValue() == null);

        if (left.getNodeValue() == null) {
            assertEquals(mess + " Values.",
                    left.getNodeValue(),
                    right.getNodeValue());
        }

        if (left.getAttributes() != null) {
            NamedNodeMap lmap = left.getAttributes();
            NamedNodeMap rmap = right.getAttributes();

            for (int i = 0; i < lmap.getLength(); i++) {
                assertNodesEqual(mess + " Attribute(" + i + ")",
                        lmap.item(i),
                        rmap.item(i));
            }
        }

        NodeList lchildren = left.getChildNodes();
        NodeList rchildren = right.getChildNodes();

        for (int i = 0; i < lchildren.getLength(); i++) {
            assertNodesEqual(mess + " Child(" + i + ")",
                    lchildren.item(i),
                    rchildren.item(i));
        }
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
        final String xlf = "&#xa;";
        while ((pos = buf.indexOf(xlf)) > -1) {
            buf.replace(pos, pos + xlf.length(), "\n");
        }

        final String dlf = "&#10;";
        while ((pos = buf.indexOf(dlf)) > -1) {
            buf.replace(pos, pos + dlf.length(), "\n");
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

