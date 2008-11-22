/**
 * 
 */
package org.argoprint.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.list.TypedList;
import org.argouml.model.Model;
import org.argouml.model.UseCasesHelper;
import org.argouml.uml.diagram.use_case.ui.FigUseCase;
import org.argouml.uml.diagram.use_case.ui.UMLUseCaseDiagram;
import org.tigris.gef.presentation.Fig;

/**
 * @author mfortner
 *
 */
public class UseCaseUtil {
	
	/**
	 * This method is responsible for getting the use cases found in a particular diagram.
	 * @param diagram
	 * @return
	 */
	public static List<FigUseCase> getUseCases(UMLUseCaseDiagram diagram){
		List<FigUseCase> useCaseList = TypedList.decorate(new ArrayList<FigUseCase>(), FigUseCase.class);
		UseCasesHelper helper = Model.getUseCasesHelper();
		Iterator<Fig> it = diagram.getFigIterator();
		Fig currFig = null;
		while(it.hasNext()){
			currFig = it.next();
			useCaseList.add((FigUseCase)currFig);
		}
		return useCaseList;
	}
	
	
	


}
