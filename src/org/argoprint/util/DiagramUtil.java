package org.argoprint.util;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.kernel.Project;
import org.argouml.model.Model;
import org.argouml.sequence2.diagram.UMLSequenceDiagram;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.collaboration.ui.FigClassifierRole;
import org.argouml.uml.diagram.collaboration.ui.UMLCollaborationDiagram;
import org.argouml.uml.diagram.deployment.ui.FigComponent;
import org.argouml.uml.diagram.deployment.ui.UMLDeploymentDiagram;
import org.argouml.uml.diagram.state.ui.FigState;
import org.argouml.uml.diagram.state.ui.UMLStateDiagram;
import org.argouml.uml.diagram.static_structure.ui.FigClass;
import org.argouml.uml.diagram.static_structure.ui.FigInterface;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.argouml.uml.diagram.use_case.ui.FigUseCase;
import org.argouml.uml.diagram.use_case.ui.UMLUseCaseDiagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Layer;
import org.tigris.gef.persistence.export.SvgWriter;
import org.tigris.gef.presentation.Fig;

import sun.misc.BASE64Encoder;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * This class contains a collection of diagram-based utilities.
 * 
 * @author mfortner
 * 
 */
public class DiagramUtil {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(DiagramUtil.class);

    /** A constant used to identify a use case diagram */
    public static final String USE_CASE_DIAGRAM = "UseCaseDiagram";

    /** A constant used to identify a class diagram */
    public static final String CLASS_DIAGRAM = "ClassDiagram";

    /** A constant used to identify a deployment diagram */
    public static final String DEPLOYMENT_DIAGRAM = "DeploymentDiagram";

    /** A constant used to identify a collaboration diagram */
    public static final String COLLABORATION_DIAGRAM = "CollaborationDiagram";

    /** A constant used to identify a sequence diagram */
    public static final String SEQUENCE_DIAGRAM = "SequenceDiagram";

    /** A constant used to identify a state diagram */
    public static final String STATE_DIAGRAM = "StateDiagram";

    /**
     * This method determines what the type of diagram.
     * 
     * @param diagram The diagram whose type you want.
     * @return One of the diagram type constants defined in this class.
     */
    public static String getDiagramType(ArgoDiagram diagram) {
        String type = null;
        if (diagram instanceof UMLUseCaseDiagram) {
            type = USE_CASE_DIAGRAM;
        } else if (diagram instanceof UMLClassDiagram) {
            type = CLASS_DIAGRAM;
        } else if (diagram instanceof UMLDeploymentDiagram) {
            type = DEPLOYMENT_DIAGRAM;
        } else if (diagram instanceof UMLCollaborationDiagram) {
            type = COLLABORATION_DIAGRAM;
        } else if (diagram instanceof UMLSequenceDiagram) {
            type = SEQUENCE_DIAGRAM;
        } else if (diagram instanceof UMLStateDiagram) {
            type = STATE_DIAGRAM;
        }
        return type;
    }

    /**
     * This method is responsible for getting diagrams of a particular diagram
     * type.
     * 
     * @param project The top-level project object.
     * @param diagramType The type of diagram (use one of the constants for
     *            this)
     * @return A List of ArgoDiagrams
     */
    public static List<ArgoDiagram> getDiagramsByType(Project project,
            final String diagramType) {
        LOG.debug("diagramtype = " + diagramType);

        List<ArgoDiagram> diagramList = new ArrayList<ArgoDiagram>();

        Iterator<ArgoDiagram> diagIt = new FilterIterator(project
                .getDiagramList().iterator(), new Predicate() {

            public boolean evaluate(Object diagram) {
                ArgoDiagram diag = (ArgoDiagram) diagram;
                return diagramType.equals(getDiagramType(diag));
            }

        });

        while (diagIt.hasNext()) {
            diagramList.add(diagIt.next());
        }

        return diagramList;
    }

