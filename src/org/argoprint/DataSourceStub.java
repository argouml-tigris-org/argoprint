package org.argoprint;

import java.lang.reflect.Method;
import java.util.Vector;

public class DataSourceStub implements ArgoPrintDataSource {
	
	/**
	 * Applies the method with name call with no parameters to the iteratorObject 
	 */ 
	public Object caller(String call, Object iteratorObject)
	throws Exception {
		Object returnValue = null;
		try {
		Method callMethod = iteratorObject.getClass().getMethod(call, new Class[0]);
		returnValue = callMethod.invoke(iteratorObject, new Object[0]);
		} catch (Exception e) {
			System.out.println("Tried to call DataSourceStub with an iterator but failed: " + e.getMessage());
		}
		return returnValue;
	}
	
	public Object caller(String call) 
	throws Exception {
		Vector v = new Vector(5);
		if (call.equals("getString()"))
			return new String("Return value of getString() in DataSourceStub.");
		else if (call.equals("getCollection()")) {
			v.add(0, "object1");
			v.add(1, "object2");
			v.add(2, "object3");
			v.add(3, "object4");
			v.add(4, "object5");
			return v;
		}
		else if (call.equals("getTrueBoolean()"))
			return new Boolean(true);
		else if (call.equals("getFalseBoolean()"))
			return new Boolean(false);
		else
			throw new Exception("Unsupported call.");	
	}
}