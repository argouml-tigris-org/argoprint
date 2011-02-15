// $Id$
// Copyright (c) 2010 The Regents of the University of California. All
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.argoprint.persistence.TemplateMetaFile;
import org.argouml.cognitive.Translator;

/**
 * This class represents the contents of the TemplateTable. This contains the
 * template metadata.
 */
public class TemplateTableModel implements TableModel {

    /** The list of templates. */
    private List<TemplateMetaFile> templateList = new ArrayList<TemplateMetaFile>();

    /** The list of listeners */
    private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    /** The names of each of the columns */
    private String[] columnNames = new String[] {
            Translator.localize("argoprint.table.selected"),
            Translator.localize("argoprint.table.group"),
            Translator.localize("argoprint.table.category"),
            Translator.localize("argoprint.table.name"),
            Translator.localize("argoprint.table.template"),
            Translator.localize("argoprint.table.output"),
            Translator.localize("argoprint.table.description"),
            Translator.localize("argoprint.table.type") };

    /** The index of the "selected" column */
    public static final int SELECTED = 0;

    /** The index of the "group" column */
    public static final int GROUP = 1;

    /** The index of the "category" column */
    public static final int CATEGORY = 2;

    /** The index of the "name" column */
    public static final int NAME = 3;

    /** The index of the "template file" column */
    public static final int TEMPLATE_FILE = 4;

    /** The index of the "output file" column */
    public static final int OUTPUT_FILE = 5;

    /** The index of the "description" column */
    public static final int DESCRIPTION = 6;

    /** The index of the "type" column */
    public static final int TYPE = 7;

    public static final String[] DEFAULT_GROUPS = new String[] { "ArgoUML",
            Translator.localize("argoprint.groups.company"),
            Translator.localize("argoprint.groups.dept") };

    public static final String[] DEFAULT_CATEGORIES = new String[] {
            Translator.localize("argoprint.categories.requirements"),
            Translator.localize("argoprint.categories.design"),
            Translator.localize("argoprint.categories.deployment") };

    /**
     * Constructor
     * 
     * @param templateList A list of templates
     */
    public TemplateTableModel(List<TemplateMetaFile> templateList) {
        this.templateList = templateList;
    }

    /**
     * Constructor
     */
    public TemplateTableModel() {
        this.templateList.add(new TemplateMetaFile());
    }

    /**
     * Adds a table model listener.
     * 
     * @param listener
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(TableModelListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Gets the class of a given column
     * 
     * @param columnIndex The index of the column.
     * @return The class of the column
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
        Class<?> clazz = null;
        if (columnIndex == 0) {
            clazz = Boolean.class;
        } else {
            clazz = String.class;
        }
        return clazz;
    }

    /** Gets a count of the number of columns */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Gets the name of a given column
     * 
     * @param columnIndex the index of the column
     * @return the name of the column
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Gets a count of the number of rows in the table.
     * 
     * @return a count of the number of rows in the table
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        int rowCount = 0;
        if (templateList != null && !templateList.isEmpty()) {
            rowCount = templateList.size();
        }
        return rowCount;
    }

    /**
     * Gets the value at a particular cell in the table.
     * 
     * @param rowIndex The row index.
     * @param columnIndex The column index.
     * @return the value of a cell
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        TemplateMetaFile file = this.templateList.get(rowIndex);
        Object val = null;
        if (file != null) {
            switch (columnIndex) {
            case SELECTED: {
                val = file.isSelected();
                break;
            }
            case NAME: {
                val = file.getName();
                break;
            }
            case DESCRIPTION: {
                val = file.getDescription();
                break;
            }
            case GROUP: {
                val = file.getGroup();
                break;
            }
            case CATEGORY: {
                val = file.getCategory();
                break;
            }
            case TEMPLATE_FILE: {
                val = file.getTemplateFile();
                break;
            }
            case OUTPUT_FILE: {
                val = file.getOutputFile();
                break;
            }
            case TYPE: {
                val = file.getType();
                break;
            }
            }
        }

        return val;

    }

    /**
     * Determines if the cell is editable.
     * 
     * @param rowIndex the index of the row.
     * @param columnIndex the index of the column
     * @return true, if the cell is editable
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * Removes a table model listener.
     * 
     * @param listener
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener listener) {
        this.listeners.remove(listener);

    }

    /**
     * Gets a count of the number of selected templates.
     * 
     * @return a count of the selected templates
     */
    public int getSelectedRowCount() {
        int count = 0;
        for (TemplateMetaFile template : templateList) {
            if (template.isSelected()) {
                count++;
            }
        }
        return count;
    }

    /**
     * This method gets a list of selected templates.
     * 
     * @return a list of selected templates
     */
    public List<TemplateMetaFile> getSelectedTemplates() {
        List<TemplateMetaFile> selectedFiles = new ArrayList<TemplateMetaFile>();

        for (TemplateMetaFile file : this.templateList) {
            if (file.isSelected()) {
                selectedFiles.add(file);
            }
        }

        return selectedFiles;
    }

    /**
     * Sets the value of a particular cell in a table.
     * 
     * @param val The new value of a cell.
     * @param rowIndex The index of the row
     * @param columnIndex The index of the column
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object val, int rowIndex, int columnIndex) {
        TemplateMetaFile template = this.templateList.get(rowIndex);

        switch (columnIndex) {
        case SELECTED: {
            template.setSelected((Boolean) val);
            System.out
                    .println(String.format("%s: template selected", template));
            break;
        }
        case NAME: {
            template.setName((String) val);
            break;
        }
        case DESCRIPTION: {
            template.setDescription((String) val);
            break;
        }
        case GROUP: {
            template.setGroup((String) val);
            break;
        }
        case CATEGORY: {
            template.setCategory((String) val);
        }
        case TEMPLATE_FILE: {
            template.setTemplateFile((String) val);
            break;
        }
        case OUTPUT_FILE: {
            template.setOutputFile((String) val);
            break;
        }

        }

    }

    /**
     * Removes a template from the table model.
     * 
     * @param template The template to be removed.
     */
    public void removeTemplate(TemplateMetaFile template) {
        templateList.remove(template);
    }

    /**
     * Adds a template to the table model.
     * 
     * @param template the template to be added.
     */
    public void addTemplate(TemplateMetaFile template) {
        this.templateList.add(template);
        fireTableDataChanged();
    }

    /**
     * Communicates the fact that the table has changed to each of the
     * registered table model listeners.
     * 
     * @param e The table model event.
     */
    public void fireTableChanged(TableModelEvent e) {
        for (TableModelListener listener : listeners) {
            ((TableModelListener) listener).tableChanged(e);
        }
    }

    /**
     * Communicates the fact that the table data has changed to each of the
     * registered table model listeners.
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Gets the TemplateMetaFile object corresponding to the given index.
     * 
     * @param index The row index of the TemplateMetaFile within the table
     * @return a TemplateMetaFile object
     */
    public TemplateMetaFile getRow(int index) {
        return this.templateList.get(index);
    }

    /**
     * Gets templates that are stored locally.
     * 
     * @return a list of the local templates.
     */
    public List<TemplateMetaFile> getLocalTemplates() {
        List<TemplateMetaFile> localTemplates = new ArrayList<TemplateMetaFile>();
        for (TemplateMetaFile template : templateList) {
            if (template.isLocalTemplate()) {
                localTemplates.add(template);
            }
        }
        return localTemplates;
    }
    
    public void selectAll(boolean selected){
        for(TemplateMetaFile template:this.templateList){
            template.setSelected(selected);
        }
        fireTableDataChanged();
    }

}
