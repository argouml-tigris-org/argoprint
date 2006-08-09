// $Id$
// Copyright (c) 2006 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
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

package org.argoprint.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.argoprint.GuidedEditing;

import org.w3c.dom.Element;
import org.w3c.dom.NameList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class DocumentJTree 
    extends JTree {

    private ContextMenu contextMenu;

    private AbstractAction
	actionRecursiveCollapse,
	actionRecursiveExpand;

    private class ContextMenu
	extends JPopupMenu {

	private AbstractAction
	    actionAppendAttribute,
	    actionAppendChild,
	    actionInsertSiblingBefore,
	    actionInsertSiblingAfter,
	    actionRemoveSubTree;

	private JMenu
	    menuAppendAttribute,
	    menuAppendChild,
	    menuInsertAfter,
	    menuInsertBefore;

	public ContextMenu() {
	    initializeActions();
	    initializeComponents();
	}

	private void initializeComponents() {
	    menuAppendAttribute = new JMenu("Append attribute");
	    menuAppendChild = new JMenu("Append child");
	    menuInsertAfter = new JMenu("Insert after");
	    menuInsertBefore = new JMenu("Insert before");


	    add(menuAppendAttribute);
 	    add(menuAppendChild);
	    add(menuInsertBefore);
	    add(menuInsertAfter);

	    add(actionRemoveSubTree);
	    add(actionRecursiveCollapse);
	    add(actionRecursiveExpand);

	    setDragEnabled(true);
	}

	private void initializeActions() {
	    actionRemoveSubTree = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
			((DocumentTreeModel) treeModel)
			    .removeSubTree(getSelectionPath());
		    }
		};
	    actionRemoveSubTree
		.putValue(AbstractAction.NAME, "Remove");

	    actionAppendChild = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
			((DocumentTreeModel) treeModel)
			    .appendChild(getSelectionPath(),
					 
					 ((JMenuItem) e
					  .getSource()).getText());

			expandPath(getSelectionPath());
		    }
		};
	    actionAppendChild
		.putValue(AbstractAction.NAME, "Append Child");

	    actionAppendAttribute = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
			((DocumentTreeModel) treeModel)
			    .appendAttribute(getSelectionPath(),

					     ((JMenuItem) e.getSource())
					     .getText());

			expandPath(getSelectionPath());
		    }
		};
	    actionAppendAttribute
		.putValue(AbstractAction.NAME, "Append Attribute");

	    actionInsertSiblingBefore = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
			((DocumentTreeModel) treeModel)
			    .insertSiblingBefore(getSelectionPath(),

						 ((JMenuItem) e.getSource())
						 .getText());
		    }
		};
	    actionInsertSiblingBefore
		.putValue(AbstractAction.NAME, "Insert Before");

	    actionInsertSiblingAfter = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
			((DocumentTreeModel) treeModel)
			    .insertSiblingAfter(getSelectionPath(),
						((JMenuItem) e.getSource())
						.getText());
		    }
		};
	    actionInsertSiblingAfter
		.putValue(AbstractAction.NAME, "Insert After");

	    actionRecursiveCollapse = new AbstractAction() {
		    private void collapse(Object node, TreePath path) {
			TreeModel model = treeModel;
			if (!model.isLeaf(node)) {
			    for (int i = 0; i < model.getChildCount(node); i++) {
				Object child = model.getChild(node, i);
				collapse(child, path.pathByAddingChild(child));
			    }
			    collapsePath(path);
			}
		    }		    

		    public void actionPerformed(ActionEvent e) {
			collapse(getLastSelectedPathComponent(),
				 getSelectionPath());
		    }
		};
	    actionRecursiveCollapse
		.putValue(AbstractAction.NAME, "Collapse");

	    actionRecursiveExpand = new AbstractAction() {
		    private void expand(Object node, TreePath path) {
			TreeModel model = treeModel;
			if (!model.isLeaf(node)) {
			    for (int i = 0; i < model.getChildCount(node); i++) {
				Object child = model.getChild(node, i);
				expand(child, path.pathByAddingChild(child));
			    }
			    expandPath(path);
			}
		    }
		    public void actionPerformed(ActionEvent e) {
			expand(getLastSelectedPathComponent(),
			       getSelectionPath());
		    }
		};
	    actionRecursiveExpand
		.putValue(AbstractAction.NAME, "Expand");

	}

	public void update() {   
	    Node selection = (Node) getLastSelectedPathComponent();
	    NameList names;
	    
	    if ((selection instanceof Element)
		&& GuidedEditing.knows(selection)) {

		menuAppendAttribute.removeAll();
		names = GuidedEditing
		    .getAllowedAttributes( (Element) selection );
		NamedNodeMap presentAtts = selection.getAttributes();
		for (int i = 0; i < names.getLength(); i++)
		    if (presentAtts.getNamedItem(names.getName(i)) == null) {
			StringBuffer name = new StringBuffer();
			String prefix;

			if ( (prefix = selection
			      .lookupPrefix(names.getNamespaceURI(i)))
			     != null) {

			    name.append(prefix);
			    name.append(":");
			}

			name.append(names.getName(i));

			menuAppendAttribute
			    .add(name.toString())
			    .addActionListener(actionAppendAttribute);
		    }

		menuAppendChild.removeAll();
		names = GuidedEditing
		    .getAllowedChildren((Element) selection);
		for (int i = 0; i < names.getLength(); i++)
		    menuAppendChild
			.add(names.getName(i))
			.addActionListener(actionAppendChild);
		
		menuInsertBefore.removeAll();
		names = GuidedEditing
		    .getAllowedPreviousSiblings((Element) selection);
		for (int i = 0; i < names.getLength(); i++)
		    menuInsertBefore
			.add(names.getName(i))
			.addActionListener(actionInsertSiblingBefore);
		
		menuInsertAfter.removeAll();
		names = GuidedEditing
		    .getAllowedNextSiblings((Element) selection);
		for (int i = 0; i < names.getLength(); i++)
		    menuInsertAfter
			.add(names.getName(i))
			.addActionListener(actionInsertSiblingAfter);

		menuAppendAttribute.setVisible(true);
		menuAppendChild.setVisible(true);
		menuInsertBefore.setVisible(true);
		menuInsertAfter.setVisible(true);
	    } else {
		menuAppendAttribute.setVisible(false);
		menuAppendChild.setVisible(false);
		menuInsertBefore.setVisible(false);
		menuInsertAfter.setVisible(false);
	    }
	    
	    if (treeModel.isLeaf(selection)) {
		actionRecursiveCollapse.setEnabled(false);
		actionRecursiveExpand.setEnabled(false);
	    } else {
		actionRecursiveCollapse.setEnabled(true);
		actionRecursiveExpand.setEnabled(true);
	    }
	}
    }

    public DocumentJTree(DocumentTreeModel model) {
	super();

	setModel(model);

	initializeComponents();
    }

    private void initializeComponents() {
	setCellRenderer(new DocumentTreeCellRenderer());
	setCellEditor(new DocumentTreeCellEditor());
	setEditable(true);

	contextMenu = new ContextMenu();

	addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    int selectedRow = getRowForLocation(e.getX(), e.getY());
		    if (selectedRow != -1) {
			setSelectionRow(selectedRow);
			if (e.getButton() == MouseEvent.BUTTON3) {
			    contextMenu.update();
			    contextMenu.show((Component) e.getSource(), e.getX(), e.getY());
			}
		    }
		}
	    });
    }
}
