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

package org.argoprint.persistence;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.argouml.kernel.Project;
import org.argouml.uml.diagram.ArgoDiagram;
import org.tigris.gef.presentation.Fig;

/**
 * This interface defines a templated code generator.
 *
 * @author mfortner
 */
public interface CodeTemplateEngine {

    /**
     * Generates the classes and interfaces that match a given predicate. If the
     * predicate is null, then all classes and interfaces in the project are
     * generated.  It iterates through each diagram in the project.
     * 
     * @param project The ArgoUML project.
     * @param predicate The predicate to be applied to the project. If null,
     *            then all classes and interfaces in the project are generated.
     * @param templateMetaFile The templateMetaFile which describes the template
     *            used in code generation.
     * @param outputDir The output directory. If the output directory does not
     *            exist then it is created.
     * @throws IOException If there is a problem writing the classes out.
     * @throws TemplateEngineException If there is a problem with the template
     *             engine.
     */
    public void generate(Project project, Predicate predicate,
            TemplateMetaFile templateMetaFile, String outputDir)
        throws IOException, TemplateEngineException;

    /**
     * Generates the interfaces that match a given predicate.
     * 
     * @param interfaceList A list of interfaces to be generated.
     * @param predicate A predicate used to filter the interface list. If null
     *            then all interfaces are generated.
     * @param templateMetaFile The template metafile which describes the
     *            template.
     * @param outputDir The output directory. If the output directory does not
     *            exist then it is created.
     * @throws IOException If there is a problem writing the classes out.
     * @throws TemplateEngineException If there is a problem with the template
     *             engine.
     */
    public void generateInterfaces(List<Fig> interfaceList,
            Predicate predicate, TemplateMetaFile templateMetaFile,
            String outputDir) throws IOException, TemplateEngineException;

    /**
     * Generates the classes that match a given predicate.
     * 
     * @param classList A list of classes to be generated.
     * @param predicate A predicate used to filter the class list. If null, then
     *            all classes are generated.
     * @param templateMetaFile The template metafile which describes the
     *            template.
     * @param outputDir The output directory. If the output directory does not
     *            exist then it is created.
     * @throws IOException If there is a problem writing the classes out.
     * @throws TemplateEngineException If there is a problem with the template
     *             engine.
     */
    public void generateClasses(List<Fig> classList, Predicate predicate,
            TemplateMetaFile templateMetaFile, String outputDir)
        throws IOException, TemplateEngineException;

    /**
     * Generates classes and interfaces found in a particular diagram.
     * 
     * @param diagram The ArgoUML diagram
     * @param predicate A predicate used to filter the elements in the diagram.
     * @param templateMetaFile The template metafile which describes the
     *            template.
     * @param outputDir The output directory. If the output directory does not
     *            exist then it is created.
     * @throws IOException If there is a problem writing the classes out.
     * @throws TemplateEngineException If there is a problem with the template
     *             engine.
     */
    public void generate(ArgoDiagram diagram, Predicate predicate,
            TemplateMetaFile templateMetaFile, String outputDir)
        throws IOException, TemplateEngineException;
}
