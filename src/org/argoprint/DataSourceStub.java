package org.argoprint;

import java.util.Vector;

public class DataSourceStub implements ArgoPrintDataSource {

	public Object caller(String call, Object iteratorObject)
	throws Exception {
		return commonCaller(call);
	}
	
	public Object caller(String call) 
	throws Exception {
		return commonCaller(call);
	}
	
	private Object commonCaller(String call)
	throws Exception {
		if (call.equals("getString()"))
			return new String("Return value of getString() in DataSourceStub.");
		else if (call.equals("getCollection()"))
			return new Vector();
		else if (call.equals("getTrueBoolean()"))
			return new Boolean(true);
		else if (call.equals("getFalseBoolean()"))
			return new Boolean(false);
		else
			throw new Exception("Unsupported call.");
	
	}
	
}