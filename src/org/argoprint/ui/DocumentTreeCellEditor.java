// $Id DocumentTreeCellEditor.java,v 0.1 2006/06/21 17:26 comp_ Exp $
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
import org.w3c.dom.Text;

/** Custom CellEditor for DOM Trees. */

class DocumentTreeCellEditor 
    implements TreeCellEditor {

    /** The component that is used for editing */
    private JPanel component;
    private JPanel componentElement;
    private JPanel componentText;

    /** The value (Node) that is subject to editing */
    private Node value;

    /** The number of clicks required to start editing */
    private static final int CLICK_COUNT = 2;

    private JButton
	buttonAddAtt,
	buttonAcceptAttMod,
	buttonAcceptAttAdd;

    private JComboBox
	tagCombo,
	attCombo;

    private JTextField
	attNameField,
	attValueField,
	fieldText;

    private AbstractAction
	actionAcceptAttModification,
	actionAcceptNewAtt,
	actionAcceptTextModification,
	actionAppendAtt;

    public DocumentTreeCellEditor() {
	initializeActions();
	initializeComponents();
    }

    private void initializeActions() {
	actionAcceptAttModification = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    String newAttValue, attName;

		    attName = (String)attCombo.getSelectedItem();
		    newAttValue = attValueField.getText();
		    // TODO: validate the new value
		    ((Element)value).getAttributeNode(attName).setValue(newAttValue);
		}
	    };
	actionAcceptAttModification.putValue(AbstractAction.NAME, "Modify");

	actionAcceptNewAtt = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    String attName = attNameField.getText(),
			attValue = attValueField.getText();
		    ((Element)value).setAttribute(attName, attValue);

		    setComponentElementForMod();
		}
	    };
	actionAcceptNewAtt.putValue(AbstractAction.NAME, "Add");

	actionAcceptTextModification = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    ((Text)value).replaceWholeText(fieldText.getText());
		    stopCellEditing();
		}
	    };

	actionAppendAtt = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    setComponentElementForAdd();
		}
	    };
	actionAppendAtt.putValue(AbstractAction.NAME, "+");
    }

    private void initializeComponents() {
	initializeElementComponent();
	initializeTextComponent();
    }

    private void initializeElementComponent() {
	componentElement = new JPanel();
	componentElement.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

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
			attValueField.setText(attValue);
		    }
		}
	    });
	attNameField = new JTextField(10);
	attValueField = new JTextField(15);

	componentElement.add(new JLabel("tag:"));
	componentElement.add(tagCombo);
	componentElement.add(new JLabel("attribute:"));
	componentElement.add(attCombo);
	componentElement.add(attNameField);
	componentElement.add(attValueField);

	componentElement.add(buttonAcceptAttAdd = new JButton(actionAcceptNewAtt));
	buttonAcceptAttAdd.setVisible(false);
	componentElement.add(buttonAcceptAttMod = new JButton(actionAcceptAttModification));
	componentElement.add(buttonAddAtt = new JButton(actionAppendAtt));
    }

    private void initializeTextComponent() {
	componentText = new JPanel();
	componentText.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

	componentText.add(fieldText = new JTextField(15));
	fieldText.getKeymap().addActionForKeyStroke(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
						    actionAcceptTextModification);
    }

    public Component getTreeCellEditorComponent(JTree tree,
						Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf,
						int row) {
	this.value = (Node)value;
	if (value instanceof Element) {
	    setComponentElement((Node)value);
	    component = componentElement;
	} else if (value instanceof Text) {
	    setComponentText((Node)value);
	    component = componentText;
	} else
	    return new JTextField(((Node)value).getNodeName());

	return component;
    }

    private void updateAttCombo() {
	attCombo.removeAllItems();

	NamedNodeMap atts = value.getAttributes();
	for (int i = 0; i < atts.getLength(); i++)
	    attCombo.addItem(((Attr)atts.item(i)).getName());
    }

    private void setComponentElementForMod() {
	updateAttCombo();

	buttonAddAtt.setVisible(true);
	buttonAcceptAttMod.setVisible(true);
	buttonAcceptAttAdd.setVisible(false);

	attCombo.setVisible(true);
	attNameField.setVisible(false);
	attValueField.setVisible(true);

	componentElement.setSize(componentElement.getLayout().preferredLayoutSize(componentElement));
    }

    private void setComponentElementForAdd() {
	buttonAddAtt.setVisible(false);
	buttonAcceptAttMod.setVisible(false);
	buttonAcceptAttAdd.setVisible(true);

	attCombo.setVisible(false);
	attNameField.setVisible(true);
	attValueField.setVisible(true);
	
	attNameField.setText("");
	attValueField.setText("");

	componentElement.setSize(componentElement.getLayout().preferredLayoutSize(componentElement));
    }

    public void setComponentElement(Node value) {
	tagCombo.removeAllItems();
	tagCombo.addItem(((Element)value).getTagName());
	setComponentElementForMod();
    }
    
    public void setComponentText(Node value) {
	fieldText.setText(((Text)value).getWholeText());
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



