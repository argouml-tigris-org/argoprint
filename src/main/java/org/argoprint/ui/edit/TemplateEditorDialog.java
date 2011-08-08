/* $Id$
 *******************************************************************************
 * Copyright (c) 2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mfortner
 *******************************************************************************
 */

package org.argoprint.ui.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.argoprint.persistence.PostProcessor;
import org.argoprint.persistence.PostProcessorFactory;
import org.argoprint.persistence.PostProcessorNotFoundException;
import org.argoprint.persistence.TemplateEngine;
import org.argoprint.persistence.TemplateEngineException;
import org.argoprint.persistence.TemplateEngineFactory;
import org.argoprint.persistence.TemplateEngineNotFoundException;
import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.util.FileUtil;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;

/**
 * This dialog box displays the template editor.
 * 
 * @author mfortner
 */
@SuppressWarnings("serial")
public class TemplateEditorDialog extends JDialog {

    private static final Logger LOG = Logger
            .getLogger(TemplateEditorDialog.class);

    /**
     * Constructor
     * 
     * @param parent  The ArgoPrintDialog instance
     * @param template  The template metafile which describes the template you are editing.
     */
    public TemplateEditorDialog(Component parent, TemplateMetaFile template) {
        this.setLocationRelativeTo(parent);
        init(template);
    }

    private void init(final TemplateMetaFile template) {
        this.setPreferredSize(new Dimension(700, 600));
        this.setTitle("Template Editor");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // create the button panel
        JPanel buttonPanel = new JPanel();

        // create the template editor
        try {
            final TemplateEditor editor = TemplateEditorFactory
                    .getTemplateEditor(template);
            if (editor != null) {
                
                contentPane.add(editor.getEditorComponent(),
                        BorderLayout.CENTER);

                // create the Preview Button
                JButton previewBtn = new JButton(new AbstractAction("Preview") {
                    public void actionPerformed(ActionEvent e) {
                        generate(template, System.getProperty("java.io.tmpdir"));
                        editor.preview(template);         
                    }

                });
                previewBtn.setEnabled(true);
                buttonPanel.add(previewBtn);

                JButton saveBtn = new JButton(new AbstractAction("Save") {

                    public void actionPerformed(ActionEvent e) {
                        editor.save(template);
                        JOptionPane.showMessageDialog(TemplateEditorDialog.this, "Template Saved");
                    }

                });
                buttonPanel.add(saveBtn);
                editor.init(template);
            } else {
                JPanel panel = new JPanel();
                panel.add(new JLabel("Editor Not Found"));
                contentPane.add(panel, BorderLayout.CENTER);
            }

            contentPane.add(buttonPanel, BorderLayout.SOUTH);

        } catch (InstantiationException e) {
            LOG.error("Exception", e);
        } catch (IllegalAccessException e) {
            LOG.error("Exception", e);
        }

        // create the close Button
        JButton closeBtn = new JButton(new AbstractAction("Close") {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }

        });

        buttonPanel.add(closeBtn, BorderLayout.SOUTH);
        pack();

    }
    
    private void generate(TemplateMetaFile metaFile, String outputDir){
        File outputFile = null;
        
        try {
            // verify that the template exists
            String templateFile = metaFile.getTemplateFile();
            String templateExt = null;

            TemplateEngine templateEngine = null;

            // get the file extension and find the
            // appropriate template engine
            templateExt = FileUtil.getExtension(templateFile);

            // verify that the template has been specified
            if (templateExt == null || templateExt == "") {
                throw new FileNotFoundException("No template specified");
            }

            templateEngine = TemplateEngineFactory.getInstance(templateExt);
            templateEngine.setOutputDir(outputDir);

            if (templateEngine == null) {
                throw new TemplateEngineNotFoundException(
                        "Unable to find a compatible template engine for template: "
                                + templateFile);
            }

            InputStream templateStream = metaFile.getTemplateStream();
            if (templateStream == null) {
                throw new IOException(String.format("%s not found",
                        metaFile));
            }

            outputFile = new File(outputDir, metaFile
                    .getOutputFile());
            Project currProject = ProjectManager.getManager()
                    .getOpenProjects().get(0);
            templateEngine.generate(currProject, new FileOutputStream(
                    outputFile), templateStream);

            String ext = FileUtil.getExtension(outputFile);
            PostProcessor postProcessor = PostProcessorFactory
                    .getInstance(ext);
            if (postProcessor == null) {
                throw new PostProcessorNotFoundException(
                        "Unable to find a compatible post processor engine for output file: "
                                + outputFile);
            }

        } catch (FileNotFoundException ex) {
            
            LOG.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(TemplateEditorDialog.this, Translator
                    .localize("argoprint.message.wrongTemplate"));
            
        } catch (TemplateEngineNotFoundException te) {
           
            LOG.error(te.getMessage(), te);
            JOptionPane.showMessageDialog(TemplateEditorDialog.this, Translator
                    .localize("argoprint.message.wrongTemplate"));
           

        } catch (TemplateEngineException ex) {
            
            LOG.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(TemplateEditorDialog.this, Translator
                    .localize("argoprint.message.transformationError"));
            
        } catch (IOException e) {
           
            LOG.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(TemplateEditorDialog.this, Translator
                    .localize("argoprint.message.transformationError"));
        } catch (PostProcessorNotFoundException e) {
           
            LOG.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(TemplateEditorDialog.this, Translator
                    .localize("argoprint.message.transformationError"));
        } catch (Throwable e) {
           
            LOG.error(e.getMessage(), e);
        } 
       
    }
}
