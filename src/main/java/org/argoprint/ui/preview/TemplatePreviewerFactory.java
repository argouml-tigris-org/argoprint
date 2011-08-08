/* $Id$
 *******************************************************************************
 * Copyright (c) 2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mfortner
 *******************************************************************************
 */

package org.argoprint.ui.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.argoprint.persistence.TemplateMetaFile;
import org.argouml.cognitive.Translator;

import sun.misc.Service;

public class TemplatePreviewerFactory {

    private static Map<String, TemplatePreviewer> previewerMap = new HashMap<String, TemplatePreviewer>();
    private static List<TemplatePreviewer> previewerList = new ArrayList<TemplatePreviewer>();
    
    
    static {
        previewerMap = loadPreviewers(null);
        if (previewerMap.isEmpty()){
            previewerMap = loadPreviewers(Translator.class.getClassLoader());
        }
        
        if (previewerMap.isEmpty()){
            previewerMap = loadPreviewers(ClassLoader.getSystemClassLoader());
        }
        
        if (previewerMap.isEmpty()){
            previewerList.add(new TextPreviewer());
            previewerList.add(new HTMLPreviewer());
            
            for(TemplatePreviewer previewer:previewerList){
                addPreviewer(previewer, previewerMap);
            }
        }
    }
    
    private static Map<String, TemplatePreviewer> loadPreviewers(ClassLoader loader){
        Map<String, TemplatePreviewer> map = new HashMap<String, TemplatePreviewer>();
        
        Iterator<TemplatePreviewer> it = Service.providers(TemplatePreviewer.class, Translator.class.getClassLoader());
        TemplatePreviewer previewer=null;
        while(it.hasNext()){
            previewer = it.next();
            addPreviewer(previewer, map);
        } 
        return map;
    }
    
    private static void addPreviewer(TemplatePreviewer previewer, Map<String, TemplatePreviewer> map){
        String[] exts = previewer.getSupportedFileExtensions();
        for(String ext:exts){
            previewerMap.put(ext, previewer);
        }
    }

    /**
     * This method gets an instance of the previewer appropriate for the
     * template's output file.
     * 
     * @param template  The template metafile.
     * @return  
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static TemplatePreviewer getPreviewer(TemplateMetaFile template)
        throws InstantiationException, IllegalAccessException {
        TemplatePreviewer previewer = null;

        previewer = previewerMap.get(template.getOutputFileExtension());

        if (previewer != null) {
            previewer = previewer.getClass().newInstance();
            previewer.init(template);
        }

        return previewer;
    }

}
