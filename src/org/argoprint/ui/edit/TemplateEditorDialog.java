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
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.argoprint.persistence.TemplateMetaFile;
import org.argouml.i18n.Translator;

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
     * @param template
     */
    public TemplateEditorDialog(Component parent, TemplateMetaFile template) {
        this.setLocationRelativeTo(parent);
        init(template);
    }

    private void init(final TemplateMetaFile template) {
        this.setTitle("Template Editor");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // create the button panel
        JPanel buttonPanel = new JPanel();

        this.setModal(true);
        this.setAlwaysOnTop(true);

        // create the template editor
        try {
            final TemplateEditor editor = TemplateEditorFactory
                    .getTemplateEditor(template);
            System.out.println("editor: "+editor);
            if (editor != null) {
                JScrollPane scroller = new JScrollPane();
                scroller.getViewport().add(editor.getEditorComponent());
                contentPane.add(scroller,
                        BorderLayout.CENTER);

                // create the Preview Button
                JButton previewBtn = new JButton(new AbstractAction("Preview") {
                    public void actionPerformed(ActionEvent e) {
                        File tmpFile = new File(System.getProperty("java.io.tmpdir"), template.getOutputFile());
                        // TODO: generate contents
                        editor.preview(template);
                    }

                });
                previewBtn.setEnabled(false);
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
}
