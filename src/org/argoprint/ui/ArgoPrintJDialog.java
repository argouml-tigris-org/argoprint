//$Id$
//Copyright (c) 2003, Mikael Albertsson, Mattias Danielsson, Per Engstr�m, 
//Fredrik Gr�ndahl, Martin Gyllensten, Anna Kent, Anders Olsson, 
//Mattias Sideb�ck.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without 
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//  this list of conditions and the following disclaimer.
// 
//* Redistributions in binary form must reproduce the above copyright 
//  notice, this list of conditions and the following disclaimer in the 
//  documentation and/or other materials provided with the distribution.
//
//* Neither the name of the University of Link�ping nor the names of its 
//  contributors may be used to endorse or promote products derived from 
//  this software without specific prior written permission. 
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.

package org.argoprint.ui;

/*
 * ArgoPrintJDialog.java
 *
 * Created on den 17 november 2003, 14:38
 */

import org.argoprint.uml_interface.*;
import org.argoprint.engine.*;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.ui.ProjectBrowser;

import org.argouml.model.ModelFacade;

import java.awt.*;
import java.awt.event.*;

import java.lang.*;

import java.util.*;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;

/**
 *
 * @author  Martin Gyllensten, Per Engstrom
 */
public class ArgoPrintJDialog extends javax.swing.JDialog {

    private Logger log;
    
    /**
     * Sets the logger (log) to logger
     *
     * @param logger a Logger
     */
    public void setLog(Logger logger){ log = logger; }
    
