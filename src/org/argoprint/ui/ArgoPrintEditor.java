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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.argoprint.ArgoPrintEditorModel;
import org.argoprint.APResources;

public class ArgoPrintEditor
    extends JPanel {
//     implements DocumentSourceListener {

    private ArgoPrintEditorModel model;
    private AbstractAction
	actionNew,
	actionOpen,
	actionSave,
	actionHighlight,
	actionClearXPath,
	actionRemoveSubtree;

    private DocumentJTree treeDocument;

    public ArgoPrintEditor(ArgoPrintEditorModel model) {
	super();

	this.model = model;
	initializeActions();
	initializeComponents();
    }

    private void initializeActions() {
	final JPanel parent = this;

	actionNew = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    if (JOptionPane.showConfirmDialog(parent,
						      "Are you sure you want to discard the current document?",
						      "Question",
						      JOptionPane.YES_NO_OPTION)
			== JOptionPane.YES_OPTION) {
			model.newDocument();
		    }
		}
	    };
	actionNew.putValue(AbstractAction.SMALL_ICON,
			   new ImageIcon(APResources
					 .getResource(APResources.ICON_NEW)));

	actionOpen = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    if (chooser.showOpenDialog(parent)
			== JFileChooser.APPROVE_OPTION) {
			try {
			    model.loadDocument(chooser.getSelectedFile());
			} catch (java.io.IOException ex) {
			    JOptionPane
				.showMessageDialog(parent,
						   "Unable to load the specified file.", "Error",
						   JOptionPane.ERROR_MESSAGE);
			} catch (org.xml.sax.SAXException ex) {
			    JOptionPane
				.showMessageDialog(parent,
						   "The specified file is not valid.", "Error",
						   JOptionPane.ERROR_MESSAGE);
			}
		    }
		}
	    };
	actionOpen.putValue(AbstractAction.SMALL_ICON,
			    new ImageIcon(APResources
					  .getResource(APResources.ICON_OPEN)));

	actionSave = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    if (chooser.showSaveDialog(parent)
			== JFileChooser.APPROVE_OPTION)
			model.saveDocument(chooser.getSelectedFile());
		}
	    };
	actionSave.putValue(AbstractAction.SMALL_ICON,
			    new ImageIcon(APResources
					  .getResource(APResources.ICON_SAVE)));

	actionHighlight = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    treeDocument.highlightXPathNodes(((JTextField) e.getSource())
						     .getText());
		}
	    };
	actionHighlight.putValue(AbstractAction.NAME,
				 "Hightlight");

	actionClearXPath = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    treeDocument.clearXPathNodes();
		}
	    };
	actionClearXPath.putValue(AbstractAction.NAME,
				 "Clear");
	actionClearXPath.putValue(AbstractAction.SHORT_DESCRIPTION,
				 "Removes the highlighting that may be present.");

	actionRemoveSubtree = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		}
	    };
	actionRemoveSubtree.putValue(AbstractAction.NAME, "Remove subtree");
    }

    private void initializeComponents() {
	setLayout(new BorderLayout());

	JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	add(toolbarPanel, BorderLayout.NORTH);

	JToolBar fileToolbar = new JToolBar();
	fileToolbar.add(new JButton(actionNew));
	fileToolbar.add(new JButton(actionOpen));
	fileToolbar.add(new JButton(actionSave));
	fileToolbar.addSeparator();
	toolbarPanel.add(fileToolbar, BorderLayout.NORTH);

	JToolBar xpathToolbar = new JToolBar();
	JTextField field =
	    new JTextField(25);

	field.addActionListener(actionHighlight);
	xpathToolbar.add(new JLabel("XPath:"));
	// is this solution dependent on the L&F impl.?
	xpathToolbar.addSeparator();
	xpathToolbar.add(field);
	xpathToolbar.add(actionClearXPath);
	toolbarPanel.add(xpathToolbar, BorderLayout.NORTH);

// 	JToolBar xslToolbar = new JToolBar();
// 	xslToolbar.add(new JButton("template"));
// 	xslToolbar.add(new JButton("parameter"));
// 	toolbarPanel.add(xslToolbar, BorderLayout.NORTH);

	JTabbedPane tabbedpane = new JTabbedPane(JTabbedPane.BOTTOM);
	DocumentTreeModel treeModel = new DocumentTreeModel(model);
	model.addDocumentSourceListener(treeModel);

	treeDocument = new DocumentJTree(treeModel);

	tabbedpane.addTab("Tree",
			  new JScrollPane(treeDocument));
	tabbedpane.addTab("Raw",
			  new JPanel());
	tabbedpane.addTab("Preview",
			  new JPanel());
	add(tabbedpane, BorderLayout.CENTER);
    }
}
