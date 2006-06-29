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

import java.net.URL;
import java.util.HashMap;
import java.util.Set;

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
}


