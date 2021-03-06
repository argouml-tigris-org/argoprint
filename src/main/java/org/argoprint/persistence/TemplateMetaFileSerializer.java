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
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is used to serialize (read/write) TemplateMetaFile objects.
 * 
 * @author mfortner
 */
public class TemplateMetaFileSerializer {

    static final Logger logger = Logger
            .getLogger(TemplateMetaFileSerializer.class);

    static List<TemplateMetaFile> defaultTemplates = new ArrayList<TemplateMetaFile>();

    static {
        TemplateMetaFile designTemplate = new TemplateMetaFile(
                "/templates/design_html.vm", "DesignDoc.html", "DesignDocSVG",
                "A software design document with SVG diagrams", "ArgoUML",
                "Design", true);

        TemplateMetaFile designTemplate2 = new TemplateMetaFile(
                "/templates/design_html2.vm", "DesignDoc.html", "DesignDoc",
                "A software design document", "ArgoUML", "Design", true);

        TemplateMetaFile reqTemplate = new TemplateMetaFile(
                "/templates/requirements_html.vm", "RequirementsDoc.html",
                "RequirementsDoc", "A software requirements document",
                "ArgoUML", "Requirements", true);
        defaultTemplates.add(designTemplate);
        defaultTemplates.add(designTemplate2);
        defaultTemplates.add(reqTemplate);
    }

    /**
     * This method reads a template metadata XML file and returns a
     * TemplateMetaFile object.
     * 
     * @param file The template metadata file.
     * @return A TemplateMetaFile object.
     * @throws IOException If there is a problem reading the file.
     */
    public static TemplateMetaFile read(String file) throws IOException {
        File fileObj = new File(file);
        if (!fileObj.exists()) {
            throw new IOException("File not found: "
                    + fileObj.getAbsolutePath());
        }
        return read(fileObj);
    }

    /**
     * This method reads a template metadata XML file and returns a
     * TemplateMetaFile object.
     * 
     * @param file The template metadata file.
     * @return A TemplateMetaFile object.
     * @throws IOException If there is a problem reading the file.
     */
    public static TemplateMetaFile read(File file) throws IOException {
        TemplateMetaFile metafile = null;
        if (!file.exists()) {
            throw new IOException("File not found: " + file.getAbsolutePath());
        }
        return readTemplate(new InputSource(new FileInputStream(file)));
    }

    /**
     * This method reads TemplateMetaFiles from the classpath.
     * 
     * @param file The TemplateMetaFile xml file.
     * @return
     * @throws IOException If there is a problem reading the file.
     */
    public static TemplateMetaFile readTemplateFromClassPath(String file)
        throws IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(file);
        if (is == null){
            throw new IOException("Unable to find resource: " + file);
        }
        return readTemplate(new InputSource(is));
    }
    
    public static TemplateMetaFile readTemplate(InputStream is){
        return readTemplate(new InputSource(is));
    }
    
    public static TemplateMetaFile readTemplate(InputSource source){
        TemplateMetaFile template = new TemplateMetaFile();
        
        try {
            DocumentBuilder builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Element root     = builder.parse(source).getDocumentElement();
            
            XPath xpath = XPathFactory.newInstance().newXPath();

            template.name = (String)xpath.evaluate( "/template/@name", root, XPathConstants.STRING );
            template.group = (String)xpath.evaluate( "/template/@group", root, XPathConstants.STRING );
            template.category = (String)xpath.evaluate( "/template/@category", root, XPathConstants.STRING );
            template.description = (String)xpath.evaluate( "/template/@description", root, XPathConstants.STRING );
            template.outputFile = (String)xpath.evaluate( "/template/@outputFile", root, XPathConstants.STRING );
            template.templateFile = (String)xpath.evaluate( "/template/@templateFile", root, XPathConstants.STRING );
        } catch (XPathExpressionException e) {
            // TODO: Auto-generated catch block
            logger.error("Exception", e);
        } catch (ParserConfigurationException e) {
            // TODO: Auto-generated catch block
            logger.error("Exception", e);
        } catch (SAXException e) {
            // TODO: Auto-generated catch block
            logger.error("Exception", e);
        } catch (IOException e) {
            // TODO: Auto-generated catch block
            logger.error("Exception", e);
        }
         
        return template;
    }

    /**
     * This method writes a template metadata XML file out to disk.
     * 
     * @param file The template metadata file.
     * @param metafile The template metafile object.
     * @throws IOException If there is a problem writing the file.
     */
    public static void write(String file, TemplateMetaFile metafile)
        throws IOException {
        write(new File(file), metafile);
    }

    /**
     * This method writes a template metadata XML file out to disk.
     * 
     * @param file The template metadata file.
     * @param metaFile The template metafile object.
     * @throws IOException If there is a problem writing the file.
     */
    public static void write(File file, TemplateMetaFile metaFile)
        throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("<?xml version=\"1.0\"?>");
        writer.write("<template \n");
        writeAttribute(writer, "name", 
                metaFile.getName());
        writeAttribute(writer, "group", metaFile.getGroup());
        writer.write("\n");
        writeAttribute(writer, "category", metaFile.getCategory());
        writer.write("\n");
        writeAttribute(writer, "type", metaFile.getType());
        writer.write("\n");
        writeAttribute(writer, "description", metaFile.getDescription());
        writer.write("\n");
        writeAttribute(writer, "templateFile", metaFile.getTemplateFile());
        writer.write("\n");
        writeAttribute(writer, "outputFile", metaFile.getOutputFile());
        writer.write("/>");
        
        writer.flush();
        writer.close();
    }
    
    private static void writeAttribute(FileWriter writer, String name, String value) throws IOException{
        writer.write(name+"=\"" + value + "\" ");
    }

    /**
     * This method writes an XML version of the TemplateMetaFile into the
     * .argouml/templates directory.
     * 
     * @param template The templateMetaFile to be written
     * @throws IOException If there is a problem writing the file out.
     */
    public static void write(TemplateMetaFile template) throws IOException {
        File userHome = new File(System.getProperty("user.home"));
        File metaFile = new File(userHome, ".argouml/templates/"
                + template.getName() + ".xml");
        System.out.println("writing file: " + metaFile.getAbsolutePath());
        write(metaFile, template);
    }

    /**
     * This method gets all of the templates.
     * 
     * @param rootdir The root directory where all template files are located.
     * @return
     */
    public static List<TemplateMetaFile> getTemplates(String rootdir) {
        List<TemplateMetaFile> metaFileList = new ArrayList<TemplateMetaFile>();
        if (rootdir != null) {
            File root = new File(rootdir);
            String[] templateFiles = root.list(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });

            TemplateMetaFile currTemplate = null;

            for (String filename : templateFiles) {
                try {
                    currTemplate = read(new File(root, filename));
                    metaFileList.add(currTemplate);
                } catch (IOException e) {
                    logger.error("Exception", e);
                }
            }

        }

        return metaFileList;
    }

    /**
     * This method gets the default templates.
     * 
     * @return
     */
    public static List<TemplateMetaFile> getTemplates() {
        return defaultTemplates;
    }

    /**
     * This method returns all template metafiles found in the classpath and in
     * the user's home directory.
     * 
     * @return
     */
    public static List<TemplateMetaFile> getAllTemplates() {

        List<TemplateMetaFile> fileList = getTemplates();

        String userTemplateRoot = System.getProperty("user.home")
                + "/.argouml/templates";
        File utRoot = new File(userTemplateRoot);
        if (utRoot.exists()) {
            fileList.addAll(getTemplates(userTemplateRoot));
        }

        return fileList;
    }
}
