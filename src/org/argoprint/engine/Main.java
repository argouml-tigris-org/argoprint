//$Id$
// Copyright (c) 2005, Linus Tolke
//Copyright (c) 2003-2004, Mikael Albertsson, Mattias Danielsson, Per Engström, 
//Fredrik Gröndahl, Martin Gyllensten, Anna Kent, Anders Olsson, 
//Mattias Sidebäck.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without 
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//  this list of conditions and the following disclaimer.
// 
//* Redistributions in binary form must reproduce the above copyright 
//  notice, this list of conditions and the following disclaimer in the 
//  documentation and/or other materials provided with the distribution.
//
//* Neither the name of the University of Linköping nor the names of its 
//  contributors may be used to endorse or promote products derived from 
//  this software without specific prior written permission. 
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.

package org.argoprint.engine;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argoprint.engine.interpreters.BadTemplateException;
import org.argoprint.engine.interpreters.Interpreter;
import org.argoprint.engine.interpreters.InterpreterBind;
import org.argoprint.engine.interpreters.InterpreterCall;
import org.argoprint.engine.interpreters.InterpreterDefault;
import org.argoprint.engine.interpreters.InterpreterIf;
import org.argoprint.engine.interpreters.InterpreterIterate;
import org.argoprint.ui.Settings;
import org.argoprint.uml_interface.UMLInterface;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The main processor for ArgoPrint.
 */
public class Main {
    private Interpreter _firstHandler;
    private DOMParser _parser;
    // TODO: remove 
    private String _outputFile;
    private ArgoPrintDataSource _dataSource;
    
    /**
     * Constructor with a dummy data source.
     */
    public Main() {
        this(new UMLInterface());
    }

    /**
     * Constructor with a given data source.
     * 
     * @param source The data source.
     */
    public Main(ArgoPrintDataSource source) {
	_parser = new DOMParser();
        _dataSource = source;

	Interpreter iCall = new InterpreterCall(_dataSource);
	_firstHandler = iCall;
	iCall.setFirstHandler(_firstHandler);
	    
	Interpreter iDefault = new InterpreterDefault(_dataSource, _firstHandler);
	Interpreter iIterate = new InterpreterIterate(_dataSource, _firstHandler);
	Interpreter iBind = new InterpreterBind(_dataSource, _firstHandler);
	Interpreter iIf = new InterpreterIf(_dataSource, _firstHandler);
    
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

	((UMLInterface) _dataSource).initialize();
	_outputFile = settings.getOutputFile();
	// TODO: set outputDir of interface
	// ((UMLInterface)dataSource).setOutputPath(settings.getOutputDir);
	_parser.parse(new InputSource(settings.getTemplate()));
    }

    /**
     * Does the actual work.
     * 
     * @throws IOException If sometihing goes wrong while reading or writing 
     *         the files.
     * @throws BadTemplateException if the input file is incorrect.
     * @throws UnsupportedCallException if the data source calls are incorrect.
     */
    public void go()
	throws IOException, BadTemplateException, UnsupportedCallException {
	Document document = _parser.getDocument();
	_firstHandler.handleTag(document, new Environment());
	FileOutputStream outputStream = new FileOutputStream(_outputFile);
	OutputFormat outputFormat = new OutputFormat(document);
	outputFormat.setIndent(4);
	outputFormat.setPreserveSpace(false);
	outputFormat.setLineWidth(80);
	XMLSerializer serializer =
	    new XMLSerializer(outputStream, outputFormat);
	serializer.serialize(document);
    }
}

 
