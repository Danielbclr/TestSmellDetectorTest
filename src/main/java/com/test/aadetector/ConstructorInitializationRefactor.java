package com.test.aadetector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstructorInitializationRefactor {
	
	public static String refactor(int line) {
		line--;
		
        // Specify the file path of the test file to refactor
		String filePath = System.getProperty("user.dir") + "\\output.java";

        // Read the test file
        List<String> lines = Util.readFile(filePath);

        // Perform test smell refactoring
        lines.set(line, changeLine(lines.get(line)));

        String result = Util.listToCode(lines);
        
        if(result == null) {
        	return null;
        }
        
        Util.writeStringToFile(result);
        
        System.out.println(result);
        
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
