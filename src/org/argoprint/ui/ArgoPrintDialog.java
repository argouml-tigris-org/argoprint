// $Id$
// Copyright (c) 2010 The Regents of the University of California. All
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

// Copyright (c) 2010 The Regents of the University of California. All
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.argoprint.persistence.PostProcessor;
import org.argoprint.persistence.PostProcessorFactory;
import org.argoprint.persistence.PostProcessorNotFoundException;
import org.argoprint.persistence.TemplateEngine;
import org.argoprint.persistence.TemplateEngineException;
import org.argoprint.persistence.TemplateEngineFactory;
import org.argoprint.persistence.TemplateEngineNotFoundException;
import org.argoprint.persistence.TemplateMetaFile;
import org.argoprint.persistence.TemplateMetaFileSerializer;
import org.argoprint.util.FileUtil;
import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
/**
 * The dialog displayed when ArgoPrint is started from the ArgoUML menu.
 */
@SuppressWarnings("serial")
public class ArgoPrintDialog extends JDialog {

    TemplateTableModel model = null;

    private static final Logger LOG = Logger.getLogger(ArgoPrintDialog.class);

    private List<TemplateMetaFile> selectedTemplates = null;

    private List<JButton> buttonList = new ArrayList<JButton>();

    private ThreadPoolExecutor threadPool = null;

    private File templateRoot = null;
    
    private TemplateTablePanel panel = new TemplateTablePanel();

    /**
     * Constructor
     */
    public ArgoPrintDialog() {
        super();
        init();
    }

