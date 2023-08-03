package com.test.aadetector;

import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JsonGenerationExample {
    public static void main(String[] args) {
        // Check if the required arguments are provided
        if (args.length < 2) {
            System.out.println("Usage: java JsonGenerationExample <filename> <testsmell>");
            return;
        }

        // Get the filename and test smell from command line arguments
        String filename = args[0];
        String testSmell = args[1];

        // Read the file content
        String fileContent = readFileContent(filename);

        // Perform test smell detection logic and find the lines with smells
        int[] linesWithSmells = detectTestSmells(fileContent, testSmell);

        // Create an instance of the JSON structure
        JsonStructure jsonStructure = new JsonStructure();
        jsonStructure.setOriginalText(fileContent);
        jsonStructure.setLinesWithSmells(linesWithSmells);

        // Convert the JSON structure to JSON string using Gson
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonStructure);

        // Print the JSON string
        System.out.println(jsonString);
    }

    // Helper method to read file content as a string
    private static String readFileContent(String filename) {
        try {
            Path filePath = Paths.get(filename);
            List<String> lines = Files.readAllLines(filePath);
            return String.join("\n", lines);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Helper method to perform test smell detection logic
    private static int[] detectTestSmells(String fileContent, String testSmell) {
        // Implement your test smell detection logic here
        // Return the line numbers where the test smell is found
        // For simplicity, this example returns a hardcoded array
        return new int[]{2, 5, 8};
    }

    // Define the JSON structure class
    static class JsonStructure {
        private String originalText;
        private int[] linesWithSmells;

        public String getOriginalText() {
            return originalText;
        }

        public void setOriginalText(String originalText) {
            this.originalText = originalText;
        }

        public int[] getLinesWithSmells() {
            return linesWithSmells;
        }

        public void setLinesWithSmells(int[] linesWithSmells) {
            this.linesWithSmells = linesWithSmells;
        }
    }
}
