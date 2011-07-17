// $Id$
// Copyright (c) 2011 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.argoprint.persistence.TemplateMetaFile;

@SuppressWarnings("serial")
public class TemplateTable extends JTable {
    
    /** The width of the selected column */
    private static final int SELECTED_WIDTH = 30;
    
    /** The width of the group column */
    private static final int GROUP_WIDTH = 60;

    /** The width of the category column */
    private static final int CATEGORY_WIDTH = 80;

    /** The width of the template column */
    private static final int TEMPLATE_WIDTH = 120;
    
    /** The width of the output column */
    private static final int OUTPUT_WIDTH = 120;

    /** The width of the description column */
    private static final int DESC_WIDTH = 210;
    
   
    
    private static final Logger LOG = Logger.getLogger(TemplateTable.class);

    public TemplateTable(TemplateTableModel model) {
        super(model);
        init();
    }

    private void init() {

        this.setRowHeight(30);

        TableColumn selectedColumn = this.getColumnModel().getColumn(
                TemplateTableModel.SELECTED);
        selectedColumn.setPreferredWidth(SELECTED_WIDTH);
        selectedColumn.setMaxWidth(SELECTED_WIDTH);

        // create group column editor
        TableColumn groupColumn = this.getColumnModel().getColumn(
                TemplateTableModel.GROUP);
        groupColumn.setCellEditor(new ComboBoxEditor(
                TemplateTableModel.DEFAULT_GROUPS));
        groupColumn.setPreferredWidth(GROUP_WIDTH);
        groupColumn.setMaxWidth(GROUP_WIDTH);

        // create category column editor
        TableColumn catColumn = this.getColumnModel().getColumn(
                TemplateTableModel.CATEGORY);
        catColumn.setCellEditor(new ComboBoxEditor(
                TemplateTableModel.DEFAULT_CATEGORIES));
        catColumn.setPreferredWidth(CATEGORY_WIDTH);
        catColumn.setMaxWidth(CATEGORY_WIDTH);

        // create output file renderer
        TableColumn outputColumn = this.getColumnModel().getColumn(
                TemplateTableModel.OUTPUT_FILE);
        outputColumn.setCellRenderer(new ToolTipRenderer());
        outputColumn.setPreferredWidth(OUTPUT_WIDTH);

        // create template renderer
        TableColumn templateColumn = this.getColumnModel().getColumn(
                TemplateTableModel.TEMPLATE_FILE);
        templateColumn.setCellRenderer(new ToolTipRenderer());
        templateColumn.setCellEditor(new FileBrowseEditor(
                "Select Template File"));
        templateColumn.setPreferredWidth(TEMPLATE_WIDTH);

        // create description column
        TableColumn descColumn = this.getColumnModel().getColumn(
                TemplateTableModel.DESCRIPTION);
        descColumn.setCellRenderer(new ToolTipRenderer());
        descColumn.setPreferredWidth(DESC_WIDTH);
    }

    /**
     * This method adds a template to the table.
     * 
     * @param template The template to be added.
     */
    public void addTemplate(TemplateMetaFile template) {
        TemplateTableModel model = (TemplateTableModel) this.getModel();
        model.addTemplate(template);
    }

    /**
     * A TableCellEditor that provides a means for selecting files
     * 
     * @author mfortner
     */
    class FileBrowseEditor implements TableCellEditor {

        JPanel panel = new JPanel();

        JTextField textFld = new JTextField();

        JButton button = new JButton("...");

        JFileChooser chooser = new JFileChooser();

        String chooserDialogTitle = null;

        ChangeEvent changeEvent = null;

        Object currValue;

        List<CellEditorListener> listeners = new ArrayList<CellEditorListener>();

        JTable table = null;

        static final int HEIGHT = 24;

        static final int WIDTH = 120;

        /**
         * Constructor
         * 
         * @param chooserDialogTitle
         */
        public FileBrowseEditor(String chooserDialogTitle) {

            textFld.setPreferredSize(new Dimension(85, 22));

            this.chooserDialogTitle = chooserDialogTitle;
            panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            panel.add(textFld);
            panel.add(button);
            panel.setAlignmentX(LEFT_ALIGNMENT);

            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setDialogTitle(this.chooserDialogTitle);

            button.setPreferredSize(new Dimension(25, HEIGHT));
            button.setAction(new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    int state = chooser.showOpenDialog(panel);
                    if (state == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        textFld.setText(file.toURI().toString());
                        stopCellEditing();
                    }

                }
            });
        }

        /**
         * Gets the table cell editor component
         * 
         * @param table
         * @param value
         * @param isSelected
         * @param row
         * @param column
         * @return
         * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
         *      java.lang.Object, boolean, int, int)
         */
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {

            this.table = table;
            currValue = value;
            this.textFld.setText((String) currValue);

            return this.panel;
        }

        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        public Object getCellEditorValue() {
            currValue = this.textFld.getText();
            return currValue;
        }

        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        public boolean stopCellEditing() {
            fireEditingStopped();

            return true;
        }

        protected void fireEditingCanceled() {
            for (CellEditorListener listener : listeners) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                listener.editingCanceled(changeEvent);
            }
        }

        protected void fireEditingStopped() {

            for (CellEditorListener listener : listeners) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                listener.editingStopped(changeEvent);
            }
        }

    }

    /**
     * A TableCellEditor that uses a combobox.
     * 
     * @author mfortner
     */
    class ComboBoxEditor extends JComboBox implements TableCellEditor {

        List<CellEditorListener> listeners = Collections
                .synchronizedList(new ArrayList<CellEditorListener>());

        Object currValue;

        JTable table = null;

        transient protected ChangeEvent changeEvent = null;

        /**
         * Constructor
         * 
         * @param values The values in the list box
         */
        public ComboBoxEditor(String[] values) {
            this.setModel(new DefaultComboBoxModel(values));
            this.setEditable(true);
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {

            currValue = value;
            this.setSelectedItem(currValue);
            this.table = table;

            return this;
        }

        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        public Object getCellEditorValue() {
            currValue = this.getSelectedItem();
            return currValue;
        }

        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        protected void fireEditingCanceled() {
            List<CellEditorListener> shortList = listeners.subList(0, listeners
                    .size());
            for (CellEditorListener listener : shortList) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                listener.editingCanceled(changeEvent);
            }
        }

        protected void fireEditingStopped() {
            List<CellEditorListener> shortList = listeners.subList(0, listeners
                    .size());
            for (CellEditorListener listener : shortList) {

                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                listener.editingStopped(changeEvent);
            }
        }

    }

    class ToolTipRenderer extends DefaultTableCellRenderer {

        public ToolTipRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);

            super.setToolTipText((String) value);

            return this;
        }

    }

}
