/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    phidias
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

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
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.argoprint.persistence.PostProcessor;
import org.argoprint.persistence.PostProcessorFactory;
import org.argoprint.persistence.PostProcessorNotFoundException;
import org.argoprint.persistence.TemplateEngine;
import org.argoprint.persistence.TemplateEngineException;
import org.argoprint.persistence.TemplateEngineFactory;
import org.argoprint.persistence.TemplateEngineNotFoundException;
import org.argoprint.util.FileUtil;
import org.argouml.configuration.Configuration;
import org.argouml.configuration.ConfigurationKey;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.util.ArgoFrame;

/**
 * The dialog displayed when ArgoPrint is started from the ArgoUML menu.
 */
public class ArgoPrintDialog extends JDialog {
    
    private static final Logger LOG = 
        Logger.getLogger(ArgoPrintDialog.class);


    /**
     * 
     */
    private static final long serialVersionUID = 2720567509558127637L;

    private static final String PREFIX = "argoprint";

    private static final ConfigurationKey CONF_DEFAULT_TEMPLATE = Configuration
            .makeKey(PREFIX, "default_template");

    private static final ConfigurationKey CONF_DEFAULT_OUTPUT = Configuration
            .makeKey(PREFIX, "default_output");
    
    JPanel panel = new JPanel();
    
    JTextField templateFld = new JTextField();
    JTextField outputFld = new JTextField();
    

