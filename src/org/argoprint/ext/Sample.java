package org.argoprint.ext;

import java.util.Date;

public class Sample {
    public static String stamp() {
	return (new Date()).toString();
    }
}