    /**
     * This method gets diagrams by a particular diagram type.
     * 
     * @param project The Argo project.
     * @param diagramType The diagram class. Should be an implementation of
     *            ArgoDiagram.
     * @return A list of ArgoDiagrams of a given type.
     */
    public static List<ArgoDiagram> getDiagramsByType(Project project,
            final Class diagramType) {

        List<ArgoDiagram> diagramList = new ArrayList<ArgoDiagram>();

        Iterator<ArgoDiagram> rawIt = project.getDiagramList().iterator();
        Iterator<ArgoDiagram> diagIt = new FilterIterator(rawIt,
                new Predicate() {

                    public boolean evaluate(Object diagram) {
                        ArgoDiagram diag = (ArgoDiagram) diagram;
                        return diag.getClass() == diagramType;
                    }

                });
        while (diagIt.hasNext()) {
            diagramList.add(diagIt.next());
        }

        return diagramList;
    }

    /**
     * This method gets the members of a particular diagram. For example, get
     * the use cases found on a use case diagram.
     * 
     * @param diagram The diagram being interrogated.
     * @param memberType The member class (an implementation of the Fig
     *            interface).
     * @return A list of members.
     */
    public static List<Fig> getMembersByType(ArgoDiagram diagram,
            final Class memberType) {

        List<Fig> memberList = new ArrayList<Fig>();

        Iterator<Fig> rawIt = diagram.getFigIterator();
        Iterator<Fig> diagIt = new FilterIterator(rawIt, new Predicate() {

            public boolean evaluate(Object diagram) {
                Fig diag = (Fig) diagram;
                return diag.getClass() == memberType;
            }

        });
        while (diagIt.hasNext()) {
            memberList.add(diagIt.next());
        }

        return memberList;
    }

    /**
     * This method gets a description of the member.
     * 
     * @param fig The member of a diagram whose description you want.
     * @return The description of the member.
     */
    public static String getMemberDescription(Fig fig) {
        return getMemberTaggedValue(fig, "documentation");
    }

    /**
     * This method gets a tagged value for a particular member of a diagram.
     * 
     * @param fig the member of a diagram (FigUseCase, FigClass, etc)
     * @param tagname the name of the tag you want to get.
     * @return A string value containing the tagged value.
     */
    public static String getMemberTaggedValue(Fig fig, String tagname) {
        String desc = null;
        desc = Model.getFacade().getTaggedValueValue(fig.getOwner(), tagname);
        return desc;
    }

    /**
     * This method gets tagged values for the current namespace used by the
     * project. These tagged values typically include the "author" and project
     * description.
     * 
     * @param project The current project.
     * @param tagname The name of the tag.
     * @return A string containing the tagged value
     */
    public static String getTaggedValue(Project project, String tagname) {
        String val = null;
        val = Model.getFacade().getTaggedValueValue(
                project.getCurrentNamespace(), tagname);
        return val;
    }

    /**
     * This is a convenience method for returning the project author name.
     * 
     * @param project The current project
     * @return A string containing the author name
     */
    public static String getAuthor(Project project) {
        return getTaggedValue(project, "author");
    }

    /**
     * This is a convenience method for returning the project description.
     * 
     * @param project The current project.
     * @return A string containing the project description.
     */
    public static String getProjectDescription(Project project) {
        return getTaggedValue(project, "documentation");
    }
    
    public static String getProjectName(Project project){
        return Model.getFacade().getName(project.getCurrentNamespace());
    }

