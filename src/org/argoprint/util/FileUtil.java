/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    phidias
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2009 The Regents of the University of California. All
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

package org.argoprint.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

/**
 * This class contains file-related utility methods.
 * 
 * @author mfortner
 */
public class FileUtil {

    private static final Logger LOG = Logger.getLogger(FileUtil.class);

    /**
     * This method gets the extension of a file.
     * 
     * @param file The file whose extension you want.
     * @return The file extension, or null if no extension exists.
     */
    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    /**
     * This method gets the extension of a file
     * 
     * @param filename The name of the file.
     * @return The file extension, or null if no extension exists.
     */
    public static String getExtension(String filename) {
        String ext = null;
        int index = filename.lastIndexOf(".");
        if (filename != null && !filename.equals("") && index != -1) {
            ext = filename.substring(index + 1);
        }
        return ext;
    }

    /**
     * This method reads the contents of an input stream and returns them as a
     * String.
     * 
     * @param is The input stream.
     * @return The contents of the input stream.
     */
    public static String readTextStream(InputStream is) {
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is,
                    "UTF-8"));

            int n;

            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);

            }

        } catch (IOException ex) {
            LOG.error(ex);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.error("Exception", e);
            }

        }

        return writer.toString();

    }

    /**
     * This method reads the contents of a file and returns them as a String.
     * 
     * @param file The file to be read.
     * @return The contents of the file.
     * @throws FileNotFoundException If the file is not found.
     */
    public static String readTextFile(File file) throws FileNotFoundException {
        return readTextStream(new FileInputStream(file));
    }
    
    /**
     * This method writes a String out to the specified file.
     * @param outputFile        The output file
     * @param contents          The contents of the output file.
     * @throws IOException      If there is a problem writing out the file.
     */
    public static void writeString(File outputFile, String contents) throws IOException{
        FileWriter writer = new FileWriter(outputFile);
        writer.write(contents);
        writer.flush();
        writer.close();
    }
}
