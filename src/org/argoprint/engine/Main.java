package org.argoprint.engine;
import org.argoprint.engine.interpreters.*;
import org.argoprint.uml_interface.UMLInterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
// import org.argoprint.ui.Settings;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.*;

public class Main {
	private Interpreter _firstHandler;
	private DOMParser _parser;
	// TODO remove 
	private String _outputFile;
	private UMLInterface _umlInterface;

	public Main() {
		_parser = new DOMParser(); 
		_umlInterface = new UMLInterface();
		// Initialize the CoR
		_firstHandler = new InterpreterDefault(_umlInterface);
		_firstHandler.setFirstHandler(_firstHandler);
	}

	// TODO change parameter to Settings when that class is finished
	public void initializeSystem(String template, String outputFile, String outputDir) 
	throws SAXException, IOException {
		_outputFile = outputFile;
		// TODO set outputDir of interface
		_parser.parse(template);
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