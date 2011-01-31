// $Id$
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

package org.argoprint.persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sun.misc.Service;

/**
 * This factory creates instances of PostProcessors which take a raw XML file
 * and perform additional processing on the file prior to output. A typical
 * PostProcessor is responsible for serializing Open Document Format XML files
 * into an ODF zip file.
 * 
 * @author mfortner
 * 
 */
public class PostProcessorFactory {

    static Map<String, PostProcessor> postProcessorMap = new HashMap<String, PostProcessor>();

    static {
        Iterator<PostProcessor> it = Service
                .providers(org.argoprint.persistence.PostProcessor.class);

        PostProcessor proc = null;
        while (it.hasNext()) {
            proc = it.next();
            String[] exts = null;
            if (proc != null) {
                exts = proc.getSupportedExtensions();
                for (int i = 0; i < exts.length; i++) {
                    postProcessorMap.put(exts[i], proc);
                }
            }
        }
    }

    /**
     * This method gets an instance of a post processor.
     * 
     * @param ext The file extension produced by this post processor.
     * @return a PostProcessor or null if one is not found.
     */
    public static PostProcessor getInstance(String ext) {
        if (ext == null || ext.equals("")) {
            throw new IllegalArgumentException(
                    "Null or empty file extensions are not supported");
        }
        return postProcessorMap.get(ext);
    }

}
