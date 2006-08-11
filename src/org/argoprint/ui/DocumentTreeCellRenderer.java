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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.argoprint.APResources;

class DocumentTreeCellRenderer
    extends DefaultTreeCellRenderer {

    private JPanel
	panelAtt,
	panelText,
	panelComment,
	panelElement;

    private static final JLabel
	SEPARATOR = new JLabel(":");

    private ImageIcon
	iconAtt,
	iconText,
	iconComment;

    private static boolean
	inDropState = false;

    private static void setDropState(boolean state) {
	inDropState = state;
    }

    public DocumentTreeCellRenderer() {
	iconAtt = new ImageIcon(APResources
				.getResource(APResources.ICON_DOT_GREEN));

	iconText = new ImageIcon(APResources
				.getResource(APResources.ICON_DOT_BLUE));

	iconComment = new ImageIcon(APResources
				    .getResource(APResources.ICON_DOT_RED));

    }

    private JPanel getPanel() { 
	JPanel result = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
// 	result.setBackground(DEFAULT_SELECTED_COLOR);

	return result;
    }

    private JPanel getAttPanel(Attr att) {
	JPanel result = getPanel();

	result.add(new JLabel(iconAtt));
	result.add(new JLabel(att.getName()));
	result.add(SEPARATOR);
	result.add(new JLabel(att.getValue()));
	
	return result;
    }
    private JPanel getElementPanel(Element elem) {
	JPanel result = getPanel();
	
	result.add(new JLabel(elem.getTagName()));
	
	return result;
    }
    private JPanel getTextPanel(Text text) {
	JPanel result = getPanel();

	result.add(new JLabel(iconText));
	result.add(new JLabel(text.getData()));
	
	return result;
    }
    private JPanel getCommentPanel(Comment comment) {
	JPanel result = getPanel();

	result.add(new JLabel(iconComment));
	result.add(new JLabel(comment.getData()));
	
	return result;
    }
    private JPanel getDefaultPanel(Object obj) {
	JPanel result = getPanel();

	result.add(new JLabel(obj.toString()));
	
	return result;
    }

    public Component getTreeCellRendererComponent(JTree tree,
						  Object value,
						  boolean selected,
						  boolean expanded,
						  boolean leaf,
						  int row,
						  boolean hasFocus) {
	DocumentJTree djtree = (DocumentJTree) tree;
	JPanel result;
	Color highlight = djtree.getHighlight((Node) value, selected);
	
	if (value instanceof Attr)
	    result = getAttPanel((Attr) value);
	else if (value instanceof Element)
	    result = getElementPanel((Element) value);
        else if (value instanceof Text)
	    result = getTextPanel((Text) value);
	else if (value instanceof Comment)
	    result = getCommentPanel((Comment) value);
	else
	    result = getDefaultPanel(value);
	
	if (highlight != DocumentJTree.NO_HIGHLIGHT) {
	    result.setBackground(highlight);
	    result.setOpaque(true);
	} else
	    result.setOpaque(false);

	return result;
    }
}
