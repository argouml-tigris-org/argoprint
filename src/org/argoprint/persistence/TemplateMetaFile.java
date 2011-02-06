// $Id$
// Copyright (c) 2010 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argoprint.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * This class represents the metadata for a template file. This metadata is
 * saved in an XML file in the .argouml/templates directory
 * 
 * @author mfortner
 */
@SuppressWarnings("serial")
public class TemplateMetaFile implements Cloneable, Serializable{
    
    public static final Logger LOG = Logger.getLogger(TemplateMetaFile.class);
    
    /** Constant defining the default categories for templates */
    public static final String[] CATEGORIES = new String[]{"Requirements","Design","Deployment"};
    
    /** Constant defining the default groups for templates */
    public static final String[] GROUPS = new String[]{"ArgoUML","Personal"};

    /** The file name */
    private String templateFile = null;

    /** The document name */
    private String name = null;

    /** The description of the template file */
    private String description = null;

    /** The group that the template belongs to */
    private String group = null;

    /**
     * Indicates whether the template file has been selected for a batch
     * operation
     */
    private boolean selected = false;

    /** The category use for the file. */
    private String category = null;

    /** The output file */
    private String outputFile;
    
    

    /**
     * Constructor
     * 
     * @param templateFile      The path to the template file
     * @param outputFile        The output file name.
     * @param name              The name of the template.
     * @param description       A description of the file.
     * @param group             A grouping used for the template.
     * @param category          A category used for the template.
     */
    public TemplateMetaFile(String templateFile, String outputFile, String name, String description,
            String group, String category) {
        super();
        this.templateFile = templateFile;
        this.outputFile = outputFile;
        this.name = name;
        this.description = description;
        this.group = group;
        this.category = category;
    }

    /**
     * Constructor
     */
    public TemplateMetaFile() {

    }
    
    /**
     * Indicates whether the template file is stored locally.
     * @return
     */
    public boolean isLocalTemplate(){
        return this.templateFile.startsWith("file:");
    }

    /**
     * Gets the category for the template.
     * 
     * @return  the category for the template
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the template.
     * @param category the category of the template
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the extension of the template file.
     * @return the extension of the template file
     */
    public String getType() {
        String filetype = null;
        if (this.templateFile != null) {
            if (templateFile.lastIndexOf('.') != -1){
                filetype = templateFile.substring(templateFile.lastIndexOf('.'));
            }
        }
        return filetype;
    }

    /**
     * Gets the location of the template file.
     * @return
     */
    public String getTemplateFile() {
        return templateFile;
    }

    /**
     * Sets the location of the template file.
     * @param templateFile      
     */
    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }
    
    
    /** 
     * Gets the name of the output file.  The output directory is supplied at file generation time.
     * @return  A string containing the output file name.
     */
    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Gets the name for the template.
     * @return the name for the template
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the template.
     * @param name   the name for the template
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description for the template.
     * @return  the description for the template.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the template.
     * @param description the description of the template.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the group for the template.
     * @return the group for the template
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group for the template.
     * @param group the group for the template
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Indicates whether the template meta file has been selected in a table model.
     * @return true if the template has been selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Determines whether the template meta file has been selected in a table model.
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Read the template out of a file in the classpath. The template may be one
     * of the standard templates found in the JAR or they may be a template file
     * found on a web server, or in a local directory
     * 
     * @return null if the file protocol attribute is not supported (i.e. not
     *         "file://" or not "http://")
     * @throws IOException If there is a problem creating an input stream.
     * @throws  
     */
    public InputStream getTemplateStream() throws IOException {
        InputStream is = null;
        if (this.templateFile != null) {
            if (this.templateFile.startsWith("/template")) {
                is = ClassLoader.getSystemClassLoader().getResourceAsStream(this.templateFile);
                if (is == null) {
                    throw new IOException("Unable to retrieve template: "
                            + this.templateFile);
                }
            } else if (this.templateFile.startsWith("file:")) {
                File localFile = null;
                try {
                    localFile = new File(new URI(this.templateFile));
                } catch (URISyntaxException e) {
                    LOG.error("Exception", e);
                    throw new IOException(e);
                }
                is = new FileInputStream(localFile);
            } else if (this.templateFile.startsWith("http:")) {
                URL url = new URL(this.templateFile);
                is = url.openStream();
            } else {
                throw new IOException("Unsupported template location: " + this.templateFile);
            }
        }
        return is;
    }
    
    @Override
    public String toString(){
        return String.format("TemplateMetaFile:[id:%d, selected:%b, name:%s, category:%s]", this.hashCode(), this.selected, this.name, this.category);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        TemplateMetaFile clone = new TemplateMetaFile(this.templateFile, this.outputFile, this.name, this.description, this.group, this.category);
        return clone;
    }


    
    

}
