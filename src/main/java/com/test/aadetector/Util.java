package com.test.aadetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.gson.Gson;

public class Util {

	public static boolean isValidTestMethod(MethodDeclaration n) {
		boolean valid = false;

		if (!n.getAnnotationByName("Ignore").isPresent()) {
			//only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
			if (n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test")) {
				//must be a public method
				if (n.getModifiers().contains(Modifier.publicModifier())) {
					valid = true;
				}
			}
		}

		return valid;
	}

	public static boolean isValidSetupMethod(MethodDeclaration n) {
		boolean valid = false;

		if (!n.getAnnotationByName("Ignore").isPresent()) {
			//only analyze methods that either have a @Before annotation (Junit 4) or the method name is 'setUp'
			if (n.getAnnotationByName("Before").isPresent() || n.getNameAsString().equals("setUp")) {
				//must be a public method
				if (n.getModifiers().contains(Modifier.publicModifier())) {
					valid = true;
				}
			}
		}

		return valid;
	}

	public static boolean isInt(String s)
	{
		try
		{ int i = Integer.parseInt(s); return true; }

		catch(NumberFormatException er)
		{ return false; }
	}

	public static boolean isNumber(String str) {
		try {
			double v = Double.parseDouble(str);
			return true;
		} catch (NumberFormatException nfe) {
		}
		return false;
	}

	public static List<String> codeToList(String filePath) {
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

	public static String listToCode(List<String> code) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : code) {
			stringBuilder.append(line).append(System.lineSeparator());
		}

		String result = stringBuilder.toString().trim();
		return result;
	}

	public static void writeStringToFile(String content) {
		String filePath = System.getProperty("user.dir") + "\\output.java";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
			// Handle the exception as needed (e.g., log it, throw it, etc.)
		}
	}

	public static List<String> readFile(String filePath) {
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

	public static String getLeadingSpaces(String string) {
		String leading_spaces = "";
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) != ' ' && string.charAt(i) != '\t') {
				break;
			} else {
				leading_spaces += string.charAt(i);
			}
		}
		return leading_spaces;
	}

	private static String extractMethodName(String line) {
		// Extract method name from the method declaration line
		Pattern methodNamePattern = Pattern.compile("public void (\\w+)\\s*\\(.*\\)\\s*\\{?");
		Matcher matcher = methodNamePattern.matcher(line);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "Unknown";
	}

	public static int getStatementEndIndex(List<String> codeLines, int startIndex) {
		Stack<Character> stack = new Stack<>();
		int currentIndex = startIndex;

		// Start searching from the startIndex and continue until the end of the codeLines
		while (currentIndex < codeLines.size()) {
			String line = codeLines.get(currentIndex);

			for (char ch : line.toCharArray()) {
				if (ch == '{') {
					stack.push(ch);
				} else if (ch == '}') {
					if (stack.isEmpty()) {
						// Found the corresponding closing brace for the if statement
						return currentIndex + 1;
					} else {
						stack.pop();
					}
				}
			}

			// Move to the next line
			currentIndex++;
		}

		// If no matching closing brace is found, return -1 to indicate an error.
		return -1;
	}

	public static int findMatchingBracket( List<String> codeLines, int line) {
		Stack<Character> stack = new Stack<>();
		for (int index = line; index < codeLines.size(); index++) {
			for(int i = 0; i < codeLines.get(index).trim().length(); i++) {
				char c = codeLines.get(index).trim().charAt(i);
				if (c == '(' || c == '{' || c == '[') {
					stack.push(c);
				} else if (c == ')' || c == '}' || c == ']') {
					if (stack.isEmpty()) {
						return -1;
					}
					char top = stack.pop();
					if (c != matchingBracket(top)) {
						return -1;
					}
				}
			}
		}
		if (!stack.isEmpty()) {
			return -1;
		}
		return line;
	}

	private static char matchingBracket(char c) {
		switch (c) {
		case '(': return ')';
		case '{': return '}';
		case '[': return ']';
		default: return '\0';
		}
	}

	private static boolean checkForAsserts( List<String> code, int begin, int end) {
		for(int i = begin; i < end; i++) {
			if(code.get(i).trim().startsWith("assert")) {
				return true;
			}
		}
		return false;
	}
	public static int findEndBracket( List<String> codeLines, int line) {
		Stack<Character> stack = new Stack<>();
		for (int index = line; index < codeLines.size(); index++) {
			for(int i = 0; i < codeLines.get(index).trim().length(); i++) {
				char c = codeLines.get(index).trim().charAt(i);
				if (c == '{') {
					stack.push(c);
				} else if (c == '}') {
					if (stack.isEmpty()) {
						return -1;
					}
					char top = stack.pop();
					if (c != matchingBracket(top)) {
						return -1;
					}
				}
			}
		}
		if (!stack.isEmpty()) {
			return -1;
		}
		return line;
	}

	public static int findClosingBracket(List<String> lines, int line_number) {
		int open_brackets = 1;
		for (int i = line_number + 1; i < lines.size(); i++) {
			open_brackets += lines.get(i).trim().chars().filter(ch -> ch == '{').count();
			open_brackets -= lines.get(i).trim().chars().filter(ch -> ch == '}').count();
			if(open_brackets <= 0) {
				return i;
			}
		}
		return -1;
	}

	public static String getMethodName(String line) {
		// Extract method name from the method declaration line
		Pattern methodNamePattern = Pattern.compile("public void (\\w+)\\s*\\(.*\\)\\s*\\{?");
		Matcher matcher = methodNamePattern.matcher(line);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "Unknown";
	}
	
	private static String toJson(List<TestSmell> testSmellList) {
		Gson gson = new Gson();
		String json = gson.toJson(testSmellList);
		return json;
	}

}
