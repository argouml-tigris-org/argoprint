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