    private void init() {
        Translator.addClassLoader(Thread.currentThread().getContextClassLoader());

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setPreferredSize(new Dimension(900, 500));
        Container parent = this.getParent();
        System.out.println(parent);

        File userHome = new File(System.getProperty("user.home"));
        templateRoot = new File(userHome, ".argouml/templates");
        templateRoot.mkdirs();

        this.setTitle(Translator.localize("argoprint.dialog.title"));

        // main panel
       
        model = panel.getModel();


        int numTemplates = model.getRowCount();

        // create threadpool used for file operations
        threadPool = new ThreadPoolExecutor(numTemplates, numTemplates + 10, 2,
                TimeUnit.SECONDS, new ArrayBlockingQueue(numTemplates));

        // enable buttons based on the number of selected templates.
        panel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {

                // enable buttons if there are any templates selected.
                if (model.getSelectedRowCount() > 0) {
                    enableButtons(true);
                } else {
                    enableButtons(false);
                }

                selectedTemplates = model.getSelectedTemplates();
            }
        });

        contentPane.add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // button panel
        // close button
        JButton closeBtn = new JButton();
        closeBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.close")) {
            public void actionPerformed(ActionEvent e) {
                ArgoPrintDialog.this.setVisible(false);
            };
        });
        buttonPanel.add(closeBtn);

        // clone button
        JButton cloneBtn = new JButton();
        cloneBtn.setEnabled(false);
        cloneBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.clone")) {

            public void actionPerformed(ActionEvent e) {
                List<TemplateMetaFile> templates = ArgoPrintDialog.this.model
                        .getSelectedTemplates();

                TemplateMetaFile clone = null;
                try {
                    for (TemplateMetaFile template : templates) {
                        clone = (TemplateMetaFile) template.clone();
                        model.addTemplate(clone);
                        TemplateCloner cloner = new TemplateCloner(clone);
                        threadPool.execute(cloner);
                    }
                    JOptionPane.showMessageDialog(ArgoPrintDialog.this,
                            Translator
                                    .localize("argoprint.msg.templatesCloned"));
                } catch (CloneNotSupportedException e1) {
                    LOG.error("Exception", e1);
                }
            }
        });
        buttonPanel.add(cloneBtn);
        buttonList.add(cloneBtn);

        // register button
        JButton newBtn = new JButton();
        newBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.new")) {

            public void actionPerformed(ActionEvent e) {
                model.addTemplate(new TemplateMetaFile());

            }
        });
        buttonPanel.add(newBtn);
        
        // Update Button
        JButton updateBtn = new JButton();
        updateBtn.setAction(new AbstractAction(Translator.localize("argoprint.button.update")){

            public void actionPerformed(ActionEvent e) {
                List<TemplateMetaFile>templates = model.getLocalTemplates();
                TemplateUpdater updater = null;
                for(TemplateMetaFile template:templates){
                    updater = new TemplateUpdater(templateRoot, template);
                    threadPool.execute(updater);
                }
                JOptionPane.showMessageDialog(ArgoPrintDialog.this, "Local Templates Updated");
            }});
        buttonPanel.add(updateBtn);

        // generate button
        JButton generateBtn = new JButton();
        buttonList.add(generateBtn);
        generateBtn.setEnabled(false);

        generateBtn.setAction(new AbstractAction(Translator
                .localize("argoprint.button.generate")) {

            public void actionPerformed(ActionEvent e) {
                FileGenerator gen = null;
                
                String outputDir = panel.getOutputDir();
                File selectedDir = new File(outputDir);
                
                selectedTemplates = model.getSelectedTemplates();
                for (TemplateMetaFile templateMetaFile : selectedTemplates) {
                  gen = new FileGenerator(templateMetaFile,
                          selectedDir);
                  threadPool.execute(gen);
                }

            }
        });
        buttonPanel.add(generateBtn);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    private void enableButtons(boolean enabled) {
        for (JButton button : buttonList) {
            button.setEnabled(enabled);
        }
    }

    /**
     * This class is responsible for generating files.
     */
    class FileGenerator implements Runnable {

        TemplateMetaFile metaFile = null;

        File outputDir = null;

        public FileGenerator(TemplateMetaFile metaFile, File outputDir) {
            this.metaFile = metaFile;
            this.outputDir = outputDir;
        }

        public void run() {
            try {
                setEnabled(false);
                // InputStream streamTemplate = null;

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

                File outputFile = new File(this.outputDir, metaFile
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

                // show "generation complete" message
                JOptionPane.showMessageDialog(ArgoPrintDialog.this, Translator
                        .localize("argoprint.message.transformationDone"));
            } catch (FileNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                JOptionPane.showMessageDialog(ArgoPrintDialog.this, Translator
                        .localize("argoprint.message.wrongTemplate"));
                return;
            } catch (TemplateEngineNotFoundException te) {
                LOG.error(te.getMessage(), te);
                JOptionPane.showMessageDialog(ArgoPrintDialog.this, Translator
                        .localize("argoprint.message.wrongTemplate"));
                return;

            } catch (TemplateEngineException ex) {
                LOG.error(ex.getMessage(), ex);
                JOptionPane.showMessageDialog(ArgoPrintDialog.this, Translator
                        .localize("argoprint.message.transformationError"));
                return;
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(ArgoPrintDialog.this, Translator
                        .localize("argoprint.message.transformationError"));
            } catch (PostProcessorNotFoundException e) {
                LOG.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(ArgoPrintDialog.this, Translator
                        .localize("argoprint.message.transformationError"));
            } finally {
                setEnabled(true);
            }

        }

    }

    /**
     * This class is responsible for duplicating a template file
     * and the associated metadata file.
     * 
     * @author mfortner
     */
    class TemplateCloner implements Runnable {

        private TemplateMetaFile metaFile;

        private int bufferSize = 5000;

        /**
         * Constructor
         * 
         * @param metaFile A template metafile clone.
         */
        public TemplateCloner(TemplateMetaFile metaFile) {
            this.metaFile = metaFile;
        }

        /**
         * Constructor
         * 
         * @param metaFile A template metafile clone.
         * @param bufferSize
         */
        public TemplateCloner(TemplateMetaFile metaFile, int bufferSize) {
            this.metaFile = metaFile;
            this.bufferSize = bufferSize;
        }

        public void run() {
            try {
                BufferedInputStream buffStream = new BufferedInputStream(
                        this.metaFile.getTemplateStream(), this.bufferSize);

                // create the destination directories and file
                File destdir = new File(System.getProperty("user.home")
                        + "/.argouml");
                destdir.mkdirs();
                File cloneFile = new File(destdir, metaFile.getTemplateFile());

                // copy the template file
                FileOutputStream out = new FileOutputStream(cloneFile);

                // initialize byte buffer and begin copying operation.
                byte[] buffer = new byte[this.bufferSize];

                ;
                int charsRead = 0;
                while ((charsRead = buffStream.read(buffer)) > 0) {
                    out.write(buffer, 0, charsRead);
                }

                out.close();

                // write out the template metafile
                File xmlfile = new File(destdir, "templates/"
                        + metaFile.getName() + ".xml");
                this.metaFile.setTemplateFile(cloneFile.toURI().toString());
                TemplateMetaFileSerializer.write(xmlfile, this.metaFile);

            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }

        }

    }

    /**
     * This class is responsible for updating the template metadata file.
     * These files are updated whenever the metadata displayed in the TemplateTable
     * are updated.
     *
     * @author mfortner
     */
    class TemplateUpdater implements Runnable {

        TemplateMetaFile template = null;

        File templateRoot = null;

        /**
         * Constructor
         * 
         * @param templateRoot The local directory where templates are stored.
         * @param template The template to be updated.
         */
        public TemplateUpdater(File templateRoot, TemplateMetaFile template) {
            this.templateRoot = templateRoot;
            this.template = template;
        }

        public void run() {
            try {
                TemplateMetaFileSerializer.write(template);
            } catch (IOException e) {
                LOG.error("Exception", e);
            }

        }

    }

}
