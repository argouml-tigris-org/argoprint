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

package org.argoprint.ui;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JEditorPane;

import org.argouml.ui.SystemInfoDialog;

@SuppressWarnings("serial")
public class MultiExceptionDialog extends JDialog {
    
    List<Throwable> exceptionList;
    JEditorPane editor = new JEditorPane();
    Component parent = null;

    /**
     * Constructor
     * @param parent    The parent object (used to center the dialog)
     * @param title     The title of the dialog.
     * @param intro     The intro for the dialog msg.
     * @param exceptionList     A list of exceptions.
     */
    public MultiExceptionDialog(Component parent, String title, String intro,  List<Throwable> exceptionList) {
        super();
        this.parent = parent;
        this.setTitle(title);
        this.editor.setText(formatExceptionList(exceptionList));
        this.getContentPane().add(editor);
        this.setLocationRelativeTo(parent);
    }

    /**
     * This method formats a list of exceptions into a single message.
     * @param exceptionList  A list of exceptions
     * @return
     */
    public static String formatExceptionList(
            List<Throwable> exceptionList) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        for (Throwable e : exceptionList) {
            if (e.getCause() != null) {

                // This text is for the developers.
                // It doesn't need to be localized.
                pw.print(e.getMessage());
                pw.print("<hr>System Info:<p>" + SystemInfoDialog.getInfo());
                pw.print("<p><hr>Error occurred at : " + new Date());
                pw.print("<p>  Cause : ");
                e.getCause().printStackTrace(pw);
                pw.print("-------<p>Full exception : ");
            }
            e.printStackTrace(pw);
        }
        return sw.toString();

    }

}
