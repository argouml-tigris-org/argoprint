package org.argoprint.engine;
import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.interpreters.*;

import org.apache.log4j.Logger;

import org.argoprint.ui.Settings;
import org.argoprint.uml_interface.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
// import org.argoprint.ui.Settings;
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
	_parser = new DOMParser(); 
	_dataSource = new UMLInterface(); //new DataSourceStub();

	
	
	// Initialize the CoR
	Interpreter interpreterDefault = new InterpreterDefault(_dataSource);
	Interpreter interpreterCall = new InterpreterCall(_dataSource);
	Interpreter interpreterIterate = new InterpreterIterate(_dataSource);
	_firstHandler = interpreterCall;
	interpreterCall.setNextHandler(interpreterIterate);
	interpreterCall.setFirstHandler(interpreterCall);
	interpreterIterate.setNextHandler(interpreterDefault);
	interpreterIterate.setFirstHandler(interpreterCall);
	interpreterDefault.setFirstHandler(interpreterCall);
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

    
    public void go()
	throws FileNotFoundException, IOException, Exception {
	Document document = _parser.getDocument();
	_firstHandler.handleTag(document, new Environment());
		
	FileOutputStream outputStream = new FileOutputStream(_outputFile);
	XMLSerializer serializer = new XMLSerializer(outputStream, null);
	// Currently only outputs the input document
	serializer.serialize(document);
    }
}

 
