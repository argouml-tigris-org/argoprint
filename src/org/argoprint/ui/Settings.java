package org.argoprint.ui;

import java.lang.*;

/**
 * Stores and manipulates settings when ArgoPrint is running.
 * Todo: Add control to se if path-/filenames are correct.
 *
 * @author matda701, Mattias Danielsson
 */
public class Settings{
    
    /**
     * The directory where files (diagrams) created by ArgoPrint
     * is to be stored.
     */
    private String _outputDir;
 
    /**
     * The name of the output XML-file
     */
    private String _outputFile;
    
    /**
     * The path to the template to be processed by Argoprint
     */
    private String _template;
    
    //private String _inputLang;
    //private String _model;
    
    /**
     * Constructor.
     */
    public Settings(){}

    /**
     * Constructor. Sets attributes to corresponding argument.
     */
    public Settings(String template, String file, String dir){
	_outputDir = new String(dir);
	_outputFile = new String(file);
	_template = new String(template);
    }
    
    /**
     * Setter for outputDir
     */
    public void setOutputDir(String dir){
	_outputDir = new String(dir);
    }

    /**
     * Setter for outputFile
     */
    public void setOutputFile(String file){
	_outputFile = new String(file);
    }
    
    /**
     * Setter for the path template 
     */
    public void setTemplete(String template){
	_template = new String(template);
    }

    /**
     * Getter for outputDir
     */
    public String getOutputDir(){
	return _outputDir;
    }

    /**
     * Getter for outputFile
     */
    public String getOutputFile(){
	return _outputFile;
    }

    /**
     * Getter for template
     */
    public String getTemplate(){
	return _template;
    }
}

