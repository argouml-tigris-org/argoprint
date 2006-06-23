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
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.argoprint.ArgoPrintEditorModel;
import org.argoprint.DocumentSourceEvent;
import org.argoprint.DocumentSourceListener;

public class ArgoPrintEditor
    extends JPanel {
//     implements DocumentSourceListener {

    // TODO - move into configuration manager
    private static final URL ICON_DIR = ClassLoader
	.getSystemResource("org/argouml/Images/plaf/javax/swing/plaf/metal/" +
			   "MetalLookAndFeel/toolbarButtonGraphics/general/");

    private static final URL ICON_NEW;
    private static final URL ICON_OPEN; 
    private static final URL ICON_SAVE; 

    private static URL urlWrap(URL context, String spec) {
	URL result = null;
	try {
	    result = new URL(context, spec);
	} catch (java.net.MalformedURLException ex) {
	    // TODO
	}
	return result;
    }
    
    static {
	ICON_NEW  = urlWrap(ICON_DIR, "New.gif");
	ICON_OPEN = urlWrap(ICON_DIR, "OpenProject.gif");
	ICON_SAVE = urlWrap(ICON_DIR, "SaveProject.gif");
    }

    private ArgoPrintEditorModel model;
    private AbstractAction
	actionNew,
	actionOpen,
	actionSave,

	actionRemoveSubtree;

    

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
						      "Are you sure you want to discard the current document?"
						      ,"Question"
						      ,JOptionPane.YES_NO_OPTION)
			== JOptionPane.YES_OPTION) {
			model.newDocument();
		    }
		}
	    };
	actionNew.putValue(AbstractAction.SMALL_ICON, new ImageIcon(ArgoPrintEditor.ICON_NEW));

	actionOpen = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			try {
			    model.loadDocument(chooser.getSelectedFile());
			} catch (java.io.IOException ex) {
			    JOptionPane
				.showMessageDialog(parent, "Unable to load the specified file.", "Error",
						   JOptionPane.ERROR_MESSAGE);
			} catch (org.xml.sax.SAXException ex) {
			    JOptionPane
				.showMessageDialog(parent, "The specified file is not valid.", "Error",
						   JOptionPane.ERROR_MESSAGE);
			}
		    }
		}
	    };
	actionOpen.putValue(AbstractAction.SMALL_ICON, new ImageIcon(ArgoPrintEditor.ICON_OPEN));

	actionSave = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)
			model.saveDocument(chooser.getSelectedFile());
		}
	    };
	actionSave.putValue(AbstractAction.SMALL_ICON, new ImageIcon(ArgoPrintEditor.ICON_SAVE));

	actionRemoveSubtree = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		}
	    };
	actionRemoveSubtree.putValue(AbstractAction.NAME, "Remove subtree");
    }

    private void initializeComponents() {
	setLayout(new BorderLayout());

	JToolBar toolbar = new JToolBar();
	toolbar.add(new JButton(actionNew));
	toolbar.add(new JButton(actionOpen));
	toolbar.add(new JButton(actionSave));
	toolbar.addSeparator();
	toolbar.add(new JButton("Context"));
	add(toolbar, BorderLayout.NORTH);

	JTabbedPane tabbedpane = new JTabbedPane(JTabbedPane.BOTTOM);
	DocumentTreeModel treeModel = new DocumentTreeModel(model);
	model.addDocumentSourceListener(treeModel);

	tabbedpane.addTab("Tree",
			  new JScrollPane(new DocumentJTree(treeModel)));
	tabbedpane.addTab("Raw",
			  new JPanel());
	tabbedpane.addTab("Preview",
			  new JPanel());
	add(tabbedpane, BorderLayout.CENTER);
    }
}