    /**
     * This method gets the diagram as an SVG snippet.
     * 
     * @param diagram The diagram.
     * @return An SVG document containing the diagram.
     */
    public static String getDiagramAsSVG(ArgoDiagram diagram) {

        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        Editor ce = Globals.curEditor();
        JComponent canvas = ce.getJComponent();

        Layer diagLayer = diagram.getLayer();
        Editor diagEditor = new Editor(diagram.getGraphModel(), canvas,
                diagLayer);

        Rectangle drawingArea = diagEditor.getLayerManager().getActiveLayer()
                .calcDrawingArea();// ce.getLayerManager().getActiveLayer().calcDrawingArea();
        try {
            SvgWriter writer = new SvgWriter(buff, drawingArea, true);
            if (writer != null) {
                diagEditor.print(writer);
                writer.dispose();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return buff.toString();
    }

    /**
     * This method gets the diagram as a JPEG bytearray and Base64 encodes it so
     * that it can be embedded into HTML, or XML-based file formats that support
     * Base64 encoded images.
     * 
     * @param diagram The diagram to be converted.
     * @return A Base64 encoded JPEG byte array
     */
    public static String getDiagramAsJPEG(ArgoDiagram diagram) {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        Editor ce = Globals.curEditor();
        JComponent canvas = ce.getJComponent();

        Layer diagLayer = diagram.getLayer();
        Editor diagEditor = new Editor(diagram.getGraphModel(), canvas,
                diagLayer);

        Rectangle drawingArea = diagEditor.getLayerManager().getActiveLayer()
                .calcDrawingArea();
        try {
            // Create an offscreen image and render the diagram into it.
            BufferedImage i = new BufferedImage(drawingArea.width,
                    drawingArea.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) i.getGraphics();
            // g.setColor(new Color(SavePNGAction.TRANSPARENT_BG_COLOR, true));

            Composite c = g.getComposite();
            g.setComposite(AlphaComposite.Src);
            g.fillRect(0, 0, drawingArea.width, drawingArea.height);
            g.setComposite(c);
            // a little extra won't hurt
            g.translate(-drawingArea.x, -drawingArea.y);
            diagEditor.print(g);

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(buff);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(i);

            param.setQuality(1.0f, false);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(i);

            g.dispose();
            // force garbage collection, to prevent out of memory exceptions
            g = null;
            i = null;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        BASE64Encoder encoder = new BASE64Encoder();
        String encImage = encoder.encode(buff.toByteArray());

        return encImage;
    }

    /**
     * This method gets the diagram dimensions.
     * 
     * @param diagram The diagram of interest.
     * @return A Rectangle representing the drawing area of the diagram.
     */
    public static Rectangle getDiagramDim(ArgoDiagram diagram) {

        Editor ce = Globals.curEditor();
        JComponent canvas = ce.getJComponent();

        Layer diagLayer = diagram.getLayer();
        Editor diagEditor = new Editor(diagram.getGraphModel(), canvas,
                diagLayer);

        return diagEditor.getLayerManager().getActiveLayer().calcDrawingArea();
    }

    /**
     * This method gets the use case diagrams for a particular project.
     * 
     * @param project The argo project.
     * @return List of UMLUseCaseDiagram objects.
     */
    public static List<ArgoDiagram> getUseCaseDiagrams(Project project) {
        return getDiagramsByType(project, UMLUseCaseDiagram.class);
    }

    /**
     * This method gets the class diagrams for a particular project.
     * 
     * @param project The argo project.
     * @return List of UMLClassDiagram objects.
     */
    public static List<ArgoDiagram> getClassDiagrams(Project project) {
        return getDiagramsByType(project, UMLClassDiagram.class);
    }

    /**
     * This method gets the collaboration diagrams for a particular project.
     * 
     * @param project The argo project.
     * @return List of UMLCollaborationDiagram objects.
     */
    public static List<ArgoDiagram> getCollaborationDiagrams(Project project) {
        return getDiagramsByType(project, UMLCollaborationDiagram.class);
    }

    /**
     * This method gets the deployment diagrams for a particular project.
     * 
     * @param project The argo project.
     * @return List of UMLDeploymentDiagram objects.
     */
    public static List<ArgoDiagram> getDeploymentDiagrams(Project project) {
        return getDiagramsByType(project, UMLDeploymentDiagram.class);
    }

    /**
     * Gets the components found in a deployment diagram.
     * 
     * @param diagram
     * @return
     */
    public static List<Fig> getComponents(ArgoDiagram diagram) {
        return getMembersByType(diagram, FigComponent.class);
    }

    /**
     * This method gets the sequence diagrams for a particular project.
     * 
     * @param project The argo project.
     * @return List of UMLSequenceDiagram objects.
     */
    public static List<ArgoDiagram> getSequenceDiagrams(Project project) {
        return getDiagramsByType(project, UMLSequenceDiagram.class);
    }

    /**
     * Gets the classifiers from a sequence diagram.
     * 
     * @param diagram The sequence diagram whose classifiers you want.
     * @return A list of FigClassifierRole objects.
     */
    public static List<Fig> getSequenceClassifiers(ArgoDiagram diagram) {
        return getMembersByType(diagram, FigClassifierRole.class);
    }

    /**
     * This method gets the state diagrams for a particular project.
     * 
     * @param project The argo project.
     * @return List of UMLStateDiagram objects.
     */
    public static List<ArgoDiagram> getStateDiagrams(Project project) {
        return getDiagramsByType(project, UMLStateDiagram.class);
    }

    /**
     * Gets the states found in a State diagram.
     * 
     * @param diagram the State diagram.
     * @return a list of FigState objects representing the states in the
     *         diagram.
     */
    public static List<Fig> getStates(ArgoDiagram diagram) {
        return getMembersByType(diagram, FigState.class);
    }

    /**
     * This method gets the use cases for a use case diagram.
     * 
     * @param diagram The use case diagram.
     * @return A list of FigUseCases
     */
    public static List<Fig> getUseCases(ArgoDiagram diagram) {
        return getMembersByType(diagram, FigUseCase.class);
    }

    /**
     * This method gets the classes for a class diagram.
     * 
     * @param diagram The class diagram.
     * @return A list of FigClasses
     */
    public static List<Fig> getClasses(ArgoDiagram diagram) {
        return getMembersByType(diagram, FigClass.class);
    }
    
    public static Map<String, String> getMethods(FigInterface figClass){
        Map<String, String> methodMap = new HashMap<String, String>();
        List methodList = Model.getFacade().getOperations(figClass.getOwner());
        Iterator methodIt = methodList.iterator();
        Object currMethod = null;
        
        while(methodIt.hasNext()){
            currMethod = methodIt.next(); 
            String doc = "";
            String name = Model.getFacade().getName(currMethod);
            Object taggedValue = Model.getFacade().getTaggedValue(currMethod, Argo.DOCUMENTATION_TAG);
            
            if (taggedValue != null) {
                doc = Model.getFacade().getValueOfTag(taggedValue);
                if (doc != null){
                    methodMap.put(name, doc);
                }else {
                    methodMap.put(name, "");
                }
            }
            
        }
        return methodMap;
    }
    
    public static Map<String, String> getMethods(FigClass figClass){
        Map<String, String> methodMap = new HashMap<String, String>();
        List methodList = Model.getFacade().getOperations(figClass.getOwner());
        Iterator methodIt = methodList.iterator();
        Object currMethod = null;
        
        while(methodIt.hasNext()){
            currMethod = methodIt.next(); 
            String doc = "";
            String name = Model.getFacade().getName(currMethod);
            Object taggedValue = Model.getFacade().getTaggedValue(currMethod, Argo.DOCUMENTATION_TAG);
            
            if (taggedValue != null) {
                doc = Model.getFacade().getValueOfTag(taggedValue);
                if (doc != null){
                    methodMap.put(name, doc);
                }else {
                    methodMap.put(name, "");
                }
            }
            
        }
        return methodMap;
    }
    
    /**
     * This method gets the attributes and their descriptions
     * for a specific class.
     * @param figClass  The class you want to examine.
     * @return  A Map of class attributes where the keys are the attribute names
     *          and the values are the attribute descriptions.
     */
    public static Map<String, String> getAttributes(FigClass figClass){
        Map<String, String> attrMap = new HashMap<String, String>();
        List attrList = Model.getFacade().getAttributes(figClass.getOwner());
        Iterator attrIt = attrList.iterator();
        Object currAttr = null;
        
        while(attrIt.hasNext()){
            currAttr = attrIt.next(); 
            String doc = "";
            String name = Model.getFacade().getName(currAttr);
            Object taggedValue = Model.getFacade().getTaggedValue(currAttr, Argo.DOCUMENTATION_TAG);
            
            if (taggedValue != null) {
                doc = Model.getFacade().getValueOfTag(taggedValue);
                if (doc != null){
                    attrMap.put(name, doc);
                }else {
                    attrMap.put(name, "");
                }
            }
            
        }
        return attrMap;
    }

    /**
     * This method gets the interfaces for a class diagram.
     * 
     * @param diagram The class diagram.
     * @return A list of FigInterfaces
     */
    public static List<Fig> getInterfaces(ArgoDiagram diagram) {
        return getMembersByType(diagram, FigInterface.class);
    }

}
