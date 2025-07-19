package com.test.aadetector;

import java.io.File;
import java.util.List;

public class ConstructorInitializationRefactor {
	
	public static String refactor(int line) {
		line--;
		
        // Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

        // Read the test file
        List<String> lines = Util.readFile(filePath);

        // Perform test smell refactoring      
        if (lines.get(line).trim().startsWith("public ") && lines.get(line).contains("()")) {
			String leadingSpaces = Util.getLeadingSpaces(lines.get(line));
			lines.set(line, leadingSpaces.concat("public void setUp() {"));
			lines.add(line, leadingSpaces.concat("@Before"));
	    } 

        String result = Util.listToCode(lines);
        
        if(result == null) {
        	return null;
        }
        
        Util.writeStringToFile(result);
        
        System.out.println(result);
        
        return result;
    }
	
	public static String refactorNoPrint(int line) {
		line--;
		
        // Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + File.separator + "output.java";

        // Read the test file
        List<String> lines = Util.readFile(filePath);

        // Perform test smell refactoring
        if (lines.get(line).trim().startsWith("public ") && lines.get(line).contains("()")) {
			String leadingSpaces = Util.getLeadingSpaces(lines.get(line));
			lines.set(line, leadingSpaces.concat("public void setUp() {"));
			lines.add(line, leadingSpaces.concat("@Before"));
	    } 

        String result = Util.listToCode(lines);
        
        if(result == null) {
        	return null;
        }
        
        Util.writeStringToFile(result);
        
//        System.out.println(result);
        
        return result;
    }

	public static String changeLine(String codeLine) {
		if (codeLine.trim().startsWith("public ") && codeLine.contains("()")) {
			String leadingSpaces = Util.getLeadingSpaces(codeLine);
	      return leadingSpaces.concat("public void setUp() {");
	    } 
		else {
	      return null;
	    }
	  }

}
