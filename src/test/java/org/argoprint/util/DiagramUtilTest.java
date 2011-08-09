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

package org.argoprint.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.model.Model;
import org.argouml.notation.InitNotation;
import org.argouml.notation.providers.java.InitNotationJava;
import org.argouml.notation.providers.uml.InitNotationUml;
import org.argouml.persistence.AbstractFilePersister;
import org.argouml.persistence.OpenException;
import org.argouml.persistence.PersistenceManager;
import org.argouml.profile.ProfileFacade;
import org.argouml.profile.init.InitProfileSubsystem;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.activity.ui.InitActivityDiagram;
import org.argouml.uml.diagram.collaboration.ui.InitCollaborationDiagram;
import org.argouml.uml.diagram.deployment.ui.InitDeploymentDiagram;
import org.argouml.uml.diagram.state.ui.InitStateDiagram;
import org.argouml.uml.diagram.static_structure.ui.FigClass;
import org.argouml.uml.diagram.static_structure.ui.FigInterface;
import org.argouml.uml.diagram.static_structure.ui.InitClassDiagram;
import org.argouml.uml.diagram.ui.InitDiagramAppearanceUI;
import org.argouml.uml.diagram.use_case.ui.FigUseCase;
import org.argouml.uml.diagram.use_case.ui.InitUseCaseDiagram;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tigris.gef.presentation.Fig;

public class DiagramUtilTest {

    static Project proj = null;

    private static final Logger LOG = Logger.getLogger(DiagramUtilTest.class);

    private static final String DEFAULT_MODEL_IMPLEMENTATION = "org.argouml.model.mdr.MDRModelImplementation";

