//$Id$
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

package org.argoprint.ui;


import java.io.*;

/**
 * Stores and manipulates settings when ArgoPrint is running.
 * Todo: Add control to se if path-/filenames are correct. Almost done!
 * Todo: Add control in the setters! 
 *
 * @author matda701, Mattias Danielsson
 */
public class Settings {
    
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
     * The the template to be processed by Argoprint
     */
    private File _template;
    
    //private String _inputLang;
    //private String _model;
    
    /**
     * Constructor.
     */
    public Settings() { }

    /**
     * Constructor. Sets attributes to corresponding argument.
     */
    public Settings(String template, String file, String dir) 
	throws Exception {
	_template = new File(template);
	_outputFile = file;
	_outputDir = dir;
	
	try {
	    checkCorrectness();
	} catch (Exception e) {
	    throw e;
	}
    } 


    /**
     * Function to check if member attributes, ie path-/filenames are valid.
     * throws Exceptions if incorrect values or r/w-rights is not correct.
     */
    private void checkCorrectness() throws Exception {

	if (!_template.exists()) {
	    throw new Exception("Template file not found");
	}   
	if (!_template.canRead()) {
	    throw new Exception("Can't read template file");
	}

	File outputFile = new File(_outputFile);
	if (outputFile.exists()) {
	    if (!outputFile.canWrite()) {
		throw new Exception("Can't write to output file: "
				    + outputFile);
	    }
	}

	File outputDir = new File(_outputDir);
	if (!outputDir.isDirectory()) {
	    throw new Exception("Not a valid output directory");
	}
    } 

    /**
     * Setter for outputDir
     */
    public void setOutputDir(String dir) {
	_outputDir = new String(dir);
    }

    /**
     * Setter for outputFile
     */
    public void setOutputFile(String file) {
	_outputFile = new String(file);
    }
    
    /**
     * Getter for outputDir
     */
    public String getOutputDir() {
	return _outputDir;
    }

    /**
     * Getter for outputFile
     */
    public String getOutputFile() {
	return _outputFile;
    }

    /**
     * Getter for template
     */
    public FileInputStream getTemplate() {
	try {
	    return new FileInputStream(_template);
	}
	catch (FileNotFoundException e) {
	    throw new RuntimeException("File not found "
				       + _template + " shouldn't happen!");
	}
    }
}

