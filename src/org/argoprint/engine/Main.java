// $Id$
// Copyright (c) 2003-2004, Mikael Albertsson, Mattias Danielsson, Per Engström,
// Fredrik Gröndahl, Martin Gyllensten, Anna Kent, Anders Olsson,
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

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argoprint.engine.interpreters.BadTemplateException;
import org.argoprint.engine.interpreters.Interpreter;
import org.argoprint.engine.interpreters.InterpreterBind;
import org.argoprint.engine.interpreters.InterpreterCall;
import org.argoprint.engine.interpreters.InterpreterDefault;
import org.argoprint.engine.interpreters.InterpreterIf;
import org.argoprint.engine.interpreters.InterpreterIterate;
import org.argoprint.interfaces.UMLInterface;
import org.argoprint.ui.Settings;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The main processor for ArgoPrint.
 */
public class Main {
    private Interpreter firstHandler;
    private DocumentBuilder parser;
    // TODO: remove
    private String outputFile;
    private ArgoPrintDataSource dataSource;

    /**
     * The document that we have parsed using.
     */
    private Document parsedDocument;

    /**
     * Constructor with a dummy data source.
     *
     * @throws ParserConfigurationException if we cannot build the parser.
     */
    public Main() throws ParserConfigurationException {
        this(new UMLInterface());
    }

    /**
     * Constructor with a given data source.
     *
     * @param source The data source.
     * @throws ParserConfigurationException if we cannot create the builder.
     */
    public Main(ArgoPrintDataSource source)
    	throws ParserConfigurationException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
	parser = dbf.newDocumentBuilder();
        dataSource = source;

	Interpreter iCall = new InterpreterCall(dataSource);
	firstHandler = iCall;
	iCall.setFirstHandler(firstHandler);

	Interpreter iDefault =
	    new InterpreterDefault(dataSource, firstHandler);
	Interpreter iIterate =
	    new InterpreterIterate(dataSource, firstHandler);
	Interpreter iBind = new InterpreterBind(dataSource, firstHandler);
	Interpreter iIf = new InterpreterIf(dataSource, firstHandler);

	iCall.setNextHandler(iIterate);
    	iIterate.setNextHandler(iBind);
    	iBind.setNextHandler(iIf);
    	iIf.setNextHandler(iDefault);
    }

    /**
     * Initializes ArgoPrint.
     *
     * @param settings The settings.
     * @throws SAXException If we cannot parse the template.
     * @throws IOException If something goes wrong while reading or writing
     *         the files.
     */
    public void initializeSystem(Settings settings)
	throws SAXException, IOException {

	dataSource.initialize();
	outputFile = settings.getOutputFile();
	// TODO: set outputDir of interface
	// ((UMLInterface)dataSource).setOutputPath(settings.getOutputDir);
	parsedDocument = parser.parse(new InputSource(settings.getTemplate()));
    }

    /**
     * Does the actual work.
     *
     * @throws IOException If something goes wrong while reading or writing
     *         the files.
     * @throws BadTemplateException if the input file is incorrect.
     * @throws UnsupportedCallException if the data source calls are incorrect.
     */
    public void go()
	throws IOException, BadTemplateException, UnsupportedCallException {
	Document document = parsedDocument;
	firstHandler.handleTag(document, new Environment());
	FileOutputStream outputStream = new FileOutputStream(outputFile);

	TransformerFactory tf = TransformerFactory.newInstance();
	Transformer serializer;
	try {
	    serializer = tf.newTransformer();
	} catch (TransformerConfigurationException e) {
	    throw new IOException("The TransformerFactory cannot initiate.");
	}
	serializer.setOutputProperty(OutputKeys.INDENT, "yes");

	try {
	    serializer.transform(new DOMSource(document),
	            		 new StreamResult(outputStream));
	} catch (TransformerException e) {
	    throw new Error("Shouldn't happen.", e);
	}

	outputStream.close();

//	OutputFormat outputFormat = new OutputFormat(document);
//	outputFormat.setIndent(4);
//	outputFormat.setPreserveSpace(false);
//	outputFormat.setLineWidth(80);
//	XMLSerializer serializer =
//	    new XMLSerializer(outputStream, outputFormat);
//	serializer.serialize(document);
    }
}
