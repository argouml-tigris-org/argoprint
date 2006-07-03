// $Id$
// Copyright (c) 2006 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argoprint;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.persistence.PersistenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ArgoPrintManagerModel {
    private HashMap<String, TemplateJob> jobs;

    public class TemplateJob {
	private boolean selected;
	private String identifier;
	private URL template, output;

	private HashMap<String, String> parameters;

	public TemplateJob(String identifier) {
	    this(identifier, null, null, false);
	}
	
	public TemplateJob(String identifier,
			   URL template,
			   URL output,
			   Boolean selected) {

	    setIdentifier(identifier);
	    setTemplate(template);
	    setOutput(output);
	    setSelected(selected);
	    parameters = new HashMap<String,String>();
	}

	public void setIdentifier(String identifier) {
	    this.identifier = identifier;
	}
	
	public String getIdentifier() {
	    return identifier;
	}

	public void setSelected(boolean selected) {
	    this.selected = selected;
	}

	public boolean getSelected() {
	    return selected;
	}
	
	public void setTemplate(URL template) {
	    this.template = template;
	}
	
	public URL getTemplate() {
	    return template;
	}

	public void setOutput(URL output) {
	    this.output = output;
	}
	
	public URL getOutput() {
	    return output;
	}

	public void setParameter(String name, String value) {
	    parameters.put(name, value);
	}

	public void renameParameter(String oldIdentifier,
				    String newIdentifier) {
	    parameters.put(newIdentifier,
			   parameters.get(oldIdentifier));
	    parameters.remove(oldIdentifier);
	}

	public String getParameter(String name) {
	    return parameters.get(name);
	}

	public Set<String> getParameters() {
	    return parameters.keySet();
	}
    }

    public ArgoPrintManagerModel() {
	jobs = new HashMap();
    }

    public void addJob(String identifier) {
	jobs.put(identifier, new TemplateJob(identifier));
    }

    public void addJob(TemplateJob job) {
	jobs.put(job.getIdentifier(), job);
    }

    public Set<String> getIdentifiers() {
	return jobs.keySet();
    }

    // TODO: modify for identifier modification
    public TemplateJob getJob(String identifier) {
	return jobs.get(identifier);
    }


    // IO methods
    public void fromDOMDocument(Document doc) {
	NodeList jobNodes = doc.getDocumentElement()
	    .getElementsByTagName("job");
	NodeList parameters;

	TemplateJob job;
	Element jobElement, parameterElement;

	for (int i = 0; i < jobNodes.getLength(); i++) {
	    jobElement = (Element)jobNodes.item(i);
	    job = new TemplateJob(jobElement.getAttribute("identifier"));

	    job.setSelected(Boolean.parseBoolean(jobElement.getAttribute("selected")));

	    try {
		job.setTemplate(new URL(jobElement.getAttribute("template")));
		job.setOutput(new URL(jobElement.getAttribute("output")));
	    } catch (java.net.MalformedURLException ex) {
		//TODO
		ex.printStackTrace();
	    }
	    
	    parameters = jobElement.getElementsByTagName("parameter");
	    for (int j = 0; j < parameters.getLength(); j++) {
		parameterElement = (Element)parameters.item(j);
		job.setParameter(parameterElement.getAttribute("name"),
				 parameterElement.getAttribute("value"));
	    }

	    addJob(job);
	}
    }

    public Document toDOMDocument() {
	Document doc = null;
	
	try {
	    doc = DocumentBuilderFactory
		.newInstance()
		.newDocumentBuilder()
		.newDocument();
	} catch (javax.xml.parsers.ParserConfigurationException ex) {
	    //TODO:
	    System.err.println("Should not happen.");
	}
	    
	Element documentElement, elementJob, elementParameter;
	TemplateJob currentJob;

	documentElement = doc.createElement("jobs");
	doc.appendChild(documentElement);
	
	Iterator<String> identifier = jobs.keySet().iterator();

	while (identifier.hasNext()) {
	    currentJob = getJob(identifier.next());

	    elementJob = doc.createElement("job");
	    elementJob.setAttribute("identifier",
					currentJob.getIdentifier());
	    elementJob.setAttribute("template",
					currentJob.getTemplate()
					.toString());
	    elementJob.setAttribute("output",
					currentJob.getOutput()
					.toString());
	    elementJob.setAttribute("selected",
					"" + currentJob.getSelected());

	    Iterator<String> parameter =
		currentJob.getParameters().iterator();

	    while (parameter.hasNext()) {
		String name = parameter.next();
		elementParameter = doc.createElement("parameter");
		elementParameter.setAttribute("name", name);
		elementParameter.setAttribute("value",
					      currentJob.getParameter(name));

		elementJob.appendChild(elementParameter);
	    }

	    documentElement.appendChild(elementJob);
	}

	return doc;
    }
    public void saveData(File file) {
	Document doc = toDOMDocument();

	// TODO: indentation of output
	try {
	    Transformer transformer = TransformerFactory
		.newInstance()
		.newTransformer();
	    transformer.transform(new DOMSource(doc), new StreamResult(file));
	} catch (javax.xml.transform.TransformerConfigurationException ex) {
	    // TODO
	    System.err.println(ex);
	} catch (javax.xml.transform.TransformerException ex) {
	    // TODO
	    System.err.println(ex);
	}
    }
    public void loadData(File file) {
	// TODO: implement the exception handling part
	Document doc = null;

	try {
	    doc = DocumentBuilderFactory
		.newInstance()
		.newDocumentBuilder()
		.parse(file);
	} catch (javax.xml.parsers.ParserConfigurationException ex) {

	    System.err.println(ex);
	} catch (org.xml.sax.SAXException ex) {
	    System.err.println(ex);
	} catch (java.io.IOException ex) {
	    System.err.println(ex);
	}
	
	fromDOMDocument(doc);
    }

    // TODO: throw exceptions
    public void generateOutput() {
	Transformer transformer = null;
	File fileXSLT = null , fileOut = null;
	StreamSource input = null;

	Iterator identifier = getIdentifiers().iterator();
	TemplateJob job;
	
	while (identifier.hasNext()) {
	    job = getJob((String)identifier.next());

	    String inputParameter = job.getParameter("argouml");

	    if (inputParameter == null)
		// TODO: throw exception
		return;
	    else if (inputParameter.equals("model")) {
		Project project =
		    ProjectManager.getManager().getCurrentProject();

		StringReader xmlString =
		    new StringReader(PersistenceManager
				     .getInstance()
				     .getQuickViewDump(project));

		input = new StreamSource(xmlString);
	    } else {
		// TODO: from file
	    }

	    try {
// 		fileXSLT = new File(job.getTemplate().toURI());
// 		fileOut = new File(job.getOutput().toURI());

		fileXSLT = new File(new URL("file:/home/comp_/xmieval.xsl").toURI());
		fileOut = new File(new URL("file:/home/comp_/out.xml").toURI());
	    } catch (java.net.URISyntaxException ex) {
		// TODO:
		ex.printStackTrace();
	    } catch (java.net.MalformedURLException ex) {
		// TODO:
		ex.printStackTrace();
	    }
	    
	    try {
		transformer = TransformerFactory
		    .newInstance()
		    .newTransformer(new StreamSource(fileXSLT));

		transformer.transform(input, new StreamResult(fileOut));
	    } catch (javax.xml.transform.TransformerConfigurationException ex) {
		// TODO:
		ex.printStackTrace();
	    } catch (javax.xml.transform.TransformerException ex) {
		// TODO:
		ex.printStackTrace();
	    }
	}
    }
}


