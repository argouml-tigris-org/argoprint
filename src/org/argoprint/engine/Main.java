package org.argoprint.engine;
import org.apache.xerces.parsers.DOMParser;

class Main {
	private DOMParser _parser;
	public Main() {
	}
	public void processTemplate(String template, String outputFile, String outputDir) {
		try {
		_parser.parse(template);
		}
		catch (Exception e) {
			System.out.println("Error in parsing template: " + e.getMessage());
		}
	}
}