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

package org.argoprint.persistence.velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.argoprint.persistence.CodeTemplateEngine;
import org.argoprint.persistence.TemplateEngineException;
import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.ui.AbstractExecutable;
import org.argoprint.ui.Executor;
import org.argoprint.util.DiagramUtil;
import org.argouml.kernel.Project;
import org.argouml.model.Model;
import org.argouml.uml.diagram.ArgoDiagram;
import org.tigris.gef.presentation.Fig;

/**
 * Implementation of the CodeTemplateEngine using the Velocity templating
 * engine.
 * 
 * @author mfortner
 */
public class VelocityCodeTemplateEngine implements CodeTemplateEngine {

    private Executor exec = new Executor();

    /**
     * Constructor
     */
    public VelocityCodeTemplateEngine() {

    }


    public void generate(Project project, Predicate predicate,
            TemplateMetaFile templateMetaFile, String outputDir)
        throws IOException, TemplateEngineException {

        List<ArgoDiagram> diagramList = DiagramUtil.getClassDiagrams(project);

        for (ArgoDiagram diagram : diagramList) {
            generate(diagram, predicate, templateMetaFile, outputDir);
        }

    }

    public void generate(ArgoDiagram diagram, Predicate predicate,
            TemplateMetaFile templateMetaFile, String outputDir)
        throws IOException, TemplateEngineException {

        List<Fig> interfaceList = DiagramUtil.getInterfaces(diagram);
        if (predicate != null) {
            CollectionUtils.filter(interfaceList, predicate);
        }
        generateInterfaces(interfaceList, predicate, templateMetaFile,
                outputDir);

        List<Fig> classList = DiagramUtil.getClasses(diagram);
        if (predicate != null) {
            CollectionUtils.filter(classList, predicate);
        }
        generateClasses(classList, predicate, templateMetaFile, outputDir);
    }

    public void generateClasses(List<Fig> classList, Predicate predicate,
            TemplateMetaFile templateMetaFile, String outputDir)
        throws IOException, TemplateEngineException {

        for (Fig clazz : classList) {
            exec.addExecutable(new CodeGenerator(clazz, templateMetaFile,
                    outputDir));
        }
        exec.execAll();
    }

    public void generateInterfaces(List<Fig> interfaceList,
            Predicate predicate, TemplateMetaFile templateMetaFile,
            String outputDir) throws IOException, TemplateEngineException {

        for (Fig intf : interfaceList) {
            exec.addExecutable(new CodeGenerator(intf, templateMetaFile,
                    outputDir));
        }

        exec.execAll();

    }

    /**
     * This class is responsible for generating the code for a particular class
     * or interface.
     * 
     * @author mfortner
     */
    class CodeGenerator extends AbstractExecutable {

        private Fig fig;

        private TemplateMetaFile templateMetaFile;

        private String outputDir;

        /**
         * Constructor
         * 
         * @param fig
         * @param templateMetaFile
         * @param outputDir
         */
        public CodeGenerator(Fig fig, TemplateMetaFile templateMetaFile,
                String outputDir) {
            this.fig = fig;
            this.templateMetaFile = templateMetaFile;
            this.outputDir = outputDir;
        }

        @Override
        public void run() {

            List methodList = Model.getFacade().getOperations(fig.getOwner());
            String pkg = (String) Model.getFacade().getPackage(fig.getOwner());
            String name = Model.getFacade().getName(fig.getOwner());
                       
            File outputFile = createOutputFile(outputDir, pkg, name, templateMetaFile.getType());
            
            try {

                Velocity.init();

                VelocityContext context = new VelocityContext();

                context.put("methods", methodList);
                context.put("DiagramUtil", new DiagramUtil());
                context.put("pkg", pkg);
                context.put("name", name);

                Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile));

                Velocity.evaluate(context, writer, "VELOCITY",
                        new InputStreamReader(this.templateMetaFile.getTemplateStream()));

                writer.flush();
                writer.close();

            } catch (Throwable th) {
                this.setException(th);
            }

        }

        private File createOutputFile(String outputDir, String pkg, String name, String type) {
            return new File(outputDir + File.separator + pkg, name+type);
        }

    }

}
