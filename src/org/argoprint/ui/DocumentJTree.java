// $Id DocumentJTree.java,v 0.1 2006/06/21 17:26 comp_ Exp $
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
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

class DocumentJTree 
    extends JTree {
//     implements TreeModelListener {

    private class ContextMenu
	extends JPopupMenu
	implements MouseListener {

	public void mouseClicked(MouseEvent e) {
	    int selectedRow = getRowForLocation(e.getX(), e.getY());
	    if ( selectedRow != -1) {
		// should not be used outside DocumentJTree
		((JTree)e.getSource()).setSelectionRow(selectedRow);
		if (e.getButton() == MouseEvent.BUTTON3) {
		    show((Component)e.getSource(), e.getX(), e.getY());
		}
	    }
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
    }

    private ContextMenu contextMenu;

    private AbstractAction
	actionAppendChild,
	actionInsertSiblingBefore,
	actionInsertSiblingAfter,
	actionRemoveSubTree;

    public DocumentJTree(DocumentTreeModel model) {
	super();

	setModel(model);
// 	model.addTreeModelListener(this);

	initializeComponents();
    }

    private void initializeActions() {
	actionRemoveSubTree = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    ((DocumentTreeModel)getModel()).removeSubTree(getSelectionPath());
		}
	    };

	actionRemoveSubTree.putValue(AbstractAction.NAME, "Remove SubTree");

	actionAppendChild = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    ((DocumentTreeModel)getModel()).appendChild(getSelectionPath(),"fix");
		    expandPath(getSelectionPath());
		}
	    };

	actionAppendChild.putValue(AbstractAction.NAME, "Append Child");

 	actionInsertSiblingBefore = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    ((DocumentTreeModel)getModel()).insertSiblingBefore(getSelectionPath(),"fix");
		}
	    };
	actionInsertSiblingBefore.putValue(AbstractAction.NAME, "Insert Before");

 	actionInsertSiblingAfter = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    ((DocumentTreeModel)getModel()).insertSiblingAfter(getSelectionPath(),"fix");
		}
	    };
	actionInsertSiblingAfter.putValue(AbstractAction.NAME, "Insert After");

    }

    private void initializeComponents() {
	initializeActions();

	setCellRenderer(new DocumentTreeCellRenderer());
	setCellEditor(new DocumentTreeCellEditor());
	setEditable(true);

	contextMenu = new ContextMenu();

	contextMenu.add(actionAppendChild);
	contextMenu.add(actionInsertSiblingBefore);
	contextMenu.add(actionInsertSiblingAfter);
	contextMenu.add(actionRemoveSubTree);


	addMouseListener(contextMenu);
    }

//     public void	treeNodesChanged(TreeModelEvent e) {
// 	revalidate();
//     }
//     public void treeNodesInserted(TreeModelEvent e) {
// 	revalidate();
//     }
//     public void	treeNodesRemoved(TreeModelEvent e) {
// 	revalidate();
//     }
//     public void	treeStructureChanged(TreeModelEvent e) {
// 	treeDidChange();
// // 	revalidate();
//     }
}
