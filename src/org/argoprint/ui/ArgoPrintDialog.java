// $Id$
// Copyright (c) 2006 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.argoprint.ArgoPrint;

import org.argouml.i18n.Translator;
import org.argouml.persistence.ArgoPrintInsider;

/**
 * The dialog displayed when ArgoPrint is started from the ArgoUML menu.
 */

public class ArgoPrintDialog
    extends JDialog {

    private static final Dimension DEFAULT_SIZE =
	 new Dimension(640, 480);

    public ArgoPrintDialog(Frame parent, String title, boolean modal) {
	super(parent, title, modal);
	initComponents();
    }

    private void initComponents() {
	setSize(DEFAULT_SIZE);
	add(ArgoPrintDialog.createSimpleDialogContent());
    }

    /*
     * Create a panel as described by the use-case in the old documentation.
     */
    private static JPanel createSimpleDialogContent() {
	JPanel result = new JPanel(new GridBagLayout());
	result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	GridBagConstraints c = new GridBagConstraints();

	final JTextField template, output;

	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 0;
	c.gridwidth = 1;
	result.add(new JLabel(Translator.localize("argoprint.template.label")), c);

	c.gridx = 1;
	result.add(template = new JTextField(20), c);

	c.gridx = 0;
	c.gridy = 1;
	result.add(new JLabel(Translator.localize("argoprint.output.label")), c);

	c.gridx = 1;
	result.add(output = new JTextField(20), c);

	c.gridx = 1;
	c.gridy = 2;
	result.add(new JButton(new AbstractAction(Translator.localize("argoprint.execute.button")) {
		public void actionPerformed(ActionEvent e) {
		    try {
			InputStream streamTemplate = new FileInputStream(template.getText());
			OutputStream streamOutput = new FileOutputStream(output.getText());

			ArgoPrint.generate(streamTemplate,
					   streamOutput);
		    } catch (FileNotFoundException ex) {
			//TODO:
		    }
		}
	    }), c);

	return result;
    }
}
