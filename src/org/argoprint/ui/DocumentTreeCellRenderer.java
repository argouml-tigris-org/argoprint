// $Id DocumentTreeCellRenderer.java,v 0.1 2006/06/21 17:26 comp_ Exp $
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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

class DocumentTreeCellRenderer
    extends DefaultTreeCellRenderer {

    private JPanel component;

    public Component getTreeCellRendererComponent(JTree tree,
						  Object value,
						  boolean selected,
						  boolean expanded,
						  boolean leaf,
						  int row,
						  boolean hasFocus) {
	Node node = (Node)value;
	StringBuffer label = new StringBuffer();

	switch (node.getNodeType()) {
	case Node.ELEMENT_NODE:
	    label.append(node.getNodeName());

	    NamedNodeMap attrs = node.getAttributes();
	    Attr attr;
	    for (int i = 0; i < attrs.getLength(); i++) {
		attr = (Attr)attrs.item(i);
		label.append(" " + attr.getName() + "=\"" + attr.getValue() + "\"");
	    }
	    
	    break;
	case Node.TEXT_NODE:
	case Node.COMMENT_NODE:
	    label.append(node.getNodeValue());
	    break;
	default:
	    label.append("???");
	}

// 	component = new JPanel();
// 	component.setLayout(new java.awt.FlowLayout());
// 	component.add(super.getTreeCellRendererComponent(tree,
// 							 label.toString(),
// 							 selected,
// 							 expanded,
// 							 leaf,
// 							 row,
// 							 hasFocus));
// 	component.add(new javax.swing.JButton("+"));
// 	return component;

	return super.getTreeCellRendererComponent(tree,
						  label.toString(),
						  selected,
						  expanded,
						  leaf,
						  row,
						  hasFocus);
    }
}
