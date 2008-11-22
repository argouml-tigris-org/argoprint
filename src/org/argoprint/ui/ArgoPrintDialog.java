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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.argoprint.persistence.TemplateEngine;
import org.argoprint.persistence.TemplateEngineException;
import org.argoprint.persistence.TemplateEngineFactory;
import org.argoprint.persistence.TemplateEngineNotFoundException;
import org.argouml.configuration.Configuration;
import org.argouml.configuration.ConfigurationKey;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.util.ArgoFrame;

/**
 * The dialog displayed when ArgoPrint is started from the ArgoUML menu.
 */
public class ArgoPrintDialog extends JDialog {

	private static final Dimension DEFAULT_SIZE = new Dimension(640, 480);

	private static final String PREFIX = "argoprint";
	private static final ConfigurationKey CONF_DEFAULT_TEMPLATE = Configuration
			.makeKey(PREFIX, "default_template");
	private static final ConfigurationKey CONF_DEFAULT_OUTPUT = Configuration
			.makeKey(PREFIX, "default_output");

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
		result.add(new JLabel(Translator.localize("argoprint.label.template")),
				c);

		c.gridx = 1;
		c.weightx = 1;
		result.add(template = new JTextField(20), c);
		template.setText(Configuration.getString(CONF_DEFAULT_TEMPLATE));

		c.gridx = 2;
		c.weightx = 0;
		result.add(new JButton(new AbstractAction("...") {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				int retval = chooser.showOpenDialog(ArgoFrame.getInstance());
				if (retval == JFileChooser.APPROVE_OPTION) {
					template.setText(chooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		}), c);

		c.gridx = 3;
		c.weightx = 0;
		result.add(new JButton(new AbstractAction(Translator
				.localize("argoprint.button.default")) {
			public void actionPerformed(ActionEvent e) {
				Configuration.setString(CONF_DEFAULT_TEMPLATE, template
						.getText());
				Configuration.save();
			}
		}), c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		result
				.add(new JLabel(Translator.localize("argoprint.label.output")),
						c);

		c.gridx = 1;
		c.weightx = 1;
		result.add(output = new JTextField(20), c);
		output.setText(Configuration.getString(CONF_DEFAULT_OUTPUT));

		c.gridx = 2;
		c.weightx = 0;
		result.add(new JButton(new AbstractAction("...") {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				int retval = chooser.showOpenDialog(ArgoFrame.getInstance());
				if (retval == JFileChooser.APPROVE_OPTION) {
					output.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		}), c);

		c.gridx = 3;
		c.weightx = 0;
		result.add(new JButton(new AbstractAction(Translator
				.localize("argoprint.button.default")) {
			public void actionPerformed(ActionEvent e) {
				Configuration.setString(CONF_DEFAULT_OUTPUT, output.getText());
				Configuration.save();
			}
		}), c);

		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0;
		result.add(new JButton(new AbstractAction(Translator
				.localize("argoprint.button.execute")) {

			public void actionPerformed(ActionEvent e) {

				Thread t = new Thread() {
					public void run() {
						try {
							setEnabled(false);
							InputStream streamTemplate = null;
							OutputStream streamOutput = null;

							// verify that the template exists
							String templateFile = template.getText();
							String templateExt = null;
							
							String outputFile = output.getText();

							TemplateEngine templateEngine = null;

							
							// get the file extension and find the
							// appropriate template engine
							templateExt = templateFile.substring(templateFile
									.lastIndexOf(".") + 1);
							
							// verify that the template has been specified
							if (templateExt == null || templateExt == "") {
								throw new FileNotFoundException(
										"No template specified");
							}
							
							templateEngine = TemplateEngineFactory
									.getInstance(templateExt);

							if (templateEngine == null) {
								throw new TemplateEngineNotFoundException(
										"Unable to find a compatible template engine for template: "
												+ templateFile);
							}

							// verify that the template exists
							streamTemplate = new FileInputStream(templateFile);
							
							templateEngine.generate(ProjectManager.getManager().getCurrentProject(), outputFile, templateFile);

							// show "generation complete" message
							JOptionPane
									.showMessageDialog(
											result,
											Translator
													.localize("argoprint.message.transformationDone"));
						} catch (FileNotFoundException ex) {
							ex.printStackTrace();
							JOptionPane
									.showMessageDialog(
											result,
											Translator
													.localize("argoprint.message.wrongTemplate"));
							return;
						} catch (TemplateEngineNotFoundException te) {
							te.printStackTrace();
							JOptionPane
									.showMessageDialog(
											result,
											Translator
													.localize("argoprint.message.wrongTemplate"));
							return;

						} catch (TemplateEngineException ex) {
							ex.printStackTrace();
							JOptionPane
									.showMessageDialog(
											result,
											Translator
													.localize("argoprint.message.transformationError"));
							return;
						} catch (IOException e) {
							e.printStackTrace();
							JOptionPane
							.showMessageDialog(
									result,
									Translator
											.localize("argoprint.message.transformationError"));
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
