package org.argoprint.engine;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.*;

class Main {
	
	public Main() {
	}
	
	public void processTemplate(String template, String outputFile, String outputDir) 
	throws SAXException, IOException, FileNotFoundException {
		DOMParser parser = new DOMParser(); 
		parser.parse(template);
		Document doc = parser.getDocument();
		
		// TODO - initialize the CoR and send it the document
		
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		XMLSerializer serializer = new XMLSerializer(outputStream, null);
		// Currently only outputs the input document
		serializer.serialize(doc);
	}
}