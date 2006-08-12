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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeCellEditor;

import javax.swing.event.CellEditorListener;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/** Custom CellEditor for DOM Trees. */

class DocumentTreeCellEditor 
    implements TreeCellEditor {

    private static final short MIN_COL_COUNT = 10;

    /** The component that is used for editing */
    private JPanel panelComponent;

    /** The value (Node) that is subject to editing */
    private Node value;

    /** The number of clicks required to start editing */
    private static final int CLICK_COUNT = 2;

    private JLabel
	label;

    private JTextField
	fieldValue;

    private JTree
	tree;

    public DocumentTreeCellEditor() {
	panelComponent = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
	panelComponent.setOpaque(false);

	label = new JLabel();

	fieldValue = new JTextField() {
		public void setText(String text) {
		    int textWidth = SwingUtilities
			.computeStringWidth(getFontMetrics(getFont()),
					    text);

		    setColumns(Math.max(MIN_COL_COUNT,
					textWidth
					/ getColumnWidth()
					+ 1));		    

		    super.setText(text);
		}
	    };

	fieldValue.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    tree.stopEditing();
		}
	    });

	panelComponent.add(label);
	panelComponent.add(fieldValue);
    }

    public Component getTreeCellEditorComponent(JTree tree,
						Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf,
						int row) {
	if (value instanceof Attr) {
	    Attr attr = (Attr) value;
	    label.setText(attr.getName());
	    fieldValue.setText(attr.getValue());
	} else if (value instanceof Text) {
	    Text text = (Text) value;
	    label.setText("");
	    fieldValue.setText(text.getData());
	}

	this.tree = tree;
	this.value = (Node) value;

	return panelComponent;
    }

    public void addCellEditorListener(CellEditorListener l) {
    }
    public void removeCellEditorListener(CellEditorListener l) {
    }

    public Object getCellEditorValue() {
	return value;
    }

    public boolean isCellEditable(EventObject event) {
	JTree source = (JTree) event.getSource();
	Object subject = source.getLastSelectedPathComponent();

	return (event instanceof MouseEvent)
	    && !(subject instanceof Element)
	    && SwingUtilities.isLeftMouseButton((MouseEvent) event)
	    && (((MouseEvent) event).getClickCount()
		== DocumentTreeCellEditor.CLICK_COUNT);
    }

    public boolean shouldSelectCell(EventObject anEvent) {
	return true;
    }

    public void cancelCellEditing() {
    }
    public boolean stopCellEditing() {
	if (isValid(fieldValue.getText())) {
	    if (value instanceof Attr)
		((Attr) value)
		    .setValue(fieldValue.getText());
	    else if (value instanceof Text)
		((Text) value)
		    .setData(fieldValue.getText());

	    return true;
	} else
	    return false;
    }

    // TODO:
    private boolean isValid(String value) {
	return true;
    }
}
