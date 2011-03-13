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

package org.argoprint.ui.preview;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.argoprint.persistence.TemplateMetaFile;

/**
 * This dialog displays the previewer component.
 * 
 * @author mfortner
 */
public class TemplatePreviewerDialog extends JDialog {

    private static final Logger LOG = Logger
            .getLogger(TemplatePreviewerDialog.class);

    /**
     * 
     * @param template
     */
    public TemplatePreviewerDialog(TemplateMetaFile template) {
        init(template);
    }

    private void init(TemplateMetaFile template) {
        this.setTitle("Template Preview");
        this.setPreferredSize(new Dimension(500,400));
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // create the button panel
        JPanel buttonPanel = new JPanel();

        // create the template editor
        try {
            final TemplatePreviewer previewer = TemplatePreviewerFactory
                    .getPreviewer(template);
            if (previewer != null) {
                previewer.init(template);
                contentPane.add(previewer.getPreviewerComponent(),
                        BorderLayout.CENTER);

                
            } else {
                JPanel panel = new JPanel();
                panel.add(new JLabel("Previewer Not Found"));
                contentPane.add(panel);
            }
            
            

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

        buttonPanel.add(closeBtn);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
    }

}
