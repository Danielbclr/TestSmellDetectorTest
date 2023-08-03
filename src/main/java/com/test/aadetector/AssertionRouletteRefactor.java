package com.test.aadetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AssertionRouletteRefactor {
	
	public String filePath;
	
    public AssertionRouletteRefactor(String path) {
		filePath = path;
	}

	public String run() {
        // Specify the file path of the test file to refactor
//        String mFilePath = filePath;

        // Read the test file
        List<String> lines = readFile(filePath);

        // Perform test smell refactoring
        List<String> refactoredLines = refactorTestSmells(lines);

        // Print the refactored test file to the console
//        for (String line : refactoredLines) {
//            System.out.println(line);
//        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : refactoredLines) {
        	stringBuilder.append(line).append(System.lineSeparator());
        }

        String result = stringBuilder.toString().trim();
        
//        System.out.print(result);
        
        return result;
    }

    private static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    private static List<String> refactorTestSmells(List<String> lines) {
        List<String> refactoredLines = new ArrayList<>();

        // Refactor Assertion Roulette test smell
        boolean hasAssertion = false;
        for (String line : lines) {
            if (line.contains("assert")) {
                if (!hasAssertion) {
                    // Add the first assertion found
                    refactoredLines.add(line);
                    hasAssertion = true;
                } else {
                    // Replace additional assertions with explanatory comments
                    refactoredLines.add("// Multiple assertions removed");
                }
            } else {
                refactoredLines.add(line);
            }
        }

        return refactoredLines;
    }
    
    public static String addLazyAssertionMessage(int line) {
    	String filePath = System.getProperty("user.dir") + "\\output.java";
		List<String> code = Util.codeToList(filePath);
		
		String codeLine = code.get(line).trim();
		
        // Check if the line contains an assert call
        if (codeLine.contains("assert")) {
            // Check if the line already contains a Lazy Assertion Message
            if (codeLine.contains("() ->")) {
                // If the line already has a Lazy Assertion Message, return the line as is
                return Util.listToCode(code);
            } else {
                // Extract the assertion condition from the line
            	String lazyMessage = "Add assertion message";
                
                // Generate the Lazy Assertion Message
                String lazyAssertionMessage = "() -> \"" + lazyMessage + "\"";
                
                while(line < code.size()) {
                	if(codeLine.endsWith(";")){
                		break;
                	}
                	else {
                		line++;
                		codeLine = code.get(line).trim();
                	}
                }
                
                String space = Util.getLeadingSpaces(code.get(line));

                // Add the Lazy Assertion Message to the line
                String modifiedLine = space + codeLine.substring(0, codeLine.lastIndexOf(")")) + ", " + lazyAssertionMessage + ");";
                
                code.set(line, modifiedLine);

                return  Util.listToCode(code);
            }
        } else {
            // If the line doesn't contain an assert call, return it as is
            return Util.listToCode(code);
        }
    }


}
