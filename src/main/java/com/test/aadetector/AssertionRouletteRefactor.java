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

	public void run() {
        // Specify the file path of the test file to refactor
//        String mFilePath = filePath;

        // Read the test file
        List<String> lines = readFile(filePath);

        // Perform test smell refactoring
        List<String> refactoredLines = refactorTestSmells(lines);

        // Print the refactored test file to the console
        for (String line : refactoredLines) {
            System.out.println(line);
        }
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

}
