//$Id$
//Copyright (c) 2003, Mikael Albertsson, Mattias Danielsson, Per Engström, 
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
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.interpreters.*;

import org.apache.log4j.Logger;

import org.argoprint.ui.Settings;
import org.argoprint.uml_interface.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.*;

import org.argoprint.DataSourceStub;

public class Main {
    private Interpreter _firstHandler;
    private DOMParser _parser;
    // TODO: remove 
    private String _outputFile;
    private ArgoPrintDataSource _dataSource;
    private Logger _log;

    public Main() {
//    public Main(boolean initDataSource) {
	_parser = new DOMParser(); 
	_dataSource = new UMLInterface(); 
	//_dataSource = new DataSourceStub();



	// Initialize the CoR
	Interpreter iDefault = new InterpreterDefault(_dataSource);
	Interpreter iCall = new InterpreterCall(_dataSource);
	Interpreter iIterate = new InterpreterIterate(_dataSource);
	Interpreter iBind = new InterpreterBind(_dataSource);
	Interpreter iIf = new InterpreterIf(_dataSource);

	_firstHandler = iCall;

	iCall.setNextHandler(iIterate);
	iCall.setFirstHandler(_firstHandler);
	iIterate.setNextHandler(iBind);
	iIterate.setFirstHandler(_firstHandler);
	iBind.setNextHandler(iIf);
	iBind.setFirstHandler(_firstHandler);
	iIf.setNextHandler(iDefault);
	iIf.setFirstHandler(_firstHandler);
	iDefault.setFirstHandler(_firstHandler);
    }

    public Main(boolean initDataSource) {
//    public Main(boolean initDataSource) {
	_parser = new DOMParser(); 
	_dataSource = new UMLInterface(); 
	//_dataSource = new DataSourceStub();

	// Initialize the CoR
	Interpreter iDefault = new InterpreterDefault(_dataSource);
	Interpreter iCall = new InterpreterCall(_dataSource);
	Interpreter iIterate = new InterpreterIterate(_dataSource);
	Interpreter iBind = new InterpreterBind(_dataSource);
	Interpreter iIf = new InterpreterIf(_dataSource);

	_firstHandler = iCall;

	iCall.setNextHandler(iIterate);
	iCall.setFirstHandler(_firstHandler);
	iIterate.setNextHandler(iBind);
	iIterate.setFirstHandler(_firstHandler);
	iBind.setNextHandler(iIf);
	iBind.setFirstHandler(_firstHandler);
	iIf.setNextHandler(iDefault);
	iIf.setFirstHandler(_firstHandler);
	iDefault.setFirstHandler(_firstHandler);
    }


    // TODO: change parameter to Settings when that class is finished
    public void initializeSystem(String template, 
				 String outputFile, 
				 String outputDir) 
	throws SAXException, IOException {
	
	_outputFile = outputFile;
	// TODO: set outputDir of interface
	_parser.parse(template);
    }
    
    public void initializeSystem(Settings settings, Logger log) 
	throws SAXException, IOException{

	((UMLInterface)_dataSource).initialize(log);
	_log = log;
	_outputFile = settings.getOutputFile();
	// TODO: set outputDir of interface
	// ((UMLInterface)_dataSource).setOutputPath(settings.getOutputDir);
	_parser.parse(settings.getTemplate());
    }

    public void initializeSystem(Settings settings) 
	throws SAXException, IOException{

	_outputFile = settings.getOutputFile();
	// TODO: set outputDir of interface
	// ((UMLInterface)_dataSource).setOutputPath(settings.getOutputDir);
	_parser.parse(settings.getTemplate());
    }
    
    public void go()
	throws FileNotFoundException, IOException, Exception {
	Document document = _parser.getDocument();
	_firstHandler.handleTag(document, new Environment());
	FileOutputStream outputStream = new FileOutputStream(_outputFile);
	OutputFormat outputFormat = new OutputFormat(document);
	outputFormat.setIndent(4);
	outputFormat.setPreserveSpace(false);
	outputFormat.setLineWidth(80);
	XMLSerializer serializer = new XMLSerializer(outputStream, outputFormat);
	serializer.serialize(document);
    }
}

 
