package org.argoprint.ext;

import java.util.Date;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;

public class Sample {
    public static String stamp() {
	return (new Date()).toString();
    }
}
