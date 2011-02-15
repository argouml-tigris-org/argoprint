/* $Id$
 *******************************************************************************
 * Copyright (c) 2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mfortner
 *******************************************************************************
 */

package org.argoprint.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.argouml.ui.ProjectBrowser;

/**
 * This class is responsible for executing a list of tasks (classes which
 * implement the Executable interface).
 * 
 * @author mfortner
 */
public class Executor {

    private static final Logger LOG = Logger.getLogger(Executor.class);

    List<Executable> executableList = new ArrayList<Executable>();
    Map<String, Executable> execMap = new HashMap<String, Executable>();
    Map<String, Throwable> exceptionMap = new HashMap<String, Throwable>();

    ThreadPoolExecutor threadPool = null;

    List<Throwable> exceptionList = new ArrayList<Throwable>();

    private String successMsg;

    private String failMsg;

    private String failTitle;

    private Component parent;

    /**
     * Constructor
     */
    public Executor() {
        this(5, 20, 0);
    }
    
    /**
     * Constructor
     * @param initThreadPoolSize
     * @param maxThreadPoolSize
     * @param maxQueueSize
     */
    public Executor(int initThreadPoolSize, int maxThreadPoolSize, int maxQueueSize){
        
        LinkedBlockingQueue queue = (maxQueueSize > 0)?new LinkedBlockingQueue(maxQueueSize):new LinkedBlockingQueue();
        
        threadPool = new ThreadPoolExecutor(initThreadPoolSize, maxThreadPoolSize, 1,
                TimeUnit.SECONDS,queue);
    }
    
    
    public Executor(Component parent, String successMsg, String failMsg, String failTitle){
        this(5,20, 0);
        this.successMsg = successMsg;
        this.failMsg = failMsg;
        this.failTitle = failTitle;
        this.parent = parent;
        
    }

    /**
     * Adds an executable to the list.
     * 
     * @param exec
     */
    public void addExecutable(Executable exec) {
        executableList.add(exec);
        execMap.put(exec.getName(), exec);
    }

    /**
     * Sets the list of executables.
     * 
     * @param execList Executable list.
     */
    public void setExecutableList(List<Executable> execList) {
        for(Executable exec:execList){
            addExecutable(exec);
        }
    }

    /**
     * Indicates whether or not any of the tasks have had exceptions.
     * @return
     */
    public boolean hasExceptions() {
        return !exceptionList.isEmpty();
    }

    /**
     * Gets a list of exceptions that have occurred.
     * @return
     */
    public List<Throwable> getExceptionList() {
        return exceptionList;
    }

    /**
     * This method executes all of the executables currently within its list.
     */
    public void execAll() {

        // submit the executable tasks to the threadpool
        List<Future> futureList = new ArrayList<Future>();
        for (Executable task : executableList) {
            System.out.println("executing: "+ task.getName());
            futureList.add(threadPool.submit(task));
        }

        // wait until all tasks have finished running.
        boolean finished = false;
        while (!finished) {
            finished = true;
            for (Future future : futureList) {
                finished &= future.isDone();
            }
            if (!finished) {
                try {
                    Thread.currentThread().wait(200);
                } catch (InterruptedException e) {
                    LOG.error("Exception", e);
                }
            }
        }

        // gather any exceptions;
        for (Executable task : executableList) {
            if (task.hasException()) {
                this.exceptionList.add(task.getException());
                this.exceptionMap.put(task.getName(), task.getException());
            }
        }
        
        if (hasExceptions()) {
            
            MultiExceptionDialog ex = new MultiExceptionDialog(
                    parent, failTitle, failMsg,
                    getExceptionList());
            ex.setVisible(true);
            LOG.error(collectExceptionMsgs());

        } else {
            JOptionPane
                    .showMessageDialog(
                            parent,successMsg);
        }

    }
    
    /**
     * Aggregates all of the exception messages into a single message
     * in order to display the exception messages.
     * @return
     */
    public String collectExceptionMsgs(){
        StringBuilder sb = new StringBuilder();
        for(Throwable ex:exceptionList){
            sb.append(ex.getMessage() + "\n");
        }
        return sb.toString();
    }

}
