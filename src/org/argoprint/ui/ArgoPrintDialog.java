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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;


import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import javax.xml.parsers.DocumentBuilderFactory;

import org.argoprint.ArgoPrintEditorModel;
import org.argoprint.ArgoPrintManagerModel;
import org.argoprint.ui.ArgoPrintEditor;

import org.w3c.dom.Document;

public class ArgoPrintDialog extends JDialog {

    private ArgoPrintManagerModel manager;
    private ArgoPrintEditorModel editor;

    private AbstractAction
	actionCloseDialog;

    private static ArgoPrintDialog instance;

    private ArgoPrintDialog(Frame parent) {
	super(parent, true);
	setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

	manager = new ArgoPrintManagerModel();
	editor = new ArgoPrintEditorModel();

	initActions();
	initComponents();

	manager.loadData(new File("managerdata.xml"));
    }

    private void initActions() {
	actionCloseDialog = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    // TODO: determine location from resource
		    manager.saveData(new java.io.File("managerdata.xml"));
		    setVisible(false);
		}
	    };
	actionCloseDialog.putValue(AbstractAction.NAME, "Close");
	actionCloseDialog.putValue(AbstractAction.SHORT_DESCRIPTION,
				   "Close the dialog.");
    }

    private void initComponents() {
	setTitle("ArgoPrint");
	setPreferredSize(new Dimension(640, 480));

	JPanel panel = new JPanel();
	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	panel.setLayout(new BorderLayout());

	JTabbedPane paneTabs = new JTabbedPane(JTabbedPane.TOP);
	paneTabs.addTab("Manager", new ArgoPrintManager(manager));
	paneTabs.addTab("Editor", new ArgoPrintEditor(editor));
	panel.add(paneTabs, BorderLayout.CENTER);

	JPanel paneButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	paneButtons.add(new JButton(actionCloseDialog));
	panel.add(paneButtons, BorderLayout.SOUTH);

	add(panel);
	pack();
    }

    public static ArgoPrintDialog getInstance(Frame parent) {
	if (instance == null)
	    instance = new ArgoPrintDialog(parent);
	
	return instance;
    }
}
