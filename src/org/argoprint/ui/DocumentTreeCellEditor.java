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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;

import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

import javax.swing.event.CellEditorListener;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/** Custom CellEditor for DOM Trees. */

class DocumentTreeCellEditor 
    implements TreeCellEditor {

    /** The component that is used for editing */
    private JPanel component;

    /** The value (Node) that is subject to editing */
    private Node value;

    /** The number of clicks required to start editing */
    private static final int CLICK_COUNT = 2;

    private JComboBox
	tagCombo,
	attCombo;

    private JTextField
	attField;

    private AbstractAction
	actionChangeTag;

    public DocumentTreeCellEditor() {
	component = new JPanel();
	initializeActions();
	initializeComponent();
    }

    private void initializeActions() {
	actionChangeTag = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		}
	    };
	actionChangeTag.putValue(AbstractAction.NAME, "Change tag");
    }

    private void initializeComponent() {
	component.setLayout(new FlowLayout());

	tagCombo = new JComboBox();
	tagCombo.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		}
	    });
	attCombo = new JComboBox();
	attCombo.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (ItemEvent.SELECTED == e.getStateChange()) {
			String attName = (String)e.getItem();
			String attValue = ((Attr)value.getAttributes().getNamedItem(attName)).getValue();
			attField.setText(attValue);
		    }
		}
	    });
	attField = new JTextField(15);

	component.add(new JLabel("tag:"));
	component.add(tagCombo);
	component.add(new JButton("+"));
	component.add(new JLabel("attribute:"));
	component.add(attCombo);
	component.add(attField);
	component.add(new JButton("A"));
	component.add(new JButton("C"));
	
// 	tagCombo.addItem("template");
// 	tagCombo.addItem("for-each");
// 	tagCombo.addItem("choose");
// 	tagCombo.addItem("if");
	
// 	attCombo.addItem("match");
// 	attCombo.addItem("test");

    }

    public Component getTreeCellEditorComponent(JTree tree,
						Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf,
						int row) {
	if (value instanceof Element) {
	    this.value = (Node)value;
	    fillComponentDataForElement((Node)value);
	    return component;
	}
	return new JTextField(15);

    }

    public void fillComponentDataForElement(Node value) {
	tagCombo.removeAllItems();
	attCombo.removeAllItems();

	tagCombo.addItem(((Element)value).getTagName());
       
	NamedNodeMap atts = value.getAttributes();
	for (int i = 0; i < atts.getLength(); i++)
	    attCombo.addItem(((Attr)atts.item(i)).getName());
    }

    public void addCellEditorListener(CellEditorListener l) {
    }
    public void removeCellEditorListener(CellEditorListener l) {
    }

    public Object getCellEditorValue() {
	return value;
    }
    public boolean isCellEditable(EventObject anEvent) {
	if (anEvent instanceof java.awt.event.MouseEvent)
	    return ((java.awt.event.MouseEvent)anEvent).getClickCount() == DocumentTreeCellEditor.CLICK_COUNT;
	return false;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
	// TODO
	return true;
    }

    public void cancelCellEditing() {
    }
    public boolean stopCellEditing() {
	// TODO
	return true;
    }
}



