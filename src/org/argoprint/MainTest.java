package org.argoprint;

import org.argoprint.engine.Main;

public class MainTest {
	public static void main(String args[]) {
		Main main = new Main();
		try {
			main.initializeSystem("/data/anders/d/pum/kod/iteratortest.xml", "/data/anders/d/pum/kod/output.xml","");
			main.go();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
