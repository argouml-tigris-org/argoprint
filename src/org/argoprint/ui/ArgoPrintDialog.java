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
import java.awt.Insets;
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
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.argoprint.ArgoPrint;

import org.argouml.application.api.Configuration;
import org.argouml.application.api.ConfigurationKey;
import org.argouml.i18n.Translator;
import org.argouml.persistence.ArgoPrintInsider;

/**
 * The dialog displayed when ArgoPrint is started from the ArgoUML menu.
 */

public class ArgoPrintDialog
    extends JDialog {

    private static final Dimension DEFAULT_SIZE =
	 new Dimension(640, 480);

    private static final String prefix = "argoprint";
    private static final ConfigurationKey CONF_DEFAULT_TEMPLATE
	= Configuration.makeKey(prefix, "default_template");
    private static final ConfigurationKey CONF_DEFAULT_OUTPUT
	= Configuration.makeKey(prefix, "default_output");

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
	final JPanel result = new JPanel(new GridBagLayout());
	result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	GridBagConstraints c = new GridBagConstraints();

	final JTextField template, output;

	c.fill = GridBagConstraints.BOTH;
	c.insets = new Insets(0, 0, 3, 3);
	c.gridx = 0;
	c.gridy = 0;
	c.gridwidth = 1;
	c.weightx = 0;
	result.add(new JLabel(Translator.localize("argoprint.label.template")), c);

	c.gridx = 1;
	c.weightx = 1;
	result.add(template = new JTextField(20), c);
	template.setText(Configuration.getString(CONF_DEFAULT_TEMPLATE));

	c.gridx = 2;
	c.weightx = 0;
	result.add(new JButton(new AbstractAction(Translator.localize("argoprint.button.default")) {
		public void actionPerformed(ActionEvent e) {
		    Configuration.setString(CONF_DEFAULT_TEMPLATE, template.getText());
		    Configuration.save();
		}
	    }), c);
	
	c.gridx = 0;
	c.gridy = 1;
	c.weightx = 0;
	result.add(new JLabel(Translator.localize("argoprint.label.output")), c);

	c.gridx = 1;
	c.weightx = 1;
	result.add(output = new JTextField(20), c);
	output.setText(Configuration.getString(CONF_DEFAULT_OUTPUT));

	c.gridx = 2;
	c.weightx = 0;
	result.add(new JButton(new AbstractAction(Translator.localize("argoprint.button.default")) {
		public void actionPerformed(ActionEvent e) {
		    Configuration.setString(CONF_DEFAULT_OUTPUT, output.getText());
		    Configuration.save();
		}
	    }), c);

	c.gridx = 2;
	c.gridy = 2;
	c.weightx = 0;
	result.add(new JButton(new AbstractAction(Translator.localize("argoprint.button.execute")) {
		public void actionPerformed(ActionEvent e) {
		    InputStream streamTemplate = null;
		    OutputStream streamOutput = null;

		    try {
			streamTemplate = new FileInputStream(template.getText());
		    } catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(result,
						      Translator
						      .localize("argoprint.message.wrongTemplate"));
			return;
		    }
		    
		    try {
			streamOutput = new FileOutputStream(output.getText());
		    } catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(result,
						      Translator
						      .localize("argoprint.message.wrongOutput"));
			return;
		    }

		    // something smart to replace this?
		    final InputStream paramTemplate = streamTemplate;
		    final OutputStream paramOutput = streamOutput;

		    Thread t = new Thread() {
			    public void run() {
				try {
				    setEnabled(false);
				    ArgoPrint.generate(paramTemplate,
						       paramOutput);
				    JOptionPane.showMessageDialog(result,
								  Translator
								  .localize("argoprint.message.transformationDone"));
				} catch (javax.xml.transform.TransformerException ex) {
				    JOptionPane.showMessageDialog(result,
								  Translator
								  .localize("argoprint.message.transformationError"));
				    return;
				} finally {
				    setEnabled(true);
				}
				
			    }
			};
		    t.start();
		}
	    }), c);

	return result;
    }
}