    /**
     * Creates new form JDialog
     *
     * @param parent The parent of the ArgoPrint gui (should be ArgoUML's gui).
     * @param modal not user (hardcoded to true).
     */
    public ArgoPrintJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, true);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * 
     */
    private void initComponents() {
	jUIPanel = new javax.swing.JPanel();
	jButtonPanel = new javax.swing.JPanel();
        jTemplateLabel = new javax.swing.JLabel();
        jTemplateTextField = new javax.swing.JTextField();
        jTemplateBrowseButton = new javax.swing.JButton();
        jOutputFileLabel = new javax.swing.JLabel();
        jOutputFileTextField = new javax.swing.JTextField();
        jOutputFileBrowseButton = new javax.swing.JButton();
        jOutputDirLabel = new javax.swing.JLabel();
        jOutputDirTextField = new javax.swing.JTextField();
        jOutputDirBrowseButton = new javax.swing.JButton();
//      jLabel4 = new javax.swing.JLabel();
        jGenerateButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();

	/** Dialog config */
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ArgoPrint");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

	BorderLayout bl = new BorderLayout();
	getContentPane().setLayout( bl );


	getContentPane().add( jUIPanel );
	getContentPane().add( jButtonPanel, bl.SOUTH );


	/** Init UI Panel */
	GridLayout gl = new GridLayout( 3, 3 );
	jUIPanel.setLayout( gl );

        jTemplateLabel.setText("Template file:");
	
        jUIPanel.add(jTemplateLabel);

        jTemplateTextField.setText("myTemplate.xml");
	
        jUIPanel.add(jTemplateTextField);

        jTemplateBrowseButton.setText("Browse");
        jTemplateBrowseButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    jTemplateBrowseButtonActionPerformed(evt);
		}
        });

	jUIPanel.add(jTemplateBrowseButton);
        jOutputFileLabel.setText("Output file:");
        jUIPanel.add(jOutputFileLabel);
        jOutputFileTextField.setText("myOutput.xml");
	
        jUIPanel.add(jOutputFileTextField);

        jOutputFileBrowseButton.setText("Browse");
        jOutputFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOutputFileBrowseButtonActionPerformed(evt);
            }
        });

	jUIPanel.add(jOutputFileBrowseButton);



        jOutputDirLabel.setText("Data storage directory:");
	
        jUIPanel.add(jOutputDirLabel);

        jOutputDirTextField.setText("myDataDirectory");

        jUIPanel.add(jOutputDirTextField);

        jOutputDirBrowseButton.setText("Browse");
        jOutputDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOutputDirBrowseButtonActionPerformed(evt);
            }
        });
	
        jUIPanel.add(jOutputDirBrowseButton);

        jGenerateButton.setText("Generate");
        jGenerateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGenerateButtonActionPerformed(evt);
            }
        });

	jButtonPanel.add(jGenerateButton);


        jCancelButton.setText("Cancel");
        jCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCancelButtonActionPerformed(evt);
            }
        });

        jButtonPanel.add(jCancelButton);


        pack();
    }

    /**
     * Closes the dialog
     *
     * @param evt action event!!!
     */
    private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
	setVisible( false );
        dispose();
    }

    /**
     * Open file browser
     *
     * @param evt action event!!!
     */
    private void jOutputDirBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {

        JFileChooser chooser = new JFileChooser();
        
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        int returnVal = chooser.showOpenDialog( this );
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                jOutputDirTextField.setText( chooser.getSelectedFile().getPath() );
        }
    }

    /**
     * Open file browser
     *
     * @param evt action event!!!
     */
    private void jOutputFileBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        
        JFileChooser chooser = new JFileChooser();
        
        int returnVal = chooser.showOpenDialog( this );
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                jOutputFileTextField.setText( chooser.getSelectedFile().getPath() );
        }
    }

    /**
     * Open file browser
     *
     * @param evt action event!!!
     */
    private void jTemplateBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {

        JFileChooser chooser = new JFileChooser();
        
        int returnVal = chooser.showOpenDialog( this );
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                jTemplateTextField.setText(chooser.getSelectedFile().getPath() );
        }

    }

    /**
     * Executes a generation
     *
     * @param evt action event!!!
     */
    private void jGenerateButtonActionPerformed(java.awt.event.ActionEvent evt) {
	/******************************************************
         * This is where Engine is invoked!!!!!
         *
         * (Not in the comment ofcourse!)
         ******************************************************/
        
//  	UMLInterface umlIf = new UMLInterface();
//  	Environment env = new Environment();

//  	//setting argopprint for current project
//  	ProjectBrowser pb = ProjectBrowser.getInstance();
//  	Project p =  ProjectManager.getManager().getCurrentProject();
	
//  	umlIf.setLog(log);
//  	umlIf.setProject(p);
//  	umlIf.setProjectBrowser(pb);
//  	umlIf.setOutputDir( jOutputDirTextField.getText() );
	
//  	try{
//  	    log.info("Proj name: " + 
//  		     umlIf.caller(new String("getName(project)")) + 
//  		     " " + umlIf.caller(new String("getName(project)")));
//  	}catch(Exception e){
//  	    JOptionPane.showMessageDialog( this, e.getMessage()); 
//  	}
	

	Settings settings = new Settings(jTemplateTextField.getText(),
					 jOutputFileTextField.getText(),
					 jOutputDirTextField.getText());
	
	Main main = new Main();
	try{
	    main.initializeSystem(settings, log);
	    main.go();
	}
	catch(Exception e){
	    JOptionPane.showMessageDialog( this, e.getMessage());   
	    //e.printStackTrace();
	    return;
	}
	
	JOptionPane.showMessageDialog( this, "Output generation complete!");
    }

    /**
     * Closes the dialog
     *
     * @param evt window event!!!
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    /** main panel */
    private javax.swing.JPanel jUIPanel;
    /** the button panel */
    private javax.swing.JPanel jButtonPanel;
    /** browse directory button */
    private javax.swing.JButton jOutputDirBrowseButton;
    /** output file button */
    private javax.swing.JButton jOutputFileBrowseButton;
    /** template file button */
    private javax.swing.JButton jTemplateBrowseButton;
    /** generation executor */
    private javax.swing.JButton jGenerateButton;
    /** cancel button */
    private javax.swing.JButton jCancelButton;
    /** directory text field */
    private javax.swing.JTextField jOutputDirTextField;
    /** output text field */
    private javax.swing.JTextField jOutputFileTextField;
    /** template text field */
    private javax.swing.JTextField jTemplateTextField;
    /** THE PYGMY SHREW!!! */
//  private javax.swing.JLabel jLabel4;
    /** output dir label */
    private javax.swing.JLabel jOutputDirLabel;
    /** output file label */
    private javax.swing.JLabel jOutputFileLabel;
    /** template label */
    private javax.swing.JLabel jTemplateLabel;
}
