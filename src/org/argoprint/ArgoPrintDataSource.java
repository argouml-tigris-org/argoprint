package org.argoprint;

/**
 * An interface that should be implemented by a class that acts as a data source 
 * for ArgoPrint.
 */
public abstract interface ArgoPrintDataSource{
    abstract public Object caller(String call, Object iteratorObject) throws Exception;
	abstract public Object caller(String call) throws Exception;
}
