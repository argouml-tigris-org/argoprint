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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

import javax.swing.event.CellEditorListener;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.argoprint.ArgoPrintResources;

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
	buttonAcceptAttAdd,
	buttonRemoveAtt;

    private JComboBox
	comboTag,
	comboAtt;

    private JTextField
	fieldAttName,
	fieldAttValue,
	fieldTag,
	fieldText;

    private AbstractAction
	actionAcceptAttModification,
	actionAcceptNewAtt,
	actionAcceptTextModification,
	actionAppendAtt,
	actionRemoveAtt,
	actionSetTag;

    public DocumentTreeCellEditor() {
	initializeActions();
	initializeComponents();
    }

    private void initializeActions() {
	// Attribute modification action
	actionAcceptAttModification = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    String newAttValue, attName;

		    attName = (String)comboAtt.getSelectedItem();
		    newAttValue = fieldAttValue.getText();
		    // TODO: validate the new value
		    ((Element)value).getAttributeNode(attName).setValue(newAttValue);
		}
	    };
	actionAcceptAttModification.putValue(AbstractAction.SMALL_ICON,
					     new ImageIcon(ArgoPrintResources
							   .getResource(ArgoPrintResources.ICON_CONFIRM)));
	actionAcceptAttModification.putValue(AbstractAction.SHORT_DESCRIPTION,
					     "Confirm attribute modification.");

	// New attribute action
	actionAcceptNewAtt = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    String attName = fieldAttName.getText(),
			attValue = fieldAttValue.getText();
		    ((Element)value).setAttribute(attName, attValue);

		    setComponentElementForMod();
		}
	    };
	actionAcceptNewAtt.putValue(AbstractAction.SMALL_ICON,
				    new ImageIcon(ArgoPrintResources
						  .getResource(ArgoPrintResources.ICON_CONFIRM)));
	actionAcceptNewAtt.putValue(AbstractAction.SHORT_DESCRIPTION,
				    "Confirm new attribute.");
				    

	actionAcceptTextModification = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    ((CharacterData)value).setData(fieldText.getText());
		}
	    };

	// Append action
	actionAppendAtt = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    setComponentElementForAdd();
		}
	    };
	actionAppendAtt.putValue(AbstractAction.SMALL_ICON,
				 new ImageIcon(ArgoPrintResources
					       .getResource(ArgoPrintResources.ICON_ADD)));
	actionAppendAtt.putValue(AbstractAction.SHORT_DESCRIPTION,
				 "Append attribute.");

	// Remove action
	actionRemoveAtt = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    String attName = (String)comboAtt.getSelectedItem();
		    ((Element)value).removeAttribute(attName);

		    setComponentElementForMod();
		}
	    };
	actionRemoveAtt.putValue(AbstractAction.SMALL_ICON,
				 new ImageIcon(ArgoPrintResources
					       .getResource(ArgoPrintResources.ICON_REMOVE)));
	actionRemoveAtt.putValue(AbstractAction.SHORT_DESCRIPTION,
				 "Remove attribute.");

	// Action set tag
	actionSetTag = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    String newTag = fieldTag.getText();
		    value.getOwnerDocument().renameNode(value, null, newTag);
		}
	    };
	
    }

    private void initializeComponents() {
	initializeElementComponent();
	initializeTextComponent();
    }

    private void initializeElementComponent() {
	componentElement = new JPanel();
	componentElement.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

	comboTag = new JComboBox();
	comboTag.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		}
	    });

	fieldTag = new JTextField(15);
	fieldTag.getKeymap()
	    .addActionForKeyStroke(javax.swing.KeyStroke
				   .getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
				   actionSetTag);

	comboAtt = new JComboBox();
	comboAtt.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (ItemEvent.SELECTED == e.getStateChange()) {
			String attName = (String)e.getItem();
			String attValue = ((Attr)value.getAttributes().getNamedItem(attName)).getValue();
			fieldAttValue.setText(attValue);
		    }
		}
	    });
	fieldAttName = new JTextField(10);
	fieldAttValue = new JTextField(15);

	componentElement.add(new JLabel("tag:"));
	componentElement.add(fieldTag);
	componentElement.add(comboTag);
	comboTag.setVisible(false);
	componentElement.add(buttonAddAtt = new JButton(actionAppendAtt));
	componentElement.add(buttonRemoveAtt = new JButton(actionRemoveAtt));
	componentElement.add(new JLabel("attribute:"));
	componentElement.add(comboAtt);
	componentElement.add(fieldAttName);
	componentElement.add(fieldAttValue);

	componentElement.add(buttonAcceptAttAdd = new JButton(actionAcceptNewAtt));
	buttonAcceptAttAdd.setVisible(false);
	componentElement.add(buttonAcceptAttMod = new JButton(actionAcceptAttModification));
    }

    private void initializeTextComponent() {
	componentText = new JPanel();
	componentText.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

	componentText.add(fieldText = new JTextField(15));
	fieldText.getKeymap()
	    .addActionForKeyStroke(javax.swing.KeyStroke
				   .getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
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
	comboAtt.removeAllItems();

	NamedNodeMap atts = value.getAttributes();
	for (int i = 0; i < atts.getLength(); i++)
	    comboAtt.addItem(((Attr)atts.item(i)).getName());
    }

    private void setComponentElementForMod() {
	updateAttCombo();

	if (((Element)value).hasAttributes()) {
	    buttonRemoveAtt.setVisible(true);
	    comboAtt.setVisible(true);
	    fieldAttValue.setVisible(true);
	    buttonAcceptAttMod.setVisible(true);
	} else {
	    buttonRemoveAtt.setVisible(false);
	    comboAtt.setVisible(false);
	    fieldAttValue.setVisible(false);
	    buttonAcceptAttMod.setVisible(false);
	}

	buttonAddAtt.setVisible(true);
	buttonAcceptAttAdd.setVisible(false);
	fieldAttName.setVisible(false);

	componentElement.setSize(componentElement.getLayout().preferredLayoutSize(componentElement));
    }

    private void setComponentElementForAdd() {
	buttonAddAtt.setVisible(false);
	buttonAcceptAttMod.setVisible(false);
	buttonAcceptAttAdd.setVisible(true);

	comboAtt.setVisible(false);
	fieldAttName.setVisible(true);
	fieldAttValue.setVisible(true);
	
	fieldAttName.setText("");
	fieldAttValue.setText("");

	componentElement.setSize(componentElement.getLayout().preferredLayoutSize(componentElement));
    }

    public void setComponentElement(Node value) {
	comboTag.removeAllItems();
	comboTag.addItem(((Element)value).getTagName());

	fieldTag.setText(((Element)value).getTagName());
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
	    return
		(((java.awt.event.MouseEvent)anEvent).getClickCount()
		 == DocumentTreeCellEditor.CLICK_COUNT);
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



