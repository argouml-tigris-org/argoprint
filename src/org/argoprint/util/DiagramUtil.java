/**
 * 
 */
package org.argoprint.util;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.model.Model;
import org.argouml.sequence2.diagram.UMLSequenceDiagram;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.DiagramUtils;
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
    private static final Logger LOG =
        Logger.getLogger(DiagramUtil.class);
    
    
	public static final String USE_CASE_DIAGRAM = "UseCaseDiagram";
	public static final String CLASS_DIAGRAM = "ClassDiagram";
	public static final String DEPLOYMENT_DIAGRAM = "DeploymentDiagram";
	public static final String COLLABORATION_DIAGRAM = "CollaborationDiagram";
	public static final String SEQUENCE_DIAGRAM = "SequenceDiagram";
	public static final String STATE_DIAGRAM = "StateDiagram";

	/**
	 * This method determines what the type of diagram.
	 * 
	 * @param diagram
	 *            The diagram whose type you want.
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
	 * @param project
	 *            The top-level project object.
	 * @param diagramType
	 *            The type of diagram (use one of the constants for this)
	 * @return A List of ArgoDiagrams
	 */
	public static List<ArgoDiagram> getDiagramsByType(Project project,
			final String diagramType) {
		System.out.println("diagramtype = " + diagramType);

		List<ArgoDiagram> diagramList = new ArrayList<ArgoDiagram>();

		Iterator diagIt = new FilterIterator(project.getDiagramList().iterator(),
				new Predicate() {

					public boolean evaluate(Object diagram) {
						ArgoDiagram diag = (ArgoDiagram) diagram;
						return diagramType.equals(getDiagramType(diag));
					}

				});
		ArgoDiagram diag = null;
		while (diagIt.hasNext()) {
			diagramList.add((ArgoDiagram) diagIt.next());
		}

		return diagramList;
	}

	/**
	 * This method gets diagrams by a particular diagram type.
	 * 
	 * @param project
	 *            The Argo project.
	 * @param diagramType
	 *            The diagram class. Should be an implementation of ArgoDiagram.
	 * @return A list of ArgoDiagrams of a given type.
	 */
	public static List<ArgoDiagram> getDiagramsByType(Project project,
			final Class diagramType) {

		List<ArgoDiagram> diagramList = new ArrayList<ArgoDiagram>();

		Iterator rawIt = project.getDiagramList().iterator();
		Iterator diagIt = new FilterIterator(rawIt, new Predicate() {

			public boolean evaluate(Object diagram) {
				ArgoDiagram diag = (ArgoDiagram) diagram;
				return diag.getClass() == diagramType;
			}

		});
		while (diagIt.hasNext()) {
			diagramList.add((ArgoDiagram) diagIt.next());
		}

		return diagramList;
	}

	/**
	 * This method gets the members of a particular diagram. For example, get
	 * the use cases found on a use case diagram.
	 * 
	 * @param diagram
	 *            The diagram being interrogated.
	 * @param memberType
	 *            The member class (an implementation of the Fig interface).
	 * @return A list of members.
	 */
	public static List<Fig> getMembersByType(ArgoDiagram diagram,
			final Class memberType) {

		List<Fig> memberList = new ArrayList<Fig>();

		Iterator rawIt = diagram.getFigIterator();
		Iterator diagIt = new FilterIterator(rawIt, new Predicate() {

			public boolean evaluate(Object diagram) {
				Fig diag = (Fig) diagram;
				return diag.getClass() == memberType;
			}

		});
		while (diagIt.hasNext()) {
			memberList.add((Fig) diagIt.next());
		}

		return memberList;
	}
	
	/**
	 * This method gets a description of the member.
	 * @param fig  The member of a diagram whose description you want.
	 * @return  The description of the member.
	 */
	public static String getMemberDescription(Fig fig){
		return getMemberTaggedValue(fig, "documentation");
	}
	
	/**
	 * This method gets a tagged value for a particular member of a diagram.
	 * @param fig		the member of a diagram (FigUseCase, FigClass, etc)
	 * @param tagname	the name of the tag you want to get.
	 * @return	A string value containing the tagged value.
	 */
	public static String getMemberTaggedValue(Fig fig, String tagname){
		String desc = null;
		desc = Model.getFacade().getTaggedValueValue(fig.getOwner(), tagname);
		return desc;		
	}
	
	/**
	 * This method gets the diagram as an SVG snippet.
	 * @param diagram  The diagram.
	 * @return	An SVG document containing the diagram.
	 */
	public static String getDiagramAsSVG(ArgoDiagram diagram){
	    // this is ugly, but it is the only way for now.
	    // we need to simulate that the diagram is active,
	    // then we save our graphics, and lately we leave all as it was
	    // before.
	    final ArgoDiagram activeDiagram = DiagramUtils.getActiveDiagram();
	    TargetManager.getInstance().setTarget(diagram);
    
	    ByteArrayOutputStream buff = new ByteArrayOutputStream();
	    Editor ce = Globals.curEditor();
	    Layer layer = ce.getLayerManager().getActiveLayer();
	    Rectangle drawingArea = layer.calcDrawingArea();
	    try {
	        SvgWriter writer = new SvgWriter(buff, drawingArea, true);
	        if (writer != null) {
	            ce.print(writer);
	            writer.dispose();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    TargetManager.getInstance().setTarget(activeDiagram);
	    return buff.toString();
	}

	/**
	 * This method gets the use case diagrams for a particular project.
	 * 
	 * @param project
	 *            The argo project.
	 * @return List of UMLUseCaseDiagram objects.
	 */
	public static List<ArgoDiagram> getUseCaseDiagrams(Project project) {
		return getDiagramsByType(project, UMLUseCaseDiagram.class);
	}

	/**
	 * This method gets the class diagrams for a particular project.
	 * 
	 * @param project
	 *            The argo project.
	 * @return List of UMLClassDiagram objects.
	 */
	public static List<ArgoDiagram> getClassDiagrams(Project project) {
		return getDiagramsByType(project, UMLClassDiagram.class);
	}

	/**
	 * This method gets the collaboration diagrams for a particular project.
	 * 
	 * @param project
	 *            The argo project.
	 * @return List of UMLCollaborationDiagram objects.
	 */
	public static List<ArgoDiagram> getCollaborationDiagrams(Project project) {
		return getDiagramsByType(project, UMLCollaborationDiagram.class);
	}

	/**
	 * This method gets the deployment diagrams for a particular project.
	 * 
	 * @param project
	 *            The argo project.
	 * @return List of UMLDeploymentDiagram objects.
	 */
	public static List<ArgoDiagram> getDeploymentDiagrams(Project project) {
		return getDiagramsByType(project, UMLDeploymentDiagram.class);
	}
	
	/**
	 * Gets the components found in a deployment diagram.
	 * @param diagram
	 * @return
	 */
	public static List<Fig> getComponents(ArgoDiagram diagram){
		return getMembersByType(diagram, FigComponent.class);
	}

	/**
	 * This method gets the sequence diagrams for a particular project.
	 * 
	 * @param project
	 *            The argo project.
	 * @return List of UMLSequenceDiagram objects.
	 */
	public static List<ArgoDiagram> getSequenceDiagrams(Project project) {
		return getDiagramsByType(project, UMLSequenceDiagram.class);
	}
	
	/**
	 * Gets the classifiers from a sequence diagram.
	 * @param diagram  The sequence diagram whose classifiers you want.
	 * @return  A list of FigClassifierRole objects.
	 */
	public static List<Fig> getSequenceClassifiers(ArgoDiagram diagram){
		return getMembersByType(diagram, FigClassifierRole.class);
	}

	/**
	 * This method gets the state diagrams for a particular project.
	 * 
	 * @param project
	 *            The argo project.
	 * @return List of UMLStateDiagram objects.
	 */
	public static List<ArgoDiagram> getStateDiagrams(Project project) {
		return getDiagramsByType(project, UMLStateDiagram.class);
	}
	
	/**
	 * Gets the states found in a State diagram.
	 * @param diagram  the State diagram.
	 * @return	a list of FigState objects representing the states in the diagram.
	 */
	public static List<Fig> getStates(ArgoDiagram diagram){
		return getMembersByType(diagram, FigState.class);
	}

	/**
	 * This method gets the use cases for a use case diagram.
	 * 
	 * @param diagram
	 *            The use case diagram.
	 * @return A list of FigUseCases
	 */
	public static List<Fig> getUseCases(ArgoDiagram diagram) {
		return getMembersByType(diagram, FigUseCase.class);
	}

	/**
	 * This method gets the classes for a class diagram.
	 * 
	 * @param diagram
	 *            The class diagram.
	 * @return A list of FigClasses
	 */
	public static List<Fig> getClasses(ArgoDiagram diagram) {
		return getMembersByType(diagram, FigClass.class);
	}

	/**
	 * This method gets the interfaces for a class diagram.
	 * 
	 * @param diagram
	 *            The class diagram.
	 * @return A list of FigInterfaces
	 */
	public static List<Fig> getInterfaces(ArgoDiagram diagram) {
		return getMembersByType(diagram, FigInterface.class);
	}

}
