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
//import java.lang.reflect.*;
import java.lang.*;

import java.util.*;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;

/**
 *
 * @author  margy626
 */
public class ArgoPrintJDialog extends javax.swing.JDialog {

    private Logger log;
    
    /**
     * Sets the logger (log) to logger
     */
    public void setLog(Logger logger){ log = logger; }
    
    /** Creates new form JDialog */
    public ArgoPrintJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jTemplateLabel = new javax.swing.JLabel();
        jTemplateTextField = new javax.swing.JTextField();
        jTemplateBrowseButton = new javax.swing.JButton();
        jOutputFileLabel = new javax.swing.JLabel();
        jOutputFileTextField = new javax.swing.JTextField();
        jOutputFileBrowseButton = new javax.swing.JButton();
        jOutputDirLabel = new javax.swing.JLabel();
        jOutputDirTextField = new javax.swing.JTextField();
        jOutputDirBrowseButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jGenerateButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();

        jTemplateLabel.setText("Template file:");
	
        getContentPane().add(jTemplateLabel);

        jTemplateTextField.setText("myTemplate.xml");
	
        getContentPane().add(jTemplateTextField);

        jTemplateBrowseButton.setText("Browse");
        jTemplateBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTemplateBrowseButtonActionPerformed(evt);
            }
        });

	getContentPane().add(jTemplateBrowseButton);
        jOutputFileLabel.setText("Output file:");
        getContentPane().add(jOutputFileLabel);
        jOutputFileTextField.setText("myOutput.xml");
	
        getContentPane().add(jOutputFileTextField);

        jOutputFileBrowseButton.setText("Browse");
        jOutputFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOutputFileBrowseButtonActionPerformed(evt);
            }
        });

	
        getContentPane().add(jOutputFileBrowseButton);



        jOutputDirLabel.setText("Data storage directory:");
	
        getContentPane().add(jOutputDirLabel);

        jOutputDirTextField.setText("myDataDirectory");

        getContentPane().add(jOutputDirTextField);

        jOutputDirBrowseButton.setText("Browse");
        jOutputDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOutputDirBrowseButtonActionPerformed(evt);
            }
        });

	
        getContentPane().add(jOutputDirBrowseButton);

        jLabel4.setIcon(new javax.swing.ImageIcon("shrew.gif"));
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
	
        getContentPane().add(jLabel4);



        jGenerateButton.setText("Generate");
        jGenerateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGenerateButtonActionPerformed(evt);
            }
        });

	
        getContentPane().add(jGenerateButton);


        jCancelButton.setText("Cancel");
        jCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCancelButtonActionPerformed(evt);
            }
        });


        getContentPane().add(jCancelButton);


	getContentPane().setLayout( new FlowLayout() );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ArgoPrint");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
    }//GEN-END:initComponents

    private void jCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
	//GEN-FIRST:event_jCancelButtonActionPerformed
        // Add your handling code here:
        
        setVisible( false );
        dispose();
    }//GEN-LAST:event_jCancelButtonActionPerformed

    private void jOutputDirBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {
	//GEN-FIRST:event_jOutputDirBrowseButtonActionPerformed
        // Add your handling code here:

        JFileChooser chooser = new JFileChooser();
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        int returnVal = chooser.showOpenDialog( this );
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                jOutputDirTextField.setText( chooser.getSelectedFile().getPath() );
        }
    }//GEN-LAST:event_jOutputDirBrowseButtonActionPerformed

    private void jOutputFileBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {
	//GEN-FIRST:event_jOutputFileBrowseButtonActionPerformed
        // Add your handling code here:
        
        JFileChooser chooser = new JFileChooser();
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        
        int returnVal = chooser.showOpenDialog( this );
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                jOutputFileTextField.setText( chooser.getSelectedFile().getPath() );
        }
    }//GEN-LAST:event_jOutputFileBrowseButtonActionPerformed

    private void jGenerateButtonActionPerformed(java.awt.event.ActionEvent evt) {
	//GEN-FIRST:event_jGenerateButtonActionPerformed
        // Add your handling code here:  
	/******************************************************
         * This is where Engine is invoked!!!!!
         *
         * (Not in the comment ofcourse!)
         ******************************************************/
        
	UMLInterface umlIf = new UMLInterface();
	Environment env = new Environment();

	//setting argopprint for current project
	ProjectBrowser pb = ProjectBrowser.getInstance();
	Project p =  ProjectManager.getManager().getCurrentProject();
	
	umlIf.setLog(log);
	umlIf.setProject(p);
	umlIf.setProjectBrowser(pb);
	umlIf.setOutputDir( jOutputDirTextField.getText() );
	
	//testing "simulated" template
	Object args[] = new Object[1];
	args[0] = p.getModel();
	
	try{
	    log.info("Test simulated template");
	    Object response = 
		umlIf.caller(new String("getOwnedElements(model)"));	  

	    if(response instanceof Collection){		
		ArgoPrintIterator iter =
		    new ArgoPrintIterator(((Collection) response).iterator());
		
		env.addIterator(new String("element"), iter);
		
		while(iter.hasNext()){
		    
		    Object element = iter.next();
		    args[0] = element; 

		    //  Object response2 = 
//  			umlIf.caller(new String("isAClass()"), element);   
		    	    
//  		    if((response2 instanceof Boolean) && 
//  		       (((Boolean)response2).booleanValue())){
			
//  			log.info("Class name: " + 
//  				 ModelFacade.getFacade().getName(element));
			
//  			Object response3 = 
//  			    umlIf.caller(new String("getOperations()"), 
//  					 element);
			
//  			if(response3 instanceof Collection){
//  			    iter = 
//  				new ArgoPrintIterator(((Collection) 
//  						       response3).iterator());
			    
//  			    env.addIterator(new String("operation"), iter);
//  			    while(iter.hasNext()){
//  				Object operation = iter.next();
//  				log.info("operation name: "+ 
//  					 ModelFacade.getFacade().getName(operation));
//  			    }
			    
//  			    env.removeIterator(new String("operation"));
//  			    iter = env.getIterator(new String("element"));
//  			}
//  		    } else{
		    log.info("Element name: " + 
			     ModelFacade.getFacade().getName(element));
			//}
		}
		env.removeIterator(new String("element"));
	    }
	    
	    
	} catch(Exception e){
	    log.info("Gui crash");
	}
	
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
	    e.printStackTrace();
	    return;
	}
	
	JOptionPane.showMessageDialog( this, "Output generation complete!");
    }//GEN-LAST:event_jGenerateButtonActionPerformed

    private void jTemplateBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {
	//GEN-FIRST:event_jTemplateBrowseButtonActionPerformed
        // Add your handling code here:

        JFileChooser chooser = new JFileChooser();
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        
        int returnVal = chooser.showOpenDialog( this );
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                jTemplateTextField.setText(chooser.getSelectedFile().getPath() );
        }

    }//GEN-LAST:event_jTemplateBrowseButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
	//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jOutputDirBrowseButton;
    private javax.swing.JButton jOutputFileBrowseButton;
    private javax.swing.JButton jTemplateBrowseButton;
    private javax.swing.JButton jGenerateButton;
    private javax.swing.JButton jCancelButton;
    private javax.swing.JTextField jOutputDirTextField;
    private javax.swing.JTextField jOutputFileTextField;
    private javax.swing.JTextField jTemplateTextField;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jOutputDirLabel;
    private javax.swing.JLabel jOutputFileLabel;
    private javax.swing.JLabel jTemplateLabel;
    // End of variables declaration//GEN-END:variables
}
