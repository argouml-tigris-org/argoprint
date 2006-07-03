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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;

import java.net.URL;

import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;

import org.argoprint.ArgoPrintManagerModel;

public class ArgoPrintManager
    extends JPanel {

    private ArgoPrintManagerModel model;

    private AbstractAction
	actionSelectLocation;

    private JTable tableIdentifiers;

    private JTextField
	fieldLocation;

    public ArgoPrintManager(ArgoPrintManagerModel model) {
	super();
	this.model = model;
	initializeActions();
	initializeComponents();
    }

    private void initializeActions() {
// 	final java.awt.Component parent = this;
// 	actionSelectLocation = new AbstractAction() {
// 		public void actionPerformed(ActionEvent e) {
// 		    JFileChooser chooser = new JFileChooser();
// 		    if (JFileChooser.APPROVE_OPTION == chooser.showDialog(parent, "Select")) {
// 			try {
// 			    // TODO: remove hardwire identifier column index
// 			    String selectedIdentifier = (String)tableIdentifiers.getValueAt(tableIdentifiers.getSelectedRow(), 1);
// 			    ArgoPrintManagerModel.TemplateJob job = model.getJob(selectedIdentifier);
// 			    if (job != null)
// 				job.setTemplateLocation(chooser.getSelectedFile().toURL());
// 			} catch (java.net.MalformedURLException ex) {
// 			    // TODO
// 			    ex.printStackTrace();
// 			}
// 		    }
			
// 		}
// 	    };
    }

    private void initializeComponents() {
	setLayout(new BorderLayout());
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	add(mainPane, BorderLayout.CENTER);


	tableIdentifiers = new JTable() {
		public void setValueAt(Object aValue, int row, int column) {
		    if (aValue instanceof String)
			aValue = ((String)aValue).trim();
		    super.setValueAt(aValue, row, column);

		    //  The value might not have been set because of unique identifier
		    // restriction. An exception can't be thrown from the model because
		    // it must comply with the AbstractTableModel class interface.

		    // TODO: remove the hardwired identifier column index
		    if ((column == 1) && (!aValue.equals(getValueAt(row, column))))
			JOptionPane.showMessageDialog(this,
						      "The identifier must be unique and the one you selected is already in the list.",
						      "Error",
						      JOptionPane.ERROR_MESSAGE);
		}
	    };
	mainPane.setTopComponent(new JScrollPane(tableIdentifiers));
// 	add(new JScrollPane(tableIdentifiers), BorderLayout.CENTER);

	tableIdentifiers.setModel(new AbstractTableModel() {
		private static final int COUNT_COL = 4;
		private static final int COL_SELECTED = 0;
		private static final int COL_IDENTIFIER = 1;
		private static final int COL_TEMPLATE = 2;
		private static final int COL_OUTPUT = 3;

		private Boolean selected;
		private String identifier;
		private URL template, output;

		// initialization
		{
		    initializeEditRow();
		}


		// helper functions
		private void initializeEditRow() {
		    selected = true;
		    identifier = "";
		    template = null;
		    output = null;
		}

		private boolean isEditRow(int row) {
		    return (row == getRowCount() - 1);
		}

		private boolean isEditRowComplete() {
		    return
			!identifier.equals("") &&
			(template != null) &&
			(output != null);
		}

		private boolean isIdentifierUnique(String identifier) {
		    return !model.getIdentifiers().contains(identifier);
		}

		private boolean hasIdentifier(int row) {
		    String ident = (String)getValueAt(row, COL_IDENTIFIER);
		    if ((ident == null) || ident.equals(""))
			return false;
		    return true;
		}

		// interface
		public int getColumnCount() {
		    return COUNT_COL;
		}

		public int getRowCount() {
		    return model.getIdentifiers().size()
			// and the edit row
			+ 1;
		}
		public Object getValueAt(int row, int column) {
		    if (isEditRow(row)) {
			switch (column) {
			case COL_SELECTED:
			    return selected;
			case COL_IDENTIFIER:
			    return identifier;
			case COL_TEMPLATE:
			    return template;
			case COL_OUTPUT:
			    return output;
			default: return null;
			}
		    } else {
			switch (column) {
			case COL_SELECTED:
			    return model.getJob((String)getValueAt(row, COL_IDENTIFIER))
				.getSelected();
			case COL_IDENTIFIER:
			    // TODO: find another random-access to identifier
			    return model.getIdentifiers().toArray()[row];
			case COL_TEMPLATE:
			    return model.getJob((String)getValueAt(row, COL_IDENTIFIER))
				.getTemplate();
			case COL_OUTPUT:
			    return model.getJob((String)getValueAt(row, COL_IDENTIFIER))
				.getOutput();
			default: return null;
			}
		    }
		}
		public void setValueAt(Object obj, int row, int column) {
		    if (isEditRow(row)) {
			switch (column) {
			case COL_SELECTED:
			    selected = (Boolean)obj;
			    break;
			case COL_IDENTIFIER:
			    if (isIdentifierUnique((String)obj))
				identifier = (String)obj;
			    break;
			case COL_TEMPLATE:
			    template = (URL)obj;
			    break;
			case COL_OUTPUT:
			    output = (URL)obj;
			    break;
			}
			if (isEditRowComplete() &&
			    isIdentifierUnique(identifier)) {
			    model.addJob(model.new
					 TemplateJob(identifier,
						      template,
						      output,
						      selected));
			    initializeEditRow();
			}
		    } else {
			switch (column) {
			case COL_SELECTED:
			    model.getJob((String)getValueAt(row, COL_IDENTIFIER))
				.setSelected((Boolean)obj);
			    break;
			case COL_IDENTIFIER:
			    model.getJob((String)getValueAt(row, COL_IDENTIFIER))
				.setIdentifier((String)obj);
			    break;
			case COL_TEMPLATE:
			    model.getJob((String)getValueAt(row, COL_IDENTIFIER))
				.setTemplate((URL)obj);
			    break;
			case COL_OUTPUT:
			    model.getJob((String)getValueAt(row, COL_IDENTIFIER))
				.setOutput((URL)obj);
			    break;
			}

		    }
		}

		public String getColumnName(int column) {
		    switch (column) {
		    case COL_IDENTIFIER: return "Identifier";
		    case COL_TEMPLATE: return "Template URL";
		    case COL_OUTPUT: return "Output URL";
		    default: return "";
		    }
		}
		public Class getColumnClass(int column) {
		    switch (column) {
		    case COL_SELECTED: return Boolean.class;
		    case COL_IDENTIFIER: return String.class;
		    case COL_TEMPLATE: return URL.class;
		    case COL_OUTPUT: return URL.class;
		    default: return String.class;
		    }
		}
		public boolean isCellEditable(int row, int column) {
		    return hasIdentifier(row) || isEditRow(row);
		}
	    });


	JTabbedPane templateTabs = new JTabbedPane();

	mainPane.setBottomComponent(templateTabs);

	final JTable tableParameters = new JTable();
	tableParameters.setModel(new AbstractTableModel() {
		private static final int COUNT_COL = 2;
		private static final int COL_IDENTIFIER = 0;
		private static final int COL_VALUE = 1;

		private String identifier, value;
		private ArgoPrintManagerModel.TemplateJob job;
		private int rowCount;

		// initialization
		{
		    initializeEditRow();
		    
 		    tableIdentifiers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			    public void valueChanged(ListSelectionEvent e) {
				updateModel();
			    }
			});

		}

		/** Updates the model of this "proxy-model" */
		private void updateModel() {
		    // TODO: ugly assumtion - last row in the other table is the
		    //  edit row
		    if ((tableIdentifiers.getSelectedRowCount() == 0) ||
			(tableIdentifiers.getSelectedRow()
			 == tableIdentifiers.getRowCount() - 1))

			rowCount = 0;
		    else {
			//TODO: remove explicit index
			int selectedRow = tableIdentifiers.getSelectedRow();
			String identifier = (String)tableIdentifiers
			    .getValueAt(selectedRow, 1);
			job = model.getJob(identifier);
			rowCount = job.getParameters().size() 
			    // and the edit row
			    + 1;
		    }

		    fireTableStructureChanged();
		}
		private void initializeEditRow() {
		    identifier = "";
		    value = "";
		}
		private boolean isEditRow(int row) {
		    return (row == getRowCount() - 1);
		}
		private boolean isEditRowComplete() {
		    return
			!identifier.equals("") &&
			!value.equals("");
		}
		private boolean isIdentifierUnique(String identifier) {
		    return !job.getParameters().contains(identifier);
		}
		private boolean hasIdentifier(int row) {
		    String ident = (String)getValueAt(row, COL_IDENTIFIER);
		    if ((ident == null) || ident.equals(""))
			return false;
		    return true;
		}


		public int getColumnCount() {
		    return COUNT_COL;
		}
		public int getRowCount() {
		    return rowCount;
		}

		public Object getValueAt(int row, int column) {
		    if (isEditRow(row))
			switch (column) {
			case COL_IDENTIFIER: return identifier;
			case COL_VALUE: return value;
			default: return null;
			}
		    else
			switch (column) {
			case COL_IDENTIFIER: return job.getParameters().toArray()[row];
			case COL_VALUE: return job.getParameter((String)getValueAt(row, COL_IDENTIFIER));
			default: return null;
			}
		}
		public void setValueAt(Object obj, int row, int column) {
		    if (isEditRow(row)) {
			switch (column) {
			case COL_IDENTIFIER: 
			    if (isIdentifierUnique((String)obj))
				identifier = (String)obj;
			    break;
			case COL_VALUE:
			    value = (String)obj;
			    break;
			}
			if (isEditRowComplete() &&
			    isIdentifierUnique(identifier)) {
			    
			    job.setParameter(identifier, value);
			    rowCount = job.getParameters().size() + 1;
			    initializeEditRow();
			}
		    } else
			//TODO: parameter verification
			switch (column) {
			case COL_IDENTIFIER:
			    job.renameParameter((String)getValueAt(row, COL_IDENTIFIER), (String)obj);
			    break;
			case COL_VALUE:
			    job.setParameter((String)getValueAt(row, COL_IDENTIFIER), (String)obj);
			    break;
			}

		}
		public String getColumnName(int column) {
		    switch (column) {
		    case COL_IDENTIFIER: return "Identifier";
		    case COL_VALUE: return "Value";
		    default: return "";
		    }
		}
		public Class getColumnClass(int column) {
		    switch (column) {
		    case COL_IDENTIFIER: return String.class;
		    case COL_VALUE: return String.class;
		    default: return null;
		    }
		}
		public boolean isCellEditable(int row, int column) {
		    return true;
		}
	    });

	JTable tableHooks = new JTable();

	templateTabs.addTab("Parameters", new JScrollPane(tableParameters));
	templateTabs.addTab("Hooks", new JScrollPane(tableHooks));

	mainPane.setDividerLocation(0.5);
    }
}
