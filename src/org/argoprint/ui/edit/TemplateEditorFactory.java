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

package org.argoprint.ui.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.ui.edit.velocity.VelocityTemplateEditor;
import org.argouml.i18n.Translator;

public class TemplateEditorFactory {

    /**
     * A map containing the file extensions and the appropriate editor for that
     * file type
     */
    private static Map<String, TemplateEditor> templateEditorMap = new HashMap<String, TemplateEditor>();

    private static List<TemplateEditor> editorList = new ArrayList<TemplateEditor>();
    
    static {
        templateEditorMap = loadEditors(null);
        if(templateEditorMap.isEmpty()){
            templateEditorMap = loadEditors(Translator.class.getClassLoader());
        }
        
        if (templateEditorMap.isEmpty()){
            templateEditorMap = loadEditors(ClassLoader.getSystemClassLoader());
        }
        
        if (templateEditorMap.isEmpty()){
            editorList.add(new VelocityTemplateEditor());
            
            for(TemplateEditor editor:editorList)
            {
                addToMap(editor, templateEditorMap);
            }
        }
    }
    
    private static Map<String, TemplateEditor> loadEditors(ClassLoader loader){ 
        Map<String, TemplateEditor> editorMap = new HashMap<String, TemplateEditor>();
        ServiceLoader svcLoader = (loader != null)?
                ServiceLoader.load(TemplateEditor.class, loader):
                ServiceLoader.load(TemplateEditor.class);
                
        Iterator<TemplateEditor> it = svcLoader.iterator();
        while (it.hasNext()) {
            TemplateEditor editor = it.next();
            addToMap(editor, editorMap);
        }
        return editorMap;
    }
    
    private static void addToMap(TemplateEditor editor, Map<String, TemplateEditor> map){
        String[] exts = editor.getSupportedFileExtensions();
        for (String ext : exts) {
            System.out.println("register editor: "+editor.getClass().getName());
            map.put(ext, editor);
        }
    }

    /**
     * Gets a new instance of the template editor bound to the specified
     * template.
     * 
     * @param template  the template meta file used to select an editor.
     * @return a new instance of the template editor or null if not found.
     * @throws IllegalAccessException If there is a problem creating a new
     *             instance of the editor
     * @throws InstantiationException If there is a problem creating a new
     *             instance of the editor
     */
    public static TemplateEditor getTemplateEditor(TemplateMetaFile template)
        throws InstantiationException, IllegalAccessException {
        TemplateEditor editor = null;
System.out.println(String.format("ext: '%s'",template.getTemplateFileExtension()));
        editor = templateEditorMap.get(template.getTemplateFileExtension());
        if (editor != null){
            editor = editor.getClass().newInstance();
        }

        return editor;
    }

}
