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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.argoprint.ArgoPrintManagerModel;

public class ArgoPrintManager
    extends JPanel {

    private static final int IDN_COUNT = 4;
    private static final int IDN_COL_SELECTED = 0;
    private static final int IDN_COL_IDENTIFIER = 1;
    private static final int IDN_COL_TEMPLATE = 2;
    private static final int IDN_COL_OUTPUT = 3;

    private static final int PAR_COUNT = 2;
    private static final int PAR_COL_IDENTIFIER = 0;
    private static final int PAR_COL_VALUE = 1;

    private ArgoPrintManagerModel model;

    private AbstractAction
	actionEditTemplate,
	actionRemoveIdentifier,
	actionRemoveParameter,
	actionSelectOutput,
	actionSelectTemplate;

    private JTable
	tableIdentifiers,
	tableParameters;

    private JTextField
	fieldLocation;

    public ArgoPrintManager(ArgoPrintManagerModel model) {
	super();
	this.model = model;
	initializeActions();
	initializeComponents();
    }

    private void initializeActions() {
	final Component parent = this;

	actionEditTemplate = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    JOptionPane
			.showMessageDialog(parent,
					   "Not Implemented yet.");
		}
	    };
	actionEditTemplate.putValue(AbstractAction.NAME, "Edit");

	actionRemoveIdentifier = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    int row = tableIdentifiers.getSelectedRow();

		    if (!isEditRow(tableIdentifiers, row)) {
			model.removeJob(getIdentifier());
			((AbstractTableModel) tableIdentifiers.getModel())
			    .fireTableRowsDeleted(row, row);
		    }
		}
	    };
	actionRemoveIdentifier.putValue(AbstractAction.NAME, "Remove");

	actionRemoveParameter = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    int row = tableParameters.getSelectedRow();
		    if (!isEditRow(tableParameters, row)) {
			
			model.getJob(getIdentifier())
			    .removeParameter(getParameter());
			
			((AbstractTableModel) tableParameters.getModel())
			    .fireTableRowsDeleted(row, row);
		    }
		}
	    };
	actionRemoveParameter.putValue(AbstractAction.NAME, "Remove");

	actionSelectOutput = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    if (JFileChooser.APPROVE_OPTION
			== chooser.showDialog(parent, "Select Output")) {
			try {
			    tableIdentifiers
				.setValueAt(chooser.getSelectedFile().toURL(),
					    tableIdentifiers.getSelectedRow(),
					    IDN_COL_OUTPUT);
			} catch (java.net.MalformedURLException ex) {
			    // TODO: (should not happen)
			    ex.printStackTrace();
			}
		    }

		}
	    };
	actionSelectOutput.putValue(AbstractAction.NAME, "Output");

	actionSelectTemplate = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    if (JFileChooser.APPROVE_OPTION
			== chooser.showDialog(parent, "Select Template")) {
			try {
			    tableIdentifiers
				.setValueAt(chooser.getSelectedFile().toURL(),
					    tableIdentifiers.getSelectedRow(),
					    IDN_COL_TEMPLATE);
			} catch (java.net.MalformedURLException ex) {
			    // TODO: (should not happen)
			    ex.printStackTrace();
			}
		    }
			
		}
	    };
	actionSelectTemplate.putValue(AbstractAction.NAME, "Template");
    }

    private void initializeComponents() {
	final Component parent = this;

	setLayout(new BorderLayout());
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	/* Proportional division does not work as the Panel has height 0 */
	mainPane.setDividerLocation(200);
	mainPane.setResizeWeight(0.90);

	add(mainPane, BorderLayout.CENTER);

	tableIdentifiers = new JTable() {
		public void setValueAt(Object aValue, int row, int column) {
		    if (aValue instanceof String)
			aValue = ((String) aValue).trim();
		    
		    super.setValueAt(aValue, row, column);
		}
	    };

	tableIdentifiers
	    .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

	tableIdentifiers.setModel(new AbstractTableModel() {
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

		private boolean isEditRowComplete() {
		    return
			!identifier.equals("")
			&& (template != null)
			&& (output != null);
		}

		private boolean isIdentifierUnique(String identifier) {
		    return !model.getIdentifiers().contains(identifier);
		}

		private boolean hasIdentifier(int row) {
		    String ident = (String) getValueAt(row, IDN_COL_IDENTIFIER);
		    if ((ident == null) || ident.equals(""))
			return false;
		    return true;
		}

		// interface
		public int getColumnCount() {
		    return IDN_COUNT;
		}

		public int getRowCount() {
		    return model.getIdentifiers().size()
			// and the edit row
			+ 1;
		}
		public Object getValueAt(int row, int column) {
		    if (isEditRow(tableIdentifiers, row)) {
			switch (column) {
			case IDN_COL_SELECTED:
			    return selected;
			case IDN_COL_IDENTIFIER:
			    return identifier;
			case IDN_COL_TEMPLATE:
			    return template;
			case IDN_COL_OUTPUT:
			    return output;
			default: return null;
			}
		    } else {
			switch (column) {
			case IDN_COL_SELECTED:
			    return model
				.getJob((String) getValueAt(row,
							    IDN_COL_IDENTIFIER))
				.getSelected();
			case IDN_COL_IDENTIFIER:
			    // TODO: find another random-access to identifier
			    return model.getIdentifiers().toArray()[row];
			case IDN_COL_TEMPLATE:
			    return model
				.getJob((String) getValueAt(row,
							    IDN_COL_IDENTIFIER))
				.getTemplate();
			case IDN_COL_OUTPUT:
			    return model
				.getJob((String) getValueAt(row,
							    IDN_COL_IDENTIFIER))
				.getOutput();
			default: return null;
			}
		    }
		}
		public void setValueAt(Object obj, int row, int column) {
		    if (isEditRow(tableIdentifiers, row)) {
			switch (column) {
			case IDN_COL_SELECTED:
			    selected = (Boolean) obj;
			    break;
			case IDN_COL_IDENTIFIER:
			    if (isIdentifierUnique((String) obj))
				identifier = (String) obj;
			    break;
			case IDN_COL_TEMPLATE:
			    template = (URL) obj;
			    break;
			case IDN_COL_OUTPUT:
			    output = (URL) obj;
			    break;
			}
			if (isEditRowComplete()
			    && isIdentifierUnique(identifier)) {
			    model.addJob(model.new
					 TemplateJob(identifier,
						      template,
						      output,
						      selected));
			    initializeEditRow();
			    // we don't know were the row was actually inserted
			    fireTableDataChanged();
			    return;
			}
		    } else {
			switch (column) {
			case IDN_COL_SELECTED:
			    model.getJob((String) getValueAt(row,
							     IDN_COL_IDENTIFIER))
				.setSelected((Boolean) obj);
			    break;
			case IDN_COL_IDENTIFIER:
			    model.getJob((String) getValueAt(row,
							     IDN_COL_IDENTIFIER))
				.setIdentifier((String) obj);
			    fireTableDataChanged();
			    break;
			case IDN_COL_TEMPLATE:
			    model.getJob((String) getValueAt(row,
							     IDN_COL_IDENTIFIER))
				.setTemplate((URL) obj);
			    break;
			case IDN_COL_OUTPUT:
			    model.getJob((String) getValueAt(row,
							     IDN_COL_IDENTIFIER))
				.setOutput((URL) obj);
			    break;
			}

		    }
		    fireTableCellUpdated(row, column);
		}

		public String getColumnName(int column) {
		    switch (column) {
		    case IDN_COL_IDENTIFIER:
			return "Identifier";
		    case IDN_COL_TEMPLATE:
			return "Template URL";
		    case IDN_COL_OUTPUT:
			return "Output URL";
		    default:
			return "";
		    }
		}
		public Class getColumnClass(int column) {
		    switch (column) {
		    case IDN_COL_SELECTED:
			return Boolean.class;
		    case IDN_COL_IDENTIFIER:
			return String.class;
		    case IDN_COL_TEMPLATE:
			return URL.class;
		    case IDN_COL_OUTPUT:
			return URL.class;
		    default:
			return String.class;
		    }
		}
		public boolean isCellEditable(int row, int column) {
		    return hasIdentifier(row)
			|| isEditRow(tableIdentifiers, row);
		}
	    });

	tableIdentifiers.getColumnModel().getColumn(IDN_COL_IDENTIFIER)
	    .setCellEditor(new DefaultCellEditor(new JTextField()) {
		private String previousValue;

		public Component getTableCellEditorComponent(JTable table,
							     Object value,
							     boolean isSelected,
							     int row,
							     int column) {
		    ((JComponent) getComponent())
			.setBorder(new LineBorder(Color.BLACK));

		    previousValue = (String) value;
		    return super.getTableCellEditorComponent(table,
							     value,
							     isSelected,
							     row,
							     column);
		}

		public boolean stopCellEditing() {
		    boolean result;
		    String newValue = ((String) super.getCellEditorValue())
			.trim();
		    
		    result = !model.getIdentifiers().contains(newValue)
			|| previousValue.equals(newValue);
		    
		    if (result)
			super.stopCellEditing();
		    else {
			((JComponent) getComponent())
			    .setBorder(new LineBorder(Color.RED));

			JOptionPane
			    .showMessageDialog(parent,
					       "The identifier must be unique and the one"
					       + "you selected is already in the list.",
					       "Error",
					       JOptionPane.ERROR_MESSAGE);
		    }
		    return result;
		}
	    });

	tableIdentifiers
	    .setDefaultEditor(URL.class, new DefaultCellEditor(new JTextField()) {
		    private Object value;
		    
		    public Component
			getTableCellEditorComponent(JTable table,
						    Object value,
						    boolean isSelected,
						    int row,
						    int column) {
			((JComponent) getComponent())
			    .setBorder(new LineBorder(Color.BLACK));
			
			return super.getTableCellEditorComponent(table,
								 value,
								 isSelected,
								 row,
								 column);
		    }
		    
		    
		    public boolean stopCellEditing() {
			boolean result = true;;
			
			try {
			    URL url = new URL((String) super
					      .getCellEditorValue());
			    java.net.URI uri = url.toURI();
			    result = 
				"file".equals(uri.getScheme().toLowerCase())
				&& (uri.getAuthority() == null);
			    
			    value = url;
			} catch (java.net.MalformedURLException ex) {
			    result = false;
			} catch (java.net.URISyntaxException ex) {
			    result = false;
			}
		    
			if (result)
			    super.stopCellEditing();
			else {
			    ((JComponent) getComponent())
				.setBorder(new LineBorder(Color.RED));

			    JOptionPane
				.showMessageDialog(parent,
						   "Invalid URL. Consider using the file"
						   + " chooser available in the context menu.",
						   "Error",
						   JOptionPane.ERROR_MESSAGE);
			}
			return result;
		    }
		    public Object getCellEditorValue() {
			return value;
		    }
		});
	
	tableIdentifiers.getColumnModel().getColumn(0).setMaxWidth(0);

	tableIdentifiers.addMouseListener(new MouseAdapter() {
		private JPopupMenu contextmenu;
		//initialize
		{
		    JMenu selectMenu = new JMenu("Select");
		    selectMenu.add(actionSelectTemplate);
		    selectMenu.add(actionSelectOutput);
		    
		    contextmenu = new JPopupMenu();
		    contextmenu.add(actionEditTemplate);
		    contextmenu.add(actionRemoveIdentifier);
		    contextmenu.add(selectMenu);
		}
		public void mouseClicked(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON3) {
			// Cancel editing first
			int editRow, editCol;
			if (((editRow
			      = tableIdentifiers.getEditingRow())
			     != -1)

			    && ((editCol
				 = tableIdentifiers.getEditingColumn())
				!= -1)) {
			    
			    tableIdentifiers
				.getCellEditor(editRow, editCol)
				.cancelCellEditing();
			}


			int row = tableIdentifiers
			    .rowAtPoint(new Point(e.getX(), e.getY()));
			tableIdentifiers.setRowSelectionInterval(row, row);
			if (isEditRow(tableIdentifiers, row)) {
			    actionEditTemplate.setEnabled(false);
			    actionRemoveIdentifier.setEnabled(false);
			} else {
			    actionEditTemplate.setEnabled(true);
			    actionRemoveIdentifier.setEnabled(true);
			}
			contextmenu.show((Component) e.getSource(),
					 e.getX(),
					 e.getY());
		    }
		}

	    });

	JTabbedPane templateTabs = new JTabbedPane();

	tableParameters = new JTable();

	tableParameters
	    .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	tableParameters.setModel(new AbstractTableModel() {
		private String identifier, value;
		private ArgoPrintManagerModel.TemplateJob job;
		private int rowCount;

		// initialization
		{
		    initializeEditRow();

 		    tableIdentifiers
			.getSelectionModel()
			.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
				    updateModel();
				}
			    });
		}

		/** Updates the model of this "proxy-model" */
		private void updateModel() {
		    if ((tableIdentifiers.getSelectedRowCount() == 0)
			|| (isEditRow(tableIdentifiers,
				      tableIdentifiers.getSelectedRow())))
			rowCount = 0;
		    else {
			int selectedRow = tableIdentifiers.getSelectedRow();
			job = model.getJob(getIdentifier());
			rowCount = job.getParameters().size() 
			    // and the edit row
			    + 1;
		    }

		    fireTableDataChanged();
		}
		private void initializeEditRow() {
		    identifier = "";
		    value = "";
		}
		private boolean isEditRowComplete() {
		    return
			!identifier.equals("")
			&& !value.equals("");
		}
		private boolean isIdentifierUnique(String identifier) {
		    return !job.getParameters().contains(identifier);
		}
		private boolean hasIdentifier(int row) {
		    String ident = (String) getValueAt(row, PAR_COL_IDENTIFIER);
		    if ((ident == null) || ident.equals(""))
			return false;
		    return true;
		}

		public int getColumnCount() {
		    return PAR_COUNT;
		}
		public int getRowCount() {
		    return rowCount;
		}

		public Object getValueAt(int row, int column) {
		    if (isEditRow(tableParameters, row))
			switch (column) {
			case PAR_COL_IDENTIFIER:
			    return identifier;
			case PAR_COL_VALUE:
			    return value;
			default:
			    return null;
			}
		    else {
			switch (column) {
			case PAR_COL_IDENTIFIER:
			    return job.getParameters().toArray()[row];
			case PAR_COL_VALUE:
			    return job
				.getParameter((String) getValueAt(row,
								  PAR_COL_IDENTIFIER));
			default:
			    return null;
			}
		    }
		}
		public void setValueAt(Object obj, int row, int column) {
		    if (isEditRow(tableParameters, row)) {
			switch (column) {
			case PAR_COL_IDENTIFIER: 
			    if (isIdentifierUnique((String) obj))
				identifier = (String) obj;
			    break;
			case PAR_COL_VALUE:
			    value = (String) obj;
			    break;
			}
			if (isEditRowComplete()
			    && isIdentifierUnique(identifier)) {
			    
			    job.setParameter(identifier, value);
			    rowCount = job.getParameters().size() + 1;
			    initializeEditRow();

			    fireTableDataChanged();
			    return;
			}
		    } else {
			switch (column) {
			case PAR_COL_IDENTIFIER:
			    job.renameParameter((String) getValueAt(row,
								    PAR_COL_IDENTIFIER),
						(String) obj);
			    break;
			case PAR_COL_VALUE:
			    job.setParameter((String) getValueAt(row,
								 PAR_COL_IDENTIFIER),
					     (String) obj);
			    break;
			}
			fireTableCellUpdated(row, column);
		    }

		}
		public String getColumnName(int column) {
		    switch (column) {
		    case PAR_COL_IDENTIFIER:
			return "Identifier";
		    case PAR_COL_VALUE:
			return "Value";
		    default: 
			return "";
		    }
		}
		public Class getColumnClass(int column) {
		    switch (column) {
		    case PAR_COL_IDENTIFIER:
			return String.class;
		    case PAR_COL_VALUE:
			return String.class;
		    default:
			return null;
		    }
		}
		public boolean isCellEditable(int row, int column) {
		    return true;
		}

		public void fireTableRowsDeleted(int firstRow, int lastRow) {
		    updateModel();
		    super.fireTableRowsDeleted(firstRow, lastRow);
		}
						    
	    });

	tableParameters.getColumnModel().getColumn(PAR_COL_IDENTIFIER)
	    .setCellEditor(new DefaultCellEditor(new JTextField()) {
		    private String previousValue;
		    
		    public Component getTableCellEditorComponent(JTable table,
								 Object value,
								 boolean isSelected,
								 int row,
								 int column) {
			((JComponent) getComponent())
			    .setBorder(new LineBorder(Color.BLACK));
			
			previousValue = (String) value;
			return super.getTableCellEditorComponent(table,
								 value,
								 isSelected,
								 row,
								 column);
		    }

		    public boolean stopCellEditing() {
			boolean result;
			String newValue = ((String) super.getCellEditorValue()).trim();
		    
			result = !model.getJob(getIdentifier()).getParameters().contains(newValue)
			    || previousValue.equals(newValue);
		    
			if (result)
			    super.stopCellEditing();
			else {
			    ((JComponent) getComponent())
				.setBorder(new javax.swing.border.LineBorder(java.awt.Color.RED));

			    JOptionPane.showMessageDialog(parent,
							  "The identifier must be unique and the one you selected is already in the list.",
							  "Error",
							  JOptionPane.ERROR_MESSAGE);
			}
			return result;
		    }
		});

	tableParameters.addMouseListener(new MouseAdapter() {
		private JPopupMenu contextmenu;

		{
		    contextmenu = new JPopupMenu();
		    contextmenu.add(actionRemoveParameter);
		}

		public void mouseClicked(MouseEvent e) {
		    if (e.getButton() == MouseEvent.BUTTON3) {
			// Cancel editing first
			int editRow, editCol;
			if (( (editRow = tableParameters.getEditingRow()) != -1)
			    && ( (editCol = tableParameters.getEditingColumn()) != -1)) {
			    
			    tableParameters.getCellEditor(editRow, editCol).cancelCellEditing();
			}

			int row = tableParameters.rowAtPoint(new Point(e.getX(), e.getY()));
			tableParameters.setRowSelectionInterval(row, row);
			if (!isEditRow(tableParameters, row))
			    contextmenu.show((Component) e.getSource(), e.getX(), e.getY());
		    }
		}

	    });


	JTable tableHooks = new JTable();

	templateTabs.addTab("Parameters", new JScrollPane(tableParameters));
	templateTabs.addTab("Hooks", new JScrollPane(tableHooks));

	mainPane.setTopComponent(new JScrollPane(tableIdentifiers));
	mainPane.getTopComponent().setMinimumSize(new Dimension(0, 0));
	mainPane.setBottomComponent(templateTabs);
	mainPane.getBottomComponent().setMinimumSize(new Dimension(0, 0));
    }

    private boolean isEditRow(JTable table, int row) {
	return (row == table.getRowCount() - 1);
    }

    private String getIdentifier() {
	int row = tableIdentifiers.getSelectedRow();
	if (row != -1)
	    return (String) tableIdentifiers.getValueAt(row, IDN_COL_IDENTIFIER);
	else
	    return null;
    }
    
    private String getParameter() {
	int row = tableParameters.getSelectedRow();
	if (row != -1)
	    return (String) tableParameters.getValueAt(row, PAR_COL_IDENTIFIER);
	else
	    return null;
    }

}