    public ArgoPrintDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        initComponents();
    }

    @SuppressWarnings("serial")
    private void initComponents() {
        
        this.setSize(new Dimension(494, 265));
        
        this.setTitle(Translator
                .localize("argoprint.dialog.title"));
        this.setContentPane(panel);
        
        this.panel.setLayout(null);
        
        this.templateFld.setSize(new Dimension(305, 22));
        this.templateFld.setLocation(new Point(26, 61));
        
        this.outputFld.setSize(new Dimension(306, 22));
        this.outputFld.setLocation(new Point(25, 125));
        
        JButton closeBtn = new JButton();
        closeBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.close")) {
            public void actionPerformed(ActionEvent e) {
                ArgoPrintDialog.this.setVisible(false);
            };
        });
        closeBtn.setSize(new Dimension(115, 24));
        closeBtn.setLocation(new Point(195, 180));
        this.panel.add(closeBtn);
       

        JLabel dialogLbl = new JLabel(Translator
                .localize("argoprint.label.dialog"));
        dialogLbl.setBounds(new Rectangle(14, 14, 440, 22));
        Font font = dialogLbl.getFont();
        font = font.deriveFont(Font.BOLD,12.0f);
        dialogLbl.setFont(font);
        
        JLabel templateLbl = new JLabel(Translator
                .localize("argoprint.label.template"));
        templateLbl.setBounds(new Rectangle(25, 45, 100, 15));
        
        JButton templateBtn = new JButton();
        templateBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.browse")) {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                int retval = chooser.showOpenDialog(ArgoFrame.getFrame());
                if (retval == JFileChooser.APPROVE_OPTION) {
                    templateFld.setText(chooser.getSelectedFile()
                            .getAbsolutePath());
                }
            };
        });
        templateBtn.setSize(new Dimension(113, 23));
        templateBtn.setLocation(new Point(345, 60));
        
        JButton configBtn = new JButton(new AbstractAction(Translator
                .localize("argoprint.button.default")) {
            public void actionPerformed(ActionEvent e) {
                Configuration.setString(CONF_DEFAULT_TEMPLATE, templateFld
                        .getText());
                Configuration.setString(CONF_DEFAULT_OUTPUT, outputFld.getText());
                Configuration.save();
            }
        });
        this.panel.add(templateLbl, templateLbl.getName());
        this.panel.add(dialogLbl);
        this.panel.add(configBtn);
        this.panel.add(templateFld);
        this.panel.add(outputFld);
        
        JLabel outputLbl = new JLabel(Translator
                .localize("argoprint.label.output"));
        outputLbl.setBounds(new Rectangle(26, 107, 100, 15));
        this.panel.add(outputLbl, null);
        
        // set defaults
        this.outputFld.setText(Configuration.getString(CONF_DEFAULT_OUTPUT));
        this.templateFld.setText(Configuration.getString(CONF_DEFAULT_TEMPLATE));
        
        JButton outputBtn = new JButton();
        outputBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.browse")) {
            
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                int retval = chooser.showOpenDialog(ArgoFrame.getFrame());
                if (retval == JFileChooser.APPROVE_OPTION) {
                    outputFld.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        
        outputBtn.setLocation(new Point(345, 120));
        outputBtn.setSize(new Dimension(115, 24));
        
        this.panel.add(templateBtn, null);
        this.panel.add(outputBtn, null);
        
        JButton defaultBtn = new JButton();
        defaultBtn.setSize(new Dimension(115, 24));
        defaultBtn.setLocation(new Point(345, 180));
        defaultBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.default")) {
            public void actionPerformed(ActionEvent e) {
                Configuration.setString(CONF_DEFAULT_OUTPUT, outputFld.getText());
                Configuration.setString(CONF_DEFAULT_TEMPLATE, templateFld
                        .getText());
                Configuration.save();
            }
        });

        this.panel.add(defaultBtn, null);
        JButton execBtn = new JButton();
        execBtn.setSize(new Dimension(115, 24));
        execBtn.setLocation(new Point(30, 180));
        execBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.execute")) {

            public void actionPerformed(ActionEvent e) {

                Thread t = new Thread() {
                    public void run() {
                        try {
                            setEnabled(false);
                            //InputStream streamTemplate = null;

                            // verify that the template exists
                            String templateFile = templateFld.getText();
                            String templateExt = null;

                            String outputFile = outputFld.getText();

                            TemplateEngine templateEngine = null;

                            // get the file extension and find the
                            // appropriate template engine
                            templateExt = FileUtil.getExtension(templateFile);

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
                            File tempFile = new File(templateFile);
                            if (!tempFile.exists()){
                                throw new FileNotFoundException("The specified template file does not exist");
                            }

                            templateEngine.generate(ProjectManager.getManager()
                                    .getCurrentProject(), outputFile,
                                    templateFile);
                            
                            String ext = FileUtil.getExtension(outputFile);
                            PostProcessor postProcessor = PostProcessorFactory.getInstance(ext);
                            if (postProcessor == null){
                                throw new PostProcessorNotFoundException("Unable to find a compatible post processor engine for output file: " + outputFile);
                            }

                            // show "generation complete" message
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            Translator
                                                    .localize("argoprint.message.transformationDone"));
                        } catch (FileNotFoundException ex) {
                            LOG.error(ex.getMessage(), ex);
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            Translator
                                                    .localize("argoprint.message.wrongTemplate"));
                            return;
                        } catch (TemplateEngineNotFoundException te) {
                            LOG.error(te.getMessage(), te);
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            Translator
                                                    .localize("argoprint.message.wrongTemplate"));
                            return;

                        } catch (TemplateEngineException ex) {
                            LOG.error(ex.getMessage(), ex);
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            Translator
                                                    .localize("argoprint.message.transformationError"));
                            return;
                        } catch (IOException e) {
                            LOG.error(e.getMessage(), e);
                            JOptionPane
                                    .showMessageDialog(
                                            ArgoPrintDialog.this,
                                            Translator
                                                    .localize("argoprint.message.transformationError"));
                        } catch (PostProcessorNotFoundException e) {
                            LOG.error(e.getMessage(), e);
                            JOptionPane.showMessageDialog(ArgoPrintDialog.this, Translator
                                                    .localize("argoprint.message.transformationError"));
                        } finally {
                            setEnabled(true);
                        }

                    }
                };
                t.start();
            }

        });

        this.panel.add(execBtn, null);
       
        
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
