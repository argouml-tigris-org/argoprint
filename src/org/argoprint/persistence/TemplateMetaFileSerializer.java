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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
                "Design");

        TemplateMetaFile designTemplate2 = new TemplateMetaFile(
                "/templates/design_html2.vm", "DesignDoc.html", "DesignDoc",
                "A software design document", "ArgoUML", "Design");

        TemplateMetaFile reqTemplate = new TemplateMetaFile(
                "/templates/requirements_html.vm", "RequirementsDoc.html",
                "RequirementsDoc", "A software requirements document",
                "ArgoUML", "Requirements");
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
        XMLDecoder decoder = new XMLDecoder(new FileInputStream(file));
        metafile = (TemplateMetaFile) decoder.readObject();
        return metafile;
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
        TemplateMetaFile metafile = null;
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(file);
        if (is == null){
            throw new IOException("Unable to find resource: " + file);
        }
        XMLDecoder decoder = new XMLDecoder(is);
        metafile = (TemplateMetaFile) decoder.readObject();
        return metafile;
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
        FileOutputStream out = new FileOutputStream(file);
        XMLEncoder encoder = new XMLEncoder(out);
        encoder.writeObject(metaFile);
        encoder.flush();
        encoder.close();

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