    @BeforeClass
    public static void setup() throws Exception{

        System.out.println("Initializing Model");
        String className = System.getProperty("argouml.model.implementation",
                DEFAULT_MODEL_IMPLEMENTATION);
        Throwable ret = Model.initialise(className);
        if (ret != null){
            ret.printStackTrace();
        }
        LOG.info("Initializing");
        (new InitNotation()).init();
        (new InitNotationUml()).init();
        (new InitNotationJava()).init();
        (new InitDiagramAppearanceUI()).init();
        (new InitActivityDiagram()).init();
        (new InitCollaborationDiagram()).init();
        (new InitDeploymentDiagram()).init();
        (new InitStateDiagram()).init();
        (new InitClassDiagram()).init();
        (new InitUseCaseDiagram()).init();
        (new InitProfileSubsystem()).init();

        LOG.info("Reading file");
        PersistenceManager mgr = PersistenceManager.getInstance();

        URL url = DiagramUtilTest.class.getResource("/library.zargo");
        if (url == null){
            System.out.println("Unable to resolve file");
            throw new Exception("Unable to resolve library.zargo");
        }
        assertNotNull("Unable to resolve: library.zargo", url);
        
        String name = url.getFile();
        AbstractFilePersister persister = mgr.getPersisterFromFileName(name);
        try {
            File file = new File(name);
            assertTrue("The zargo file can't be found",file.exists());
            proj = persister.doLoad(file);
            
            for(ArgoDiagram diag: proj.getDiagramList()){
                System.out.println(diag.getClass().getName());
            }
            
            assertNotNull("The project was null", proj);
        } catch (OpenException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @After
    public void tearDown() {
        ProfileFacade.reset();
    }

    @Test
    public void testGetDiagramType() {
        List<ArgoDiagram> diagramList = DiagramUtil.getDiagramsByType(proj, DiagramUtil.USE_CASE_DIAGRAM);
        assertNotNull("No use case diagrams found",diagramList);
        assertFalse(diagramList.isEmpty());

        assertEquals(DiagramUtil.USE_CASE_DIAGRAM,  DiagramUtil.getDiagramType(diagramList.get(0)));
    }

    @Test
    public void testGetDiagramsByTypeProjectString() {
        List<ArgoDiagram> diagramList = DiagramUtil.getDiagramsByType(proj, DiagramUtil.USE_CASE_DIAGRAM);
        assertNotNull(diagramList);
        assertFalse("No use case diagrams found",diagramList.isEmpty());
    }




    @Test
    public void testGetAuthor() {
        String author = DiagramUtil.getAuthor(proj);
        assertNotNull("The author was null", author);
        assertFalse("The author field was empty", author.isEmpty());
    }

    @Test
    public void testGetProjectDescription() {
        String desc = DiagramUtil.getProjectDescription(proj);
        assertNotNull("The project description was null", desc);
        assertFalse("The project description was empty", desc.isEmpty());
    }

    @Test
    public void testGetProjectName() {
        String desc = DiagramUtil.getProjectName(proj);
        assertNotNull("The project name was null", desc);
        assertFalse("The project name was empty", desc.isEmpty());
    }


    @Test
    public void testGetUseCaseDiagrams() {
        List<ArgoDiagram> usecases = DiagramUtil.getUseCaseDiagrams(proj);
        assertNotNull(usecases);
        assertFalse("No use case diagrams found", usecases.isEmpty());
    }

    @Test
    public void testGetClassDiagrams() {
        List<ArgoDiagram> classes = DiagramUtil.getClassDiagrams(proj);
        assertNotNull(classes);
        assertFalse("No classDiagrams found", classes.isEmpty());
    }

    @Test
    public void testGetCollaborationDiagrams() {
        List<ArgoDiagram> collabs = DiagramUtil.getCollaborationDiagrams(proj);
        assertNotNull(collabs);
        assertFalse("No collaboration diagrams found", collabs.isEmpty());
    }

    @Test
    public void testGetDeploymentDiagrams() {
        List<ArgoDiagram> dep =  DiagramUtil.getDeploymentDiagrams(proj);
        assertNotNull(dep);
        assertFalse("No deployment diagrams found",dep.isEmpty());        
    }

    @Test
    public void testGetComponents() {
        List<ArgoDiagram> dep = DiagramUtil.getDeploymentDiagrams(proj);
        Map<String, String> compMap = DiagramUtil.getComponents(dep.get(0));
        assertNotNull(compMap);
        assertFalse("Unable to retrieve components",compMap.isEmpty());
    }

    @Test
    public void testGetSequenceDiagrams() {
        List<ArgoDiagram> seqDiagrams = DiagramUtil.getSequenceDiagrams(proj);
        assertNotNull(seqDiagrams);
        assertFalse("Unable to retrieve sequence diagrams", seqDiagrams.isEmpty());
    }

    @Test
    public void testGetSequenceClassifiers() {
        List<ArgoDiagram> seqDiagrams = DiagramUtil.getSequenceDiagrams(proj);
        assertNotNull(seqDiagrams);
        assertFalse("Unable to retrieve sequence diagrams", seqDiagrams.isEmpty());
        List<Fig> seqClass = DiagramUtil.getSequenceClassifiers(seqDiagrams.get(0));
        assertNotNull(seqClass);
        assertFalse("Unable to retrieve sequence classifiers", seqClass.isEmpty());
     }

    @Test
    public void testGetStateDiagrams() {
        List<ArgoDiagram> stateDiagrams = DiagramUtil.getStateDiagrams(proj);
        assertNotNull(stateDiagrams);
        assertFalse("Unable to retrieve state diagrams",stateDiagrams.isEmpty());
    }

    @Test
    public void testGetStates() {
        List<ArgoDiagram> stateDiagrams = DiagramUtil.getStateDiagrams(proj);
        assertNotNull(stateDiagrams);
        assertFalse(stateDiagrams.isEmpty());
        
        Map<String, String> states = DiagramUtil.getStatesAsMap(stateDiagrams.get(0));
        assertNotNull(states);
        assertFalse("Unable to retrieve states",states.isEmpty());

    }

    @Test
    public void testGetUseCases() {
        List<ArgoDiagram> useCases = DiagramUtil.getUseCaseDiagrams(proj);
        assertNotNull(useCases);
        assertFalse(useCases.isEmpty());

        ArgoDiagram diagram = useCases.get(0);
        List<Fig> figs = DiagramUtil.getUseCases(diagram);
        assertNotNull(figs);
        assertFalse("No use cases found in diagram: " + diagram.getName(),
                figs.isEmpty());

    }

    @Test
    public void testGetUseCaseExtensionPoints() {
        List<ArgoDiagram> useCases = DiagramUtil.getUseCaseDiagrams(proj);
        assertNotNull(useCases);
        assertFalse(useCases.isEmpty());

        ArgoDiagram diagram = useCases.get(0);
        List<Fig> figs = DiagramUtil.getUseCases(diagram);
        assertNotNull(figs);
        assertFalse(figs.isEmpty());

        FigUseCase usecase = (FigUseCase) figs.get(0);

        Map<String, String> eps = DiagramUtil
                .getUseCaseExtensionPoints(usecase);
        assertNotNull(eps);
        assertFalse("Unable to retrieve use case extension points",eps.isEmpty());

    }

    @Test
    public void testGetClasses() {
        List<ArgoDiagram> classDiagrams = DiagramUtil.getClassDiagrams(proj);
        assertNotNull(classDiagrams);
        assertFalse(classDiagrams.isEmpty());

        ArgoDiagram diagram = classDiagrams.get(0);
        List<Fig> classes = DiagramUtil.getClasses(diagram);
        assertNotNull(classes);
        assertFalse("Unable to retrieve classes",classes.isEmpty());

    }

    @Test
    public void testGetMethodsFigInterface() {
        List<ArgoDiagram> classDiagrams = DiagramUtil.getClassDiagrams(proj);
        assertNotNull(classDiagrams);
        assertFalse(classDiagrams.isEmpty());

        ArgoDiagram diagram = classDiagrams.get(0);
        List<Fig> classes = DiagramUtil.getInterfaces(diagram);

        Map<String, String> methodMap = DiagramUtil
                .getMethods((FigInterface) classes.get(0));
        assertFalse("Unable to retrieve methods", methodMap.isEmpty());

    }

    @Test
    public void testGetMethodsFigClass() {
        List<ArgoDiagram> classDiagrams = DiagramUtil.getClassDiagrams(proj);
        assertNotNull(classDiagrams);
        assertFalse(classDiagrams.isEmpty());

        ArgoDiagram diagram = classDiagrams.get(0);
        List<Fig> classes = DiagramUtil.getClasses(diagram);
        
        Map<String, String> methodMap = DiagramUtil
                .getMethods((FigClass) classes.get(0));
        assertFalse("Unable to retrieve methods", methodMap.isEmpty());
    }

    @Test
    public void testGetAttributes() {
        List<ArgoDiagram> classDiagrams = DiagramUtil.getClassDiagrams(proj);
        assertNotNull(classDiagrams);
        assertFalse(classDiagrams.isEmpty());

        ArgoDiagram diagram = classDiagrams.get(0);
        List<Fig> classes = DiagramUtil.getClasses(diagram);

        Map<String, String> attrMap = DiagramUtil
                .getAttributes((FigClass) classes.get(0));
        assertFalse("Unable to retrieve attributes", attrMap.isEmpty());
    }

    @Test
    public void testGetInterfaces() {
        List<ArgoDiagram> classDiagrams = DiagramUtil.getClassDiagrams(proj);
        assertNotNull(classDiagrams);
        assertFalse(classDiagrams.isEmpty());

        ArgoDiagram diagram = classDiagrams.get(0);
        List<Fig> classes = DiagramUtil.getInterfaces(diagram);
        assertNotNull(classes);
        assertFalse("Unable to retrieve interfaces",classes.isEmpty());

    }

}
